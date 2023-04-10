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
}
