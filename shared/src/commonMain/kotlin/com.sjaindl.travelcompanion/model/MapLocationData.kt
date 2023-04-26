package com.sjaindl.travelcompanion.model

import kotlinx.serialization.Serializable

@Serializable
data class MapLocationData(
    val latitude: Float,
    val longitude: Float,
    val radius: Float,
)
