package com.sjaindl.travelcompanion.explore.details.photos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL

@HiltViewModel(assistedFactory = ExploreDetailPhotosViewModelFactory::class)
class ExploreDetailPhotosViewModel @AssistedInject constructor(
    private val fireStoreUtils: FireStoreUtils,
    @Assisted pinId: Long,
    @Assisted isChoosePlanImageMode: Boolean,
    dataRepository: DataRepository,
) : ViewModel() {

    sealed class State {
        data object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan, val bitmap: Bitmap?) : State()

        data object PhotoChosen : State()
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    init {
        if (isChoosePlanImageMode) {
            loadPlan()
        }
    }

    fun persistPlan(bitmap: ImageBitmap?, url: String?) = viewModelScope.launch(Dispatchers.IO) {
        val loaded = state.value as? State.Loaded ?: return@launch

        _state.value = State.Loading

        var image: Bitmap? = null
        if (bitmap != null) {
            image = bitmap.asAndroidBitmap()
        } else if (url != null) {
            val byteArray = URL(url).openStream().readBytes()
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        if (image == null) return@launch

        fireStoreUtils.persistPlanPhoto(
            plan = loaded.plan,
            image = image,
            onSuccess = {
                _state.value = State.PhotoChosen
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = {
                _state.value = State.Error(it)
            }
        )
    }

    private fun loadPlan() {
        val planName = pin?.name ?: return

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
}

@AssistedFactory
interface ExploreDetailPhotosViewModelFactory {
    fun create(
        pinId: Long,
        isChoosePlanImageMode: Boolean,
    ): ExploreDetailPhotosViewModel
}
