package com.sjaindl.travelcompanion

import android.app.AppOpsManager
import android.app.Application
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.sjaindl.travelcompanion.api.firestore.FireStoreRemoteConfig
import timber.log.Timber
import timber.log.Timber.Forest.plant

class TCApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        FireStoreRemoteConfig.activateFetched()

        plant(Timber.DebugTree())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupAppOpsCallback()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupAppOpsCallback() {
        val appOpsCallback = object : AppOpsManager.OnOpNotedCallback() {
            override fun onNoted(syncNotedAppOp: SyncNotedAppOp) {
                logPrivateDataAccess(
                    opCode = syncNotedAppOp.op,
                    attributionTag = syncNotedAppOp.attributionTag.orEmpty(),
                )
            }

            override fun onSelfNoted(syncNotedAppOp: SyncNotedAppOp) {
                logPrivateDataAccess(
                    opCode = syncNotedAppOp.op,
                    attributionTag = syncNotedAppOp.attributionTag.orEmpty(),
                )
            }

            override fun onAsyncNoted(asyncNotedAppOp: AsyncNotedAppOp) {
                logPrivateDataAccess(
                    opCode = asyncNotedAppOp.op,
                    attributionTag = asyncNotedAppOp.attributionTag.orEmpty(),
                    message = asyncNotedAppOp.message,
                )
            }

            private fun logPrivateDataAccess(
                opCode: String,
                attributionTag: String,
                message: String? = null,
            ) {
                Timber.tag("TCApplication")
                    .d("Private data accessed.\nOperation: $opCode\nAttribution Tag: $attributionTag\nMessage:${message.orEmpty()}")
            }
        }

        val appOpsManager = getSystemService(AppOpsManager::class.java) as AppOpsManager
        appOpsManager.setOnOpNotedCallback(mainExecutor, appOpsCallback)
    }
}
