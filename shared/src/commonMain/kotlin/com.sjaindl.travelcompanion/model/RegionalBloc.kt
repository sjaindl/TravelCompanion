package com.sjaindl.travelcompanion.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionalBlock(val acronym: String? = null, val name: String? = null, val otherNames: List<String>? = null)
