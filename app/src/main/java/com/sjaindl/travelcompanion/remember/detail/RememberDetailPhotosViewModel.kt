package com.sjaindl.travelcompanion.remember.detail

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RememberDetailPhotosViewModel : ViewModel() {

    sealed class State {
        object Loading : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()

        object Loaded : State()
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    private val _loadedPhotos = mutableStateListOf<LoadedPhoto>()
    private val _loadedPhotosFlow = MutableStateFlow(_loadedPhotos)
    val loadedPhotosFlow: StateFlow<List<LoadedPhoto>> = _loadedPhotosFlow.asStateFlow()

    fun loadBitmaps(photos: List<RememberPhoto>) {
        if (photos.isEmpty()) {
            _state.value = State.Loaded
            return
        }

        _state.value = State.Loading

        _loadedPhotos.clear()

        photos.forEach { photo ->
            FireStoreUtils.loadImageIfAvailable(
                imagePath = photo.url,
                onLoaded = { bitmap ->
                    _loadedPhotos.add(
                        LoadedPhoto(
                            url = photo.url,
                            documentId = photo.documentId,
                            bitmap = bitmap,
                        )
                    )
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
