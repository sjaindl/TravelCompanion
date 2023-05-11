package com.sjaindl.travelcompanion.plan

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import timber.log.Timber
import java.util.*

class PlanViewModel(private val dataRepository: DataRepository) : ViewModel() {
    sealed class State {
        object Loading : State()

        data class Error(val exception: Exception?) : State()

        object Finished : State()
    }

    private val fireStoreDbReference by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    val tag = "PlanHomeScreen"

    private val _upcomingTrips = mutableStateListOf<Plan>()
    private val _upcomingTripsFlow = MutableStateFlow(_upcomingTrips)
    val upcomingTripsFlow: StateFlow<List<Plan>> = _upcomingTripsFlow.asStateFlow()

    private val _pastTrips = mutableStateListOf<Plan>()
    private val _pastTripsFlow = MutableStateFlow(_pastTrips)
    val pastTripsFlow: StateFlow<List<Plan>> = _pastTripsFlow.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val sessionToken = randomStringByKotlinRandom(32)

    private val googleClient by lazy {
        TCInjector.googleClient
    }

    private val geoNamesClient by lazy {
        TCInjector.geoNamesClient
    }

    fun fetchPlans() {
        fireStoreDbReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    Timber.tag(tag).d("${document.id} => {$document.data}")

                    val name = document.getString(FireStoreConstants.Ids.Plan.name)
                    val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                    val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                    val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                    val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
                    val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }

                    storageImageRef?.downloadUrl?.addOnSuccessListener { imagePath ->
                        Timber.tag(tag).d("fetched imagePath: $imagePath")
                        if (name != null && pinName != null && startDate != null && endDate != null) {
                            val plan = Plan(name, pinName, startDate, endDate, imagePath)

                            // preload subdocuments of plan
                            plan.loadPlannables { exception ->
                                if (exception != null) {
                                    _state.value = State.Error(exception)
                                }
                            }

                            if (endDate > Date()) {
                                _upcomingTrips.add(plan)
                            } else {
                                _pastTrips.add(plan)
                            }

                            addPinIfNeeded(plan)
                        }
                    }?.addOnFailureListener {
                        _state.value = State.Error(it)
                    }
                }

                _state.value = State.Finished
            } else {
                _state.value = State.Error(task.exception)
                Timber.e(task.exception)
            }
        }
    }

    fun getPinId(name: String): Long? {
        return dataRepository.singlePin(name)?.id
    }

    fun onShow() {
        // TODO
    }

    fun onDelete(plan: Plan) {
        _state.value = State.Loading

        val documentRef = fireStoreDbReference.document(plan.pinName)
        val document = documentRef.get().result
        val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
        val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }

        documentRef.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d(tag, "Document successfully removed!")

                // delete plan photo in Firebase storage
                storageImageRef?.delete()?.addOnCompleteListener { imageTask ->
                    if (!imageTask.isSuccessful) {
                        _state.value = State.Error(imageTask.exception)
                    } else {
                        _state.value = State.Finished
                    }
                }

                // delete subdocuments from Firestore
                plan.deleteSubDocuments { exception ->
                    if (exception != null) {
                        _state.value = State.Error(exception)
                    } else {
                        _state.value = State.Finished
                    }
                }
            } else {
                _state.value = State.Error(task.exception)
            }
        }
    }

    private fun addPinIfNeeded(plan: Plan) = viewModelScope.launch {
        if (dataRepository.singlePin(plan.pinName) == null) {
            val autocompleteResult = googleClient.autocomplete(
                plan.pinName, sessionToken
            )

            val prediction = autocompleteResult?.predictions?.firstOrNull() ?: return@launch
            val placeId = prediction.placeId ?: return@launch

            val details = googleClient.placeDetail(placeId, sessionToken)
            val location = details.result.geometry.location
            val countryCode = geoNamesClient.fetchCountryCode(latitude = location.lat, longitude = location.lng)

            val component = details.result.addressComponents?.firstOrNull {
                it.types.contains("country")
            }

            dataRepository.insertPin(
                id = 0,
                address = details.result.formattedAddress,
                country = component?.longName,
                countryCode = countryCode,
                creationDate = Clock.System.now(),
                latitude = details.result.geometry.location.lat,
                longitude = details.result.geometry.location.lng,
                name = plan.pinName,
                phoneNumber = null,
                placeId = placeId,
                rating = null,
                url = details.result.url,
            )
        }
    }
}

class PlanViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlanViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
    }
}
