package com.sjaindl.travelcompanion

import kotlinx.serialization.Serializable

@Serializable
data class ErrorRepository(val status: Int, val error: String)
