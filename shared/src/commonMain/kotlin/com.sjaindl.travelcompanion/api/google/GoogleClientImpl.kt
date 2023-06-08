package com.sjaindl.travelcompanion.api.google

import com.sjaindl.travelcompanion.AutocompleteConfig
import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

@Mockable
class GoogleClientImpl(private val httpResponseHandler: HttpResponseHandler) : GoogleClient {
    private val autocompleteCache = mutableMapOf<String, PlacesAutoCompleteResponse?>()
    private val placeDetailCache = mutableMapOf<String, PlacesDetailsResponse?>()

    override suspend fun searchPlaces(
        text: String?,
        latitude: Double?,
        longitude: Double?,
        type: String,
        radius: String,
    ): PlacesNearbySearchResponse {
        val requestParams = buildPlaceSearchRequestParams(
            text = text,
            latitude = latitude,
            longitude = longitude,
            type = type,
            radius = radius
        )

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        return httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathNearbySearch,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        ).body()
    }

    override suspend fun autocomplete(input: String, token: String): PlacesAutoCompleteResponse? {
        if (input.count() < AutocompleteConfig.autocompletionMinChars) {
            return null
        }

        autocompleteCache[input]?.let {
            return it
        }

        val requestParams = buildAutoCompleteRequestParams(input = input, token = token)

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        return httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathAutocomplete,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        ).body<PlacesAutoCompleteResponse?>().also {
            autocompleteCache[input] = it
        }
    }

    override suspend fun placeDetail(placeId: String, token: String): PlacesDetailsResponse {
        placeDetailCache[placeId]?.let {
            return it
        }

        val requestParams = buildPlaceDetailRequestParams(placeId = placeId, token = token)

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        return httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathPlaceDetail,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        ).body<PlacesDetailsResponse>().also {
            placeDetailCache[placeId] = it
        }
    }

    override fun buildAutoCompleteRequestParams(
        input: String,
        token: String
    ): List<Pair<String, String>> {
        return listOf(
            //GoogleConstants.ParameterKeys.radius: GoogleConstants.ParameterValues.radius,
            GoogleConstants.ParameterKeys.types to GoogleConstants.ParameterValues.autocompletePlaceType,
            GoogleConstants.ParameterKeys.input to input,
            GoogleConstants.ParameterKeys.sessionToken to token,
            GoogleConstants.ParameterKeys.key to SecretConstants.apiKeyGooglePlaces
            //GoogleConstants.ParameterKeys.strictBounds: GoogleConstants.ParameterValues.strictBounds
        )
    }

    override fun buildPlaceDetailRequestParams(
        placeId: String,
        token: String
    ): List<Pair<String, String>> {
        return listOf(
            GoogleConstants.ParameterKeys.placeId to placeId,
            GoogleConstants.ParameterKeys.fields to GoogleConstants.ParameterValues.placeDetailFields,
            GoogleConstants.ParameterKeys.sessionToken to token,
            GoogleConstants.ParameterKeys.key to SecretConstants.apiKeyGooglePlaces
        )
    }

    private fun buildPlaceSearchRequestParams(
        text: String?,
        latitude: Double?,
        longitude: Double?,
        type: String,
        radius: String
    ): List<Pair<String, String>> {
        val parameters = mutableListOf(
            GoogleConstants.ParameterKeys.rankBy to GoogleConstants.ParameterValues.rankBy,
            GoogleConstants.ParameterKeys.radius to radius,
            GoogleConstants.ParameterKeys.placeType to type,
            GoogleConstants.ParameterKeys.key to SecretConstants.apiKeyGooglePlaces,
            GoogleConstants.ParameterKeys.strictBounds to GoogleConstants.ParameterValues.strictBounds
        )

        if (latitude != null && longitude != null) {
            parameters.add(GoogleConstants.ParameterKeys.location to "${latitude},${longitude}")
        }

        val name = text?.trim()
        if (!name.isNullOrBlank()) {
            parameters.add(GoogleConstants.ParameterKeys.name to name)
        }

        return parameters
    }
}
