package com.sjaindl.travelcompanion.api.google

import co.touchlab.kermit.Logger
import com.sjaindl.travelcompanion.AutocompleteConfig
import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
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
        val baseUrl = "${urlComponents.URL_PROTOCOL}://${urlComponents.DOMAIN_MAPS}/"

        Logger.d("GoogleClient searchPlaces: $baseUrl")

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.PATH_NEARBY_SEARCH,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams
        )

        Logger.d("GoogleClient searchPlaces: ${response.bodyAsText()}")

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

        val body = buildAutoCompleteBody(input = input, token = token)

        val urlComponents = GoogleConstants.UrlComponents
        val baseUrl = "${urlComponents.URL_PROTOCOL}://${urlComponents.DOMAIN_PLACES}/"

        Logger.d("GoogleClient autocomplete: $baseUrl")

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.PATH_AUTOCOMPLETE,
            httpMethod = HttpMethod.Post,
            requestHeaders = HttpResponseHandler.defaultHeaders.toMutableList().apply {
                add(GoogleConstants.ParameterKeys.X_KEY to SecretConstants.apiKeyGooglePlaces)
            },
            requestParams = emptyList(),
            setBody = {
                it.setBody(body = body)
            },
        )

        Logger.d("GoogleClient autocomplete: ${response.bodyAsText()}")

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
        val baseUrl = "${urlComponents.URL_PROTOCOL}://${urlComponents.DOMAIN_PLACES}"

        Logger.d("GoogleClient placeDetail: $baseUrl")

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = "${urlComponents.PATH_PLACE_DETAILS}/$placeId",
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders.toMutableList().apply {
                add(GoogleConstants.ParameterKeys.X_KEY to SecretConstants.apiKeyGooglePlaces)
            },
            requestParams = requestParams
        )

        Logger.d("GoogleClient placeDetail: ${response.bodyAsText()}")

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
        val baseUrl = "${urlComponents.URL_PROTOCOL}://${urlComponents.DOMAIN_MAPS}"

        Logger.d("GoogleClient reverseGeocode: $baseUrl")

        val response = httpResponseHandler.request(
            baseUrl = baseUrl,
            urlString = urlComponents.PATH_REVERSE_GEOCODE,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams,
        )

        Logger.d("GoogleClient reverseGeocode: ${response.bodyAsText()}")

        return try {
            Result.success(
                response.body()
            )
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }

    override fun buildAutoCompleteBody(
        input: String,
        token: String,
    ): AutoCompleteBody {
        return AutoCompleteBody(input = input, sessionToken = token)
    }

    override fun buildPlaceDetailRequestParams(
        placeId: String,
        token: String
    ): List<Pair<String, String>> {
        return listOf(
            GoogleConstants.ParameterKeys.FIELDS to GoogleConstants.ParameterValues.PLACE_DETAIL_FIELDS,
            GoogleConstants.ParameterKeys.SESSION_TOKEN to token,
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
            GoogleConstants.ParameterKeys.RANK_BY to GoogleConstants.ParameterValues.RANK_BY,
            GoogleConstants.ParameterKeys.RADIUS to radius,
            GoogleConstants.ParameterKeys.PLACE_TYPE to type,
            GoogleConstants.ParameterKeys.KEY to SecretConstants.apiKeyGooglePlaces,
            GoogleConstants.ParameterKeys.STRICT_BOUNDS to GoogleConstants.ParameterValues.STRICT_BOUNDS
        )

        if (latitude != null && longitude != null) {
            parameters.add(GoogleConstants.ParameterKeys.LOCATION to "${latitude},${longitude}")
        }

        val name = text?.trim()
        if (!name.isNullOrBlank()) {
            parameters.add(GoogleConstants.ParameterKeys.NAME to name)
        }

        return parameters
    }

    override fun buildReverseGeocodeRequestParams(
        latitude: Float,
        longitude: Float,
    ): List<Pair<String, String>> {
        return listOf(
            GoogleConstants.ParameterKeys.LAT_LNG to "$latitude,$longitude",
            GoogleConstants.ParameterKeys.KEY to SecretConstants.apiKeyGooglePlaces,
            GoogleConstants.ParameterKeys.RESULT_TYPE to GoogleConstants.ParameterValues.RESULT_TYPE,
        )
    }
}
