package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesDetailsResponse(
    var id: String? = null,
    var name: String? = null,
    var formattedAddress: String? = null,
    var addressComponents: List<PlaceDetailAddressComponent>? = null,
    var location: LocationPair = LocationPair(latitude = 0.0, longitude = 0.0),
    var websiteUri: String? = null,
    var utcOffsetMinutes: Int? = null,
    var adrFormatAddress: String? = null,
    var iconMaskBaseUri: String? = null,
    var iconBackgroundColor: String? = null,
    var displayName: DisplayName? = null,
    var photos: List<DetailPhoto> = emptyList(),
)

@Serializable
data class DetailPhoto(
    var name: String,
    var widthPx: Int,
    var heightPx: Int,
    var authorAttributions: List<AuthorAttributions> = emptyList(),
)

@Serializable
data class AuthorAttributions(
    var displayName: String? = null,
    var uri: String? = null,
    var photoUri: String? = null,
)

@Serializable
data class DisplayName(
    var text: String,
    var languageCode: String,
)

@Serializable
data class PlaceDetailAddressComponent(
    @SerialName("longText")
    var longName: String? = null,
    @SerialName("shortText")
    var shortName: String? = null,
    var types: List<String> = emptyList(),
    var languageCode: String? = null,
)
