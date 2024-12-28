package com.sjaindl.travelcompanion.remember

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RememberViewModel @Inject constructor(
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Error(val exception: Exception) : State()

        data class Info(val stringRes: Int) : State()

        data object Finished : State()
    }

    val tag = "RememberViewModel"

    private val _rememberTrips = mutableStateListOf<Plan>()
    private val _rememberTripsFlow = MutableStateFlow(_rememberTrips)
    val rememberTripsFlow: StateFlow<List<Plan>> = _rememberTripsFlow.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    fun fetchPlans() {
        if (!_rememberTrips.isEmpty()) return // already loaded

        fireStoreUtils.loadPlans(
            onLoaded = { plan ->
                plan?.let {
                    addPlan(plan = it)
                }
                _state.value = State.Finished
            },
            onError = {
                _state.value = State.Error(it)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
        )
    }

    private fun addPlan(plan: Plan) {
        //remember photos should be storeable from the beginning of the trip
        if (Date() >= plan.startDate) {
            _rememberTrips.add(plan)
        }
    }

    fun bitmapForPlan(planName: String) = fireStoreUtils.bitmapForPlan(planName = planName)
}
