package com.sjaindl.travelcompanion.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.profile.secure.SecureRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecureRequestViewModel @Inject constructor(
    private val secureRequestUseCase: SecureRequestUseCase,
) : ViewModel() {
    sealed class State {
        data object Initial : State()

        data class Error(val exception: Throwable) : State()

        data class Finished(val response: String) : State()
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Initial)
    var state = _state.asStateFlow()

    fun performSecureRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            secureRequestUseCase.execute()
                .onSuccess {
                    _state.value = State.Finished(it)
                }.onFailure {
                    _state.value = State.Error(exception = it)
                }
        }
    }
}
