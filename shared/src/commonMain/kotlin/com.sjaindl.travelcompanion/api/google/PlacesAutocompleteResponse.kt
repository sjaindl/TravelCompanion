package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesAutoCompleteResponse(
    var predictions: List<PlacesPredictions>, var status: String
)

@Serializable
data class PlacesPredictions(
    var description: String,
    var id: String? = null,
    @SerialName("place_id")
    var placeId: String? = null,
    var reference: String? = null,
    var types: List<String>,
    var terms: List<PlacesAutoCompleteTerm>
)

@Serializable
data class PlacesAutoCompleteTerm(
    var offset: Int, var value: String
)
