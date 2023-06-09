package com.sjaindl.travelcompanion.api.google

interface GoogleClient {
    suspend fun searchPlaces(
        text: String?,
        latitude: Double?,
        longitude: Double?,
        type: String,
        radius: String,
    ): Result<PlacesNearbySearchResponse>

    suspend fun autocomplete(input: String, token: String): Result<PlacesAutoCompleteResponse?>

    suspend fun placeDetail(placeId: String, token: String): Result<PlacesDetailsResponse>

    fun buildAutoCompleteRequestParams(
        input: String,
        token: String
    ): List<Pair<String, String>>

    fun buildPlaceDetailRequestParams(
        placeId: String,
        token: String
    ): List<Pair<String, String>>
}
