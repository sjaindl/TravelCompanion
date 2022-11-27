package com.sjaindl.travelcompanion.exception

typealias ApiExceptionParser = (code: Int, json: String) -> ApiException
