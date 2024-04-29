package com.sjaindl.travelcompanion.plan

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

@HiltViewModel(assistedFactory = ChangeDateViewModel.ChangeDateViewModelFactory::class)
class ChangeDateViewModel @AssistedInject constructor(
    @Assisted private val planName: String,
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan) : State()

        data object Done : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val tag = "AddPlanViewModel"

    fun loadPlan() {
        fireStoreUtils.loadPlan(
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
        )
    }

    fun changeDate(
        planName: String,
        startDate: Date,
        endDate: Date,
    ) {
        fireStoreUtils.updatePlanDates(
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

    @AssistedFactory
    interface ChangeDateViewModelFactory {
        fun create(
            planName: String
        ): ChangeDateViewModel
    }
}
