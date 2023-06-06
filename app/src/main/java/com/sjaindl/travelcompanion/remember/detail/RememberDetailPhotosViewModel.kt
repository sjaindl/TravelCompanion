package com.sjaindl.travelcompanion.remember.detail

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RememberDetailPhotosViewModel(
    private val photos: List<RememberPhoto>,
) : ViewModel() {

    sealed class State {
        object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        object Loaded : State()
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val _bitmaps = mutableStateListOf<Bitmap>()
    private val _bitmapsFlow = MutableStateFlow(_bitmaps)
    val bitmapsFlow: StateFlow<List<Bitmap>> = _bitmapsFlow.asStateFlow()

    init {
        loadBitmaps()
    }

    private fun loadBitmaps() {
        if (photos.isEmpty()) {
            _state.value = State.Loaded
        }

        photos.forEach { photo ->
            FireStoreUtils.loadImageIfAvailable(
                imagePath = photo.url,
                onLoaded = { bitmap ->
                    _bitmaps.add(bitmap)
                    _state.value = State.Loaded // loaded at least first bitmap
                },
                onInfo = {
                    _state.value = State.Info(it)
                },
                onError = {
                    _state.value = State.Error(it)
                },
            )
        }
    }
}

class RememberDetailPhotosViewModelFactory(
    private val photos: List<RememberPhoto>,
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = RememberDetailPhotosViewModel(photos = photos) as T
}
