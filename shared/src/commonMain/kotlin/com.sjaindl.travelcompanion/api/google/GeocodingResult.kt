package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    @SerialName("address_components")
    val addressComponents: List<PlaceDetailAddressComponent>? = null,
    @SerialName("formatted_address")
    val formattedAddress: String? = null,
    val geometry: PlaceDetailGeometry,
    @SerialName("place_id")
    val placeId: String,
    var types: List<String>?
)
