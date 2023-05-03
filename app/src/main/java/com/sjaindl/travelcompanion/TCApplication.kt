package com.sjaindl.travelcompanion

import android.app.Application
import com.google.firebase.FirebaseApp

class TCApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }
}
