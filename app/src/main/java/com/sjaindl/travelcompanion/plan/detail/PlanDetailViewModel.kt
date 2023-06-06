package com.sjaindl.travelcompanion.plan.detail

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlanDetailViewModel(private val planName: String, private val dataRepository: DataRepository) : ViewModel() {
    sealed class State {
        object Loading : State()

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
        // TODO: improve reloading (when changing dates)
        //if (state.value is State.Loaded) return // already loaded

        FireStoreUtils.loadPlan(
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

    class PlanDetailViewModelFactory(private val plan: String, private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlanDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlanDetailViewModel(planName = plan, dataRepository = dataRepository) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
