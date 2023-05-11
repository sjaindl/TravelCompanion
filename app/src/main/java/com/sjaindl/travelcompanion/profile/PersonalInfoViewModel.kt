package com.sjaindl.travelcompanion.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class PersonalInfoViewModel : ViewModel() {
    private val currentUser = FirebaseAuth.getInstance().currentUser

    var userName = currentUser?.displayName ?: "Anonymous"
    var email = currentUser?.email
}
