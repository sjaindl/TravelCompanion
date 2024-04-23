package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResult(
    @SerialName("address_components")
    val addressComponents: List<GeocodingPlaceDetailAddressComponent>? = null,
    @SerialName("formatted_address")
    val formattedAddress: String? = null,
    val geometry: PlaceDetailGeometry,
    @SerialName("place_id")
    val placeId: String,
    var types: List<String>?
)

@Serializable
data class PlaceDetailGeometry(var location: PlaceDetailLocation)

@Serializable
data class PlaceDetailLocation(
    var lat: Double,
    var lng: Double,

    @SerialName("location_type")
    val locationType: String? = null,
    val viewPort: ViewPort? = null,
)

@Serializable
data class ViewPort(
    @SerialName("northeast")
    val northEast: LocationPair? = null,
    @SerialName("southwest")
    val southWest: LocationPair? = null,
)

@Serializable
data class LocationPair(
    var latitude: Double,
    var longitude: Double,
)

@Serializable
data class GeocodingPlaceDetailAddressComponent(
    @SerialName("long_name")
    var longName: String,
    @SerialName("short_name")
    var shortName: String,
    var types: List<String>
)
