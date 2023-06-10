package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding?hl=en
@Serializable
data class GeocodingResponse(
    @SerialName("plus_code")
    val plusCode: PlusCode? = null,
    val results: List<GeocodingResult> = emptyList(),
)
