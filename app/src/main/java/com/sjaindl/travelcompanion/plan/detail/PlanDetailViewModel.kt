package com.sjaindl.travelcompanion.plan.detail

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = PlanDetailViewModel.PlanDetailViewModelFactory::class)
class PlanDetailViewModel @AssistedInject constructor(
    @Assisted private val planName: String,
    private val dataRepository: DataRepository,
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {
    sealed class State {
        data object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan, val bitmap: Bitmap?) : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val pin by lazy {
        dataRepository.singlePin(planName)
    }

    fun loadPlan() {
        fireStoreUtils.loadPlan(
            planName = planName,
            onLoaded = { plan, bitmap ->
                _state.value = State.Loaded(plan = plan, bitmap = bitmap)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            },
        )
    }

    fun locationData(): MapLocationData {
        val zoom = Constants.Settings.zoomLevelDetail
        val latitude = pin?.latitude?.toFloat() ?: MapLocationData.default.latitude
        val longitude = pin?.longitude?.toFloat() ?: MapLocationData.default.longitude
        return MapLocationData(latitude = latitude, longitude = longitude, radius = zoom)
    }

    @AssistedFactory
    interface PlanDetailViewModelFactory {
        fun create(
            planName: String
        ): PlanDetailViewModel
    }
}
