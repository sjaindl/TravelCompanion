package com.sjaindl.travelcompanion.plan

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class ChangeDateViewModel(private val planName: String) : ViewModel() {
    sealed class State {
        object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan) : State()

        object Done : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val tag = "AddPlanViewModel"

    fun loadPlan() {
        FireStoreUtils.loadPlan(
            planName = planName,
            onLoaded = { plan, _ ->
                _state.value = State.Loaded(plan = plan)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            },
            withImageRef = false,
        )
    }

    fun changeDate(
        planName: String,
        startDate: Date,
        endDate: Date,
    ) {
        FireStoreUtils.updatePlanDates(
            planName = planName,
            startDate = startDate,
            endDate = endDate,
            onSuccess = {
                _state.value = State.Done
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            }
        )
    }

    class ChangeDateViewModelFactory(private val planName: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangeDateViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ChangeDateViewModel(planName = planName) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
