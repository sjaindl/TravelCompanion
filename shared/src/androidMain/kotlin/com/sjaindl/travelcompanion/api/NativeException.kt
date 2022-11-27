package com.sjaindl.travelcompanion.api

import com.sjaindl.travelcompanion.exception.OfflineException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

actual fun handleNativeException(exception: Exception) {
    when (exception) {
        is UnknownHostException -> throw OfflineException()
        is SocketTimeoutException -> throw OfflineException()
        is ConnectException -> throw OfflineException()
    }
}
