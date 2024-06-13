package com.sjaindl.travelcompanion.profile.usericon

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserIconViewModel : ViewModel() {

    sealed class UserIconViewState {
        data object Initial : UserIconViewState()

        data class Content(val initials: String?) : UserIconViewState()
    }

    private var _userIconViewState = MutableStateFlow<UserIconViewState>(UserIconViewState.Initial)
    var userIconViewState = _userIconViewState.asStateFlow()

    init {
        registerListeners()
    }

    private fun registerListeners() {
        FirebaseAuth.getInstance().currentUser?.let {
            onCurrentUser(currentUser = it)
        }

        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            onCurrentUser(currentUser = auth.currentUser)
        }
    }

    private fun onCurrentUser(currentUser: FirebaseUser?) {
        val name = currentUser?.displayName
        val initials = name?.split(" ")?.take(n = 2)?.joinToString(separator = "") {
            (it.firstOrNull() ?: "Anonymous".first()).toString()
        }

        _userIconViewState.value = UserIconViewState.Content(initials = initials)
    }
}
