package com.sjaindl.travelcompanion.api.firestore

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.sjaindl.travelcompanion.Constants
import timber.log.Timber

object FireStoreRemoteConfig {
    var photoResizingHeight = Constants.RemoteConfig.LocalDefaultValues.photoResizingHeight
        private set

    var photoResizingWidth = Constants.RemoteConfig.LocalDefaultValues.photoResizingWidth
        private set

    private val inAppDefaults = mapOf(
        Constants.RemoteConfig.Keys.photoResizingHeight to Constants.RemoteConfig.LocalDefaultValues.photoResizingHeight,
        Constants.RemoteConfig.Keys.photoResizingWidth to Constants.RemoteConfig.LocalDefaultValues.photoResizingWidth,
    )

    init {
        //Set in-app default values
        Firebase.remoteConfig.setDefaultsAsync(inAppDefaults)
    }

    fun activateFetched() {
        Firebase.remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchRemoteConfigValues()
                } else {
                    Timber.tag("FirestoreRemoteConfig").d("Couldn't fetch RemoteConfig. Applying default values.")
                }
            }
    }

    private fun fetchRemoteConfigValues() {
        photoResizingHeight = Firebase.remoteConfig.getLong(Constants.RemoteConfig.Keys.photoResizingHeight).toInt()
        photoResizingWidth = Firebase.remoteConfig.getLong(Constants.RemoteConfig.Keys.photoResizingWidth).toInt()
    }
}
