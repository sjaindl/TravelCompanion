package com.sjaindl.travelcompanion.exception

class ApiException(val code: Int, val body: Any?) : Exception()
