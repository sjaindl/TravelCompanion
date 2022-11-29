package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesDetailsResponse(
    @SerialName("html_attributions")
    var htmlAttributions: List<String>,
    var result: PlaceDetailResult,
    var status: String
)

//photos
@Serializable
data class PlaceDetailResult(
    @SerialName("address_components")
    var addressComponents: List<PlaceDetailAddressComponent>? = null,
    @SerialName("adr_address")
    var adrAddress: String? = null,
    @SerialName("formatted_address")
    var formattedAddress: String? = null,
    var geometry: PlaceDetailGeometry,
    var icon: String? = null,
    var name: String? = null,
    @SerialName("permanently_closed")
    var permanentlyClosed: String? = null,
    var url: String? = null,
    @SerialName("utc_offset")
    var utcOffset: Int? = null,
    var vicinity: String? = null,
    var types: List<String?>
)

@Serializable
data class PlaceDetailGeometry(var location: PlaceDetailLocation)

@Serializable
data class PlaceDetailLocation(
    var lat: Double,
    var lng: Double
)

@Serializable
data class PlaceDetailAddressComponent(
    @SerialName("long_name")
    var longName: String,
    @SerialName("short_name")
    var shortName: String,
    var types: List<String>
)
