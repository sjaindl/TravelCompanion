package com.sjaindl.travelcompanion.api

import co.touchlab.kermit.Logger

actual fun logError(exception: Exception) {
    Logger.e(exception.message ?: exception.stackTraceToString(), exception)
}
