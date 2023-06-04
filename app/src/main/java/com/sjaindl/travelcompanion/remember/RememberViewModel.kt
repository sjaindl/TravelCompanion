package com.sjaindl.travelcompanion.remember

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.plan.PlanUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class RememberViewModel : ViewModel() {
    sealed class State {
        object Loading : State()

        data class Error(val exception: Exception) : State()

        data class Info(val stringRes: Int) : State()

        object Finished : State()
    }

    val tag = "RememberViewModel"

    private val _rememberTrips = mutableStateListOf<Plan>()
    private val _rememberTripsFlow = MutableStateFlow(_rememberTrips)
    val rememberTripsFlow: StateFlow<List<Plan>> = _rememberTripsFlow.asStateFlow()

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    fun fetchPlans() {
        if (!_rememberTrips.isEmpty()) return // already loaded

        PlanUtils.loadPlans(
            onLoaded = {
                addPlan(it)
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
        //remember photos should be storable from the beginning of the trip
        if (plan.startDate >= Date()) {
            _rememberTrips.add(plan)
        }
    }
}
