package com.sjaindl.travelcompanion

import android.app.Application
import com.google.firebase.FirebaseApp
import com.sjaindl.travelcompanion.api.firestore.FireStoreRemoteConfig

class TCApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FireStoreRemoteConfig.activateFetched()
    }
}
