package com.sjaindl.travelcompanion.remember.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RememberDetailViewModel(
    private val planName: String,
) : ViewModel() {

    companion object {
        const val tag = "RememberDetailViewModel"
    }

    sealed class State {
        object Loading : State()

        data class Error(val exception: Exception) : State()

        data class Info(val stringRes: Int) : State()

        data class LoadedPhotos(val photos: List<RememberPhoto>) : State()
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    fun loadPhotos() {
        FireStoreUtils.loadPhotoPaths(
            planName = planName,
            onLoaded = {
                _state.value = State.LoadedPhotos(photos = it)
            },
            onInfo = {
                _state.value = State.Info(it)
            },
            onError = { exception ->
                _state.value = State.Error(exception)
            },
        )
    }

    fun setError(exception: Exception) {
        _state.value = State.Error(exception)
    }

    fun setInfo(stringRes: Int) {
        _state.value = State.Info(stringRes)
    }

    fun addPhoto(photo: RememberPhoto) {
        val curPhotos = ((state.value as? State.LoadedPhotos)?.photos ?: emptyList()).toMutableList()
        curPhotos.add(photo)
        _state.value = State.LoadedPhotos(curPhotos)
    }
}

class RememberDetailViewModelFactory(private val planName: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RememberDetailViewModel(planName = planName) as T
}
