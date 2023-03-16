package com.sjaindl.travelcompanion.model

import kotlinx.serialization.Serializable

@Serializable
data class Currency(val code: String? = null, val name: String? = null, val symbol: String? = null)
