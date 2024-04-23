package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.Serializable

// https://developers.google.com/maps/documentation/places/web-service/place-autocomplete
// Could extend with e.g. locationBias / locationRestriction, if needed
@Serializable
data class AutoCompleteBody(
    val input: String,
    val sessionToken: String,
)
