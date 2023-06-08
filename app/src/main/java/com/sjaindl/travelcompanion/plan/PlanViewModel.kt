package com.sjaindl.travelcompanion.plan

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
import com.sjaindl.travelcompanion.util.FireStoreUtils
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

        data class Info(val stringRes: Int) : State()

        object Finished : State()
    }

    private val fireStoreDbReference by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    val tag = "PlanViewModel"

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

        FireStoreUtils.loadPlans(
            onLoaded = { plan ->
                addPlan(plan = plan)
                _state.value = State.Finished
            },
            onError = {
                _state.value = State.Error(it)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            withImageRef = true,
        )
    }

    private fun addPlan(plan: Plan) {
        if (plan.endDate > Date()) {
            _upcomingTrips.add(plan)
        } else {
            _pastTrips.add(plan)
        }

        addPinIfNeeded(plan)
    }

    fun getPinId(name: String): Long? {
        return dataRepository.singlePin(name)?.id
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

        val planUtils = PlanUtilsFactory.getOrCreate(planName = plan.name)
        // delete subdocuments from Firestore
        // no auto deletion of subdocs: https://firebase.google.com/docs/firestore/manage-data/delete-data?hl=en
        planUtils.loadPlannables { exception ->
            if (exception != null) {
                _state.value = State.Error(exception)
            } else {
                planUtils.deleteSubDocuments(pinName = plan.pinName) { subDocException ->
                    if (subDocException != null) {
                        _state.value = State.Error(subDocException)
                    } else {
                        _state.value = State.Finished
                    }
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

        FireStoreUtils.loadPlan(
            planName = documentName,
            onLoaded = { newPlan, bitmap ->
                val upcomingTripsIndex = _upcomingTrips.indexOfFirst { it.pinName == newPlan.pinName }
                val pastTripsIndex = _pastTrips.indexOfFirst { it.pinName == newPlan.pinName }

                if (upcomingTripsIndex != -1) {
                    val oriPlan = _upcomingTrips[upcomingTripsIndex]
                    newPlan.imagePath = oriPlan.imagePath

                    if (newPlan.endDate > Date()) {
                        _upcomingTrips[upcomingTripsIndex] = newPlan
                    } else {
                        _upcomingTrips.removeAt(upcomingTripsIndex)
                        _pastTrips.add(newPlan)
                    }

                } else if (pastTripsIndex != -1) {
                    val oriPlan = _pastTrips[pastTripsIndex]
                    newPlan.imagePath = oriPlan.imagePath

                    if (newPlan.endDate > Date()) {
                        _pastTrips.removeAt(pastTripsIndex)
                        _upcomingTrips.add(newPlan)
                    } else {
                        _pastTrips[pastTripsIndex] = newPlan
                    }

                } else {
                    addPlan(newPlan)
                }
            },
            onError = {
                _state.value = State.Error(it)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            withImageRef = true,
        )
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
