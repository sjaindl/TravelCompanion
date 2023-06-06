package com.sjaindl.travelcompanion.remember.detail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sjaindl.travelcompanion.util.FireStoreUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class RememberDetailLazyScreenViewModel(private val planName: String) : ViewModel() {
    sealed class State {
        object InitialOrDone : State()

        data class Info(@StringRes val stringRes: Int) : State()

        data class Error(val exception: Exception?) : State()
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.InitialOrDone)
    var state = _state.asStateFlow()

    private val _showDialog: MutableStateFlow<LoadedPhoto?> = MutableStateFlow(null)
    val showDialog = _showDialog.asStateFlow()

    fun onDelete() {
        val url = showDialog.value?.url
        val documentId = showDialog.value?.documentId

        _showDialog.value = null

        if (url != null && documentId != null) {
            FireStoreUtils.deletePhoto(
                planName = planName,
                url = url,
                documentId = documentId,
                onSuccess = {
                    Timber.d("Successfully deleted image with id $documentId")
                    _state.value = State.InitialOrDone
                },
                onError = {
                    _state.value = State.Error(it)
                },
                onInfo = {
                    _state.value = State.Info(it)
                }
            )
        }
    }

    fun onDismiss() {
        _showDialog.value = null
    }

    fun clickedOnImage(photo: LoadedPhoto) {
        _showDialog.value = photo
    }

    class RememberDetailLazyScreenViewModelFactory(private val planName: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RememberDetailLazyScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RememberDetailLazyScreenViewModel(planName = planName) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
