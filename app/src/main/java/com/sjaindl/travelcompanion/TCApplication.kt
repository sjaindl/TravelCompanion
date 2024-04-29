package com.sjaindl.travelcompanion

import android.app.AppOpsManager
import android.app.Application
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.appmattus.certificatetransparency.BasicAndroidCTLogger
import com.appmattus.certificatetransparency.CTPolicy
import com.appmattus.certificatetransparency.cache.AndroidDiskCache
import com.appmattus.certificatetransparency.installCertificateTransparencyProvider
import com.appmattus.certificatetransparency.loglist.LogListDataSourceFactory
import com.google.firebase.FirebaseApp
import com.sjaindl.travelcompanion.api.firestore.FireStoreRemoteConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class TCApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        FirebaseApp.initializeApp(this)
        FireStoreRemoteConfig.activateFetched()

        plant(Timber.DebugTree())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupAppOpsCallback()
        }

        installCertificateTransparency()
    }

    private fun installCertificateTransparency(
        ctPolicy: CTPolicy? = null,
        excludedDomains: List<String> = emptyList(),
        includedDomains: List<String> = emptyList(),
    ) {
        installCertificateTransparencyProvider {
            // Service providing log_list.json and log_list.sig byte data from the network
            setLogListService(
                logListService = LogListDataSourceFactory.createLogListService(baseUrl = "https://www.gstatic.com/ct/log_list/v3/")
            )

            // Caches the log list
            diskCache = AndroidDiskCache(context = applicationContext)

            // Default when null: https://github.com/GoogleChrome/CertificateTransparency/blob/master/ct_policy.md
            policy = ctPolicy

            // Determine if a failure to pass certificate transparency results in the connection being closed
            setFailOnError(failOnError = true)

            // Setup logging to logcat
            logger = BasicAndroidCTLogger(isDebugMode = BuildConfig.DEBUG)

            // Exclude any domains or subdomains
            -excludedDomains

            // Override the exclusion by including specific subdomains
            +includedDomains
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
