package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlacesNearbySearchResponse(
    @SerialName("html_attributions")
    var htmlAttributions: List<String>,

    @SerialName("next_page_token")
    var nextPageToken: String? = null,
    var results: List<GooglePlace>,
    var status: String,

    @SerialName("error_message")
    var errorMessage: String? = null
)
