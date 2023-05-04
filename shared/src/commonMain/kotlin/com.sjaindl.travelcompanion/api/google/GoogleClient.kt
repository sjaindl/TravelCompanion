package com.sjaindl.travelcompanion.api.google

interface GoogleClient {
    suspend fun searchPlaces(
        text: String?,
        latitude: Double?,
        longitude: Double?,
        type: String,
        radius: String,
    ): PlacesNearbySearchResponse

    suspend fun autocomplete(input: String, token: String): PlacesAutoCompleteResponse?

    suspend fun placeDetail(placeId: String, token: String): PlacesDetailsResponse

    fun buildAutoCompleteRequestParams(
        input: String,
        token: String
    ): List<Pair<String, String>>

    fun buildPlaceDetailRequestParams(
        placeId: String,
        token: String
    ): List<Pair<String, String>>
}
