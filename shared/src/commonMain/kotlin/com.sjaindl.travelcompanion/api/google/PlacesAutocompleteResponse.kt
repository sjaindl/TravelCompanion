package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesAutoCompleteResponse(
    var suggestions: List<PlacePredictions> = emptyList(),
)

@Serializable
data class PlacePredictions(
    var placePrediction: PlacePrediction,
)

@Serializable
data class PlacePrediction(
    @SerialName("text")
    var description: PlacesAutoCompleteText? = null,
    var placeId: String? = null,
)

@Serializable
data class PlacesAutoCompleteText(
    var text: String
)
