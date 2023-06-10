package com.sjaindl.travelcompanion.api.google

import com.sjaindl.travelcompanion.AutocompleteConfig
import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
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
    ): Result<PlacesNearbySearchResponse> {
        val requestParams = buildPlaceSearchRequestParams(
            text = text,
            latitude = latitude,
            longitude = longitude,
            type = type,
            radius = radius
        )

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathNearbySearch,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        )

        return try {
            Result.success(response.body())
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }

    override suspend fun autocomplete(input: String, token: String): Result<PlacesAutoCompleteResponse?> {
        if (input.count() < AutocompleteConfig.autocompletionMinChars) {
            return Result.success(null)
        }

        autocompleteCache[input]?.let {
            return Result.success(it)
        }

        val requestParams = buildAutoCompleteRequestParams(input = input, token = token)

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathAutocomplete,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        )

        return try {
            Result.success(
                response.body<PlacesAutoCompleteResponse?>().also {
                    autocompleteCache[input] = it
                }
            )
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }

    override suspend fun placeDetail(placeId: String, token: String): Result<PlacesDetailsResponse> {
        placeDetailCache[placeId]?.let {
            return Result.success(it)
        }

        val requestParams = buildPlaceDetailRequestParams(placeId = placeId, token = token)

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathPlaceDetail,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        )

        return try {
            Result.success(
                response.body<PlacesDetailsResponse>().also {
                    placeDetailCache[placeId] = it
                }
            )
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }

    override suspend fun reverseGeocode(
        latitude: Float,
        longitude: Float,
    ): Result<GeocodingResponse> {
        val requestParams = buildReverseGeocodeRequestParams(
            latitude = latitude,
            longitude = longitude,
        )

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}"

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.pathReverseGeocode,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams,
        )

        return try {
            Result.success(
                response.body()
            )
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
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

    override fun buildReverseGeocodeRequestParams(
        latitude: Float,
        longitude: Float,
    ): List<Pair<String, String>> {
        return listOf(
            GoogleConstants.ParameterKeys.latLng to "$latitude,$longitude",
            GoogleConstants.ParameterKeys.key to SecretConstants.apiKeyGooglePlaces,
            GoogleConstants.ParameterKeys.resultType to GoogleConstants.ParameterValues.resultType,
        )
    }
}
