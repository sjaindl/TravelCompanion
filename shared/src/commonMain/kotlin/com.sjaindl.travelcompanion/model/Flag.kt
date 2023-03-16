package com.sjaindl.travelcompanion.model

import kotlinx.serialization.Serializable

@Serializable
data class Flag(val svg: String? = null, val png: String? = null)
