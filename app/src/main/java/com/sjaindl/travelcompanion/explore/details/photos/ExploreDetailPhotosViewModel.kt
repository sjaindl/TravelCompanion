package com.sjaindl.travelcompanion.explore.details.photos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.plan.Plan
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL

class ExploreDetailPhotosViewModel(
    dataRepository: DataRepository,
    pinId: Long,
) :
    ViewModel() {

    sealed class State {
        object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        data class Loaded(val plan: Plan, val bitmap: Bitmap?) : State()

        object PhotoChosen : State()
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    init {
        loadPlan()
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

        FireStoreUtils.persistPlanPhoto(
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
}

class ExploreDetailPhotosViewModelFactory(
    private val dataRepository: DataRepository,
    private val pinId: Long,
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ExploreDetailPhotosViewModel(dataRepository, pinId) as T
}
