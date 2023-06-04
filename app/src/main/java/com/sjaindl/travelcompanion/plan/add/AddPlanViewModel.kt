package com.sjaindl.travelcompanion.plan.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreConstants
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.Date

class AddPlanViewModel(private val dataRepository: DataRepository) : ViewModel() {
    sealed class State {
        object Loading : State()

        data class Error(val exception: Exception?) : State()

        data class Info(val stringRes: Int) : State()

        data class LoadedPlaces(val places: List<String>) : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val fireStoreDbReference by lazy {
        FireStoreClient.userReference().collection(FireStoreConstants.Collections.plans)
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
        val plan = Plan(
            name = name,
            pinName = pinName,
            startDate = startDate,
            endDate = endDate,
            imagePath = null,
        )

        //TODO: check whether plan already exists and ask if user wants to override
        persistPlan(plan = plan, completion = completion)
    }

    private fun persistPlan(plan: Plan, completion: () -> Unit) {
        val data = mapOf(
            FireStoreConstants.Ids.Plan.name to plan.name,
            FireStoreConstants.Ids.Plan.pinName to plan.pinName,
            FireStoreConstants.Ids.Plan.startDate to plan.startDate,
            FireStoreConstants.Ids.Plan.endDate to plan.endDate
        )

        FireStoreClient.addData(collectionReference = fireStoreDbReference, documentName = plan.pinName, data = data) { exception ->
            if (exception != null) {
                _state.value = State.Error(exception)
            } else {
                completion()
            }
        }
    }

    class AddPlanViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddPlanViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddPlanViewModel(dataRepository) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
