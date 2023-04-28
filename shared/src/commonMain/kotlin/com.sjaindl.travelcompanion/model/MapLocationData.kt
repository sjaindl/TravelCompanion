package com.sjaindl.travelcompanion.model

import kotlinx.serialization.Serializable

@Serializable
data class MapLocationData(
    val latitude: Float,
    val longitude: Float,
    val radius: Float,
) {
    companion object {
        val default = MapLocationData(latitude = 37.38605f, longitude = -122.083855f, radius = 50.0f)
    }
}
