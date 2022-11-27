package com.sjaindl.travelcompanion.api

import timber.log.Timber

actual fun logError(exception: Exception) {
    Timber.e(exception)
}
