package com.sjaindl.travelcompanion.plan

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreClientObserver
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
import java.util.Date

class PlanViewModel(private val dataRepository: DataRepository) : ViewModel(), FireStoreClientObserver {
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

    init {
        FireStoreClient.addObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        FireStoreClient.removeObserver(this)
    }

    fun fetchPlans() {
        if (!_upcomingTrips.isEmpty() || !_pastTrips.isEmpty()) return // already loaded

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
                    val downloadFromUrlTask = storageImageRef?.downloadUrl

                    if (downloadFromUrlTask == null) {
                        Timber.tag(tag).d("Add plan without image: $name")
                        addPlan(name, pinName, startDate, endDate)
                    } else {
                        downloadFromUrlTask.addOnSuccessListener { imagePath ->
                            Timber.tag(tag).d("fetched imagePath: $imagePath for $name")
                            addPlan(name, pinName, startDate, endDate, imagePath)
                        }.addOnFailureListener {
                            _state.value = State.Error(it)
                        }
                    }
                }

                _state.value = State.Finished
            } else {
                _state.value = State.Error(task.exception)
                Timber.e(task.exception)
            }
        }
    }

    private fun addPlan(
        name: String?,
        pinName: String?,
        startDate: Date?,
        endDate: Date?,
        imagePath: Uri? = null,
    ) {
        if (name == null || pinName == null || startDate == null || endDate == null) return

        val plan = Plan(
            name = name,
            pinName = pinName,
            startDate = startDate,
            endDate = endDate,
            imagePath = imagePath
        )

        addPlan(plan, endDate)
    }

    private fun addPlan(plan: Plan, endDate: Date) {
        if (endDate > Date()) {
            _upcomingTrips.add(plan)
        } else {
            _pastTrips.add(plan)
        }

        addPinIfNeeded(plan)
    }

    fun getPinId(name: String): Long? {
        return dataRepository.singlePin(name)?.id
    }

    fun onShow() {
        // TODO
    }

    fun onDelete(plan: Plan) {
        _state.value = State.Loading

        _upcomingTrips.remove(plan)
        _pastTrips.remove(plan)

        plan.imagePath?.let {
            // delete plan photo in Firebase storage
            val storageImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(it.toString())
            storageImageRef.delete().addOnCompleteListener { imageTask ->
                if (!imageTask.isSuccessful) {
                    _state.value = State.Error(imageTask.exception)
                }
            }
        }

        val documentRef = fireStoreDbReference.document(plan.pinName)
        documentRef.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.d(tag, "Document successfully removed!")

                _state.value = State.Finished
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

    override fun didAddData(documentName: String) {
        Timber.tag(tag).d("Add new plan: $documentName")
        fireStoreDbReference.document(documentName).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val name = snapshot.getString(FireStoreConstants.Ids.Plan.name) ?: return@addOnSuccessListener
                val pinName = snapshot.getString(FireStoreConstants.Ids.Plan.pinName) ?: return@addOnSuccessListener
                val startDate = snapshot.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate() ?: return@addOnSuccessListener
                val endDate = snapshot.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate() ?: return@addOnSuccessListener

                val upcomingTripsIndex = _upcomingTrips.indexOfFirst { it.pinName == pinName }
                val pastTripsIndex = _pastTrips.indexOfFirst { it.pinName == pinName }

                if (upcomingTripsIndex != -1) {
                    val oriPlan = _upcomingTrips[upcomingTripsIndex]
                    val newPlan = Plan(
                        name = name,
                        pinName = pinName,
                        startDate = startDate,
                        endDate = endDate,
                        imagePath = oriPlan.imagePath,
                    )

                    if (endDate > Date()) {
                        _upcomingTrips[upcomingTripsIndex] = newPlan
                    } else {
                        _upcomingTrips.removeAt(upcomingTripsIndex)
                        _pastTrips.add(newPlan)
                    }

                } else if (pastTripsIndex != -1) {
                    val oriPlan = _pastTrips[pastTripsIndex]
                    val newPlan = Plan(
                        name = name,
                        pinName = pinName,
                        startDate = startDate,
                        endDate = endDate,
                        imagePath = oriPlan.imagePath,
                    )

                    if (endDate > Date()) {
                        _pastTrips.removeAt(pastTripsIndex)
                        _upcomingTrips.add(newPlan)
                    } else {
                        _pastTrips[pastTripsIndex] = newPlan
                    }

                } else {
                    addPlan(name, pinName, startDate, endDate)
                }
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
}
