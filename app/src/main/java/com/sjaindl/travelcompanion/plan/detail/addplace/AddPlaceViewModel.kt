package com.sjaindl.travelcompanion.plan.detail.addplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.api.google.asMap
import com.sjaindl.travelcompanion.api.google.asPlannable
import com.sjaindl.travelcompanion.plan.Plan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class AddPlaceViewModel(private val planName: String) : ViewModel() {

    sealed class State {
        object Initial : State()

        data class Error(val exception: Exception?) : State()

        data class PlanReady(val plan: Plan) : State()

        object Finished : State()
    }

    val tag = "PlanDetailViewModel"

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Initial)
    var state = _state.asStateFlow()

    private val fireStoreDbReference by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans).document(planName)
    }

    private val stepSize = 0.1

    val minDistanceKm = 1.0
    val maxDistanceKm = 50.0

    val steps: Int
        get() = ((maxDistanceKm - minDistanceKm) / stepSize - 1).toInt()

    fun persistPlace(place: GooglePlace, placeType: GooglePlaceType?) {
        // Easier, but causes null values being written to Firebase:
        // val serialized = Json.encodeToString(place)

        val data = place.asMap()

        val placeTypeCollection = getPlaceTypeReference(placeType = placeType)
        val task = placeTypeCollection?.document(place.placeId)?.set(data)
        task?.addOnSuccessListener {
            addPlaceToPlan(place = place, placeType = placeType)
        }?.addOnFailureListener {
            _state.value = State.Error(it)
        }
    }
    
    fun fetchPlan() {
        if (state.value is State.PlanReady || state.value is State.Finished) return // already loaded

        fireStoreDbReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.addOnFailureListener {
                    Timber.e(task.exception)
                    _state.value = State.Error(task.exception)
                }.addOnSuccessListener { document ->
                    Timber.tag(tag).d("${document.id} => {$document.data}")

                    val name = document.getString(FireStoreConstants.Ids.Plan.name)
                    val pinName = document.getString(FireStoreConstants.Ids.Plan.pinName)
                    val startDate = document.getTimestamp(FireStoreConstants.Ids.Plan.startDate)?.toDate()
                    val endDate = document.getTimestamp(FireStoreConstants.Ids.Plan.endDate)?.toDate()

                    val imageRef = document.getString(FireStoreConstants.Ids.Plan.imageReference)
                    val storageImageRef = imageRef?.let { FirebaseStorage.getInstance().getReferenceFromUrl(it) }
                    val downloadFromUrlTask = storageImageRef?.downloadUrl

                    if (name == null || pinName == null || startDate == null || endDate == null) return@addOnSuccessListener

                    val plan = Plan(
                        name = name,
                        pinName = pinName,
                        startDate = startDate,
                        endDate = endDate,
                        imagePath = null,
                    )

                    if (downloadFromUrlTask == null) {
                        Timber.tag(tag).d("Add plan without image: $name")
                        _state.value = State.PlanReady(plan = plan)
                    } else {
                        downloadFromUrlTask.addOnSuccessListener { imagePath ->
                            Timber.tag(tag).d("fetched imagePath: $imagePath for $name")
                            plan.imagePath = imagePath
                            _state.value = State.PlanReady(plan = plan)
                        }.addOnFailureListener {
                            _state.value = State.Error(it)
                        }
                    }
                }
            } else {
                Timber.e(task.exception)
                _state.value = State.Error(task.exception)
            }
        }
    }

    private fun addPlaceToPlan(place: GooglePlace, placeType: GooglePlaceType?) {
        val plan = (state.value as? State.PlanReady)?.plan ?: return

        when (placeType) {
            GooglePlaceType.Lodging -> plan.hotels.add(place.asPlannable())
            GooglePlaceType.Restaurant -> plan.restaurants.add(place.asPlannable())
            else -> plan.attractions.add(place.asPlannable())
        }

        _state.value = State.Finished
    }

    private fun getPlaceTypeReference(placeType: GooglePlaceType?): CollectionReference? {
        val plan = (state.value as? State.PlanReady)?.plan ?: return null

        return when (placeType) {
            GooglePlaceType.Lodging -> plan.fireStoreHotelDbReference
            GooglePlaceType.Restaurant -> plan.fireStoreRestaurantDbReference
            else -> plan.fireStoreAttractionDbReference
        }
    }

    class AddPlaceViewModelFactory(private val planName: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddPlaceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddPlaceViewModel(planName = planName) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
