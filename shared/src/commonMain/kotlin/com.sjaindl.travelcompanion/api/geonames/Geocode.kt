package com.sjaindl.travelcompanion.api.geonames

import kotlinx.serialization.Serializable

@Serializable
data class GeocodeResponse (
    val languages: String,
    val distance: String,
    val countryCode: String,
    val countryName: String
)
