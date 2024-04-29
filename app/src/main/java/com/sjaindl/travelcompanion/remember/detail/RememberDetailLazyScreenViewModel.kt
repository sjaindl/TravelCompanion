package com.sjaindl.travelcompanion.remember.detail

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.sjaindl.travelcompanion.util.FireStoreUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@HiltViewModel(assistedFactory = RememberDetailLazyScreenViewModel.RememberDetailLazyScreenViewModelFactory::class)
class RememberDetailLazyScreenViewModel @AssistedInject constructor(
    @Assisted private val planName: String,
    private val fireStoreUtils: FireStoreUtils,
) : ViewModel() {
    sealed class State {
        data object InitialOrDone : State()

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
            fireStoreUtils.deletePhoto(
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

    @AssistedFactory
    interface RememberDetailLazyScreenViewModelFactory {
        fun create(
            planName: String,
        ): RememberDetailLazyScreenViewModel
    }
}
