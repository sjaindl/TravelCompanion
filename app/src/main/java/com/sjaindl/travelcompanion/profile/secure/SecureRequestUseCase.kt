package com.sjaindl.travelcompanion.profile.secure

import android.content.Context
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityToken
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenProvider
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenRequest
import com.sjaindl.travelcompanion.di.TCInjector
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class SecureRequestUseCase @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {

    private val secureRequestClient by lazy {
        TCInjector.secureRequestClient
    }

    private var integrityTokenProvider: StandardIntegrityTokenProvider? = null

    suspend fun execute(): Result<String> {
        val integrityTokenProvider = prepare()
        val requestHash = calculateRequestHash(inputString = "demoRequest")
        val standardIntegrityToken = requestIntegrityVerdict(integrityTokenProvider = integrityTokenProvider, requestHash = requestHash)

        return sendToServer(requestHash = requestHash, token = standardIntegrityToken.token())
    }

    suspend fun prepare(): StandardIntegrityTokenProvider {
        integrityTokenProvider?.let {
            return it
        }

        return suspendCancellableCoroutine { continuation ->
            val standardIntegrityManager = IntegrityManagerFactory.createStandard(applicationContext)

            standardIntegrityManager.prepareIntegrityToken(
                StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
                    .setCloudProjectNumber(cloudProjectNumber)
                    .build()
            ).addOnSuccessListener {
                integrityTokenProvider = it
                continuation.resumeWith(Result.success(it))
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    private suspend fun requestIntegrityVerdict(
        integrityTokenProvider: StandardIntegrityTokenProvider,
        requestHash: String
    ): StandardIntegrityToken {
        return integrityTokenProvider.request(
            StandardIntegrityTokenRequest.builder()
                .setRequestHash(requestHash)
                .build()
        ).await()
    }

    private fun calculateRequestHash(inputString: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(inputString.toByteArray())
        val hashBytes = messageDigest.digest()
        return hashBytes.fold("") { str, it -> str + "%02x".format(it) }
    }

    private suspend fun sendToServer(requestHash: String, token: String): Result<String> {
        return secureRequestClient.performSecureRequest(requestHash = requestHash, token = token)
    }
}
