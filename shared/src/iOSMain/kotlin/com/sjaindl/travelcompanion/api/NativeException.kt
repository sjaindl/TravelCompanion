package com.sjaindl.travelcompanion.api

import co.touchlab.kermit.Logger
import com.sjaindl.travelcompanion.exception.OfflineException
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import platform.Foundation.NSURLErrorCancelled
import platform.Foundation.NSURLErrorDomain

val nonRelevantURLSessionErrorsCodes = listOf(NSURLErrorCancelled)

actual fun handleNativeException(exception: Exception) {
    Logger.e(exception.message ?: exception.stackTraceToString(), exception)

    val nsError = (exception as? DarwinHttpRequestException)?.origin ?: return
    val code = nsError.code
    val domain = nsError.domain ?: return

    if (domain == NSURLErrorDomain && !nonRelevantURLSessionErrorsCodes.contains(code)) {
        throw OfflineException()
    }
}
