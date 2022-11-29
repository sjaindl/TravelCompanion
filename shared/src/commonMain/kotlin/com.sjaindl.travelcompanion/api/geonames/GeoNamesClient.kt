package com.sjaindl.travelcompanion.api.geonames

import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpClientBuilder
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.api.logError
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Mockable
class GeoNamesClient() {
    private val client = HttpClientBuilder()
        .withJsonSerialization()
        .withLogging()
        .build()

    suspend fun fetchCountryCode(latitude: Double, longitude: Double): String? {
        val urlComponents = GeoNamesConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/"

        val response = HttpResponseHandler(client).request(
            baseUrl = baseUrl,
            urlString = urlComponents.path,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = listOf(
                Pair(GeoNamesConstants.ParameterKeys.latitude, latitude),
                Pair(GeoNamesConstants.ParameterKeys.longitude, longitude),
                Pair(GeoNamesConstants.ParameterKeys.username, SecretConstants.userNameGeoNames)
            )
        )

        val responseText = response.bodyAsText().trim()

        // response could be valid GeocodeResponse JSON, or just a country code string
        return try {
            Json.decodeFromString<GeocodeResponse>(responseText).countryCode
        } catch (exc: SerializationException) {
            logError(exc)
            if (responseText.startsWith("ERR:")) {
                return null
            }
            return responseText
        }
    }
}
