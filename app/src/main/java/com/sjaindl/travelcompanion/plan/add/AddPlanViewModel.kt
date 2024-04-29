package com.sjaindl.travelcompanion.plan.add

import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddPlanViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val fireStoreUtils: FireStoreUtils,
    private val fireStoreClient: FireStoreClient,
) : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Error(val exception: Exception?) : State()

        data class Info(val stringRes: Int) : State()

        data class LoadedPlaces(val places: List<String>) : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val fireStoreDbReference by lazy {
        fireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
    }

    val tag = "AddPlanViewModel"

    fun fetchPlans() {
        fireStoreDbReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val places = mutableListOf<String>()
                for (document in task.result) {
                    Timber.tag(tag).d("${document.id} => {$document.data}")

                    val name = document.getString(FireStoreConstants.Ids.Plan.name)

                    name?.let {
                        places.add(it)
                    }
                }

                dataRepository.allPins()
                    .mapNotNull { it.name }
                    .forEach {
                        if (!places.contains(it)) {
                            places.add(it)
                        }
                    }

                _state.value = State.LoadedPlaces(places.sorted())
            } else {
                val exception = task.exception
                if (exception != null) {
                    _state.value = State.Error(exception)
                } else {
                    _state.value = State.Info(R.string.cancelled)
                }
            }
        }
    }

    fun addPlan(name: String, pinName: String, startDate: Date, endDate: Date, completion: () -> Unit) {
        if (fireStoreUtils.planExists(planName = name)) {
            _state.value = State.Info(R.string.planAlreadyExists)
            return
        }

        fireStoreUtils.addPlan(
            name = name,
            pinName = pinName,
            startDate = startDate,
            endDate = endDate,
            onError = { exception ->
                _state.value = State.Error(exception)
            },
            completion = completion,
        )
    }
}
