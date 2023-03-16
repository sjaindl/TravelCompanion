package com.sjaindl.travelcompanion.api.country

import com.sjaindl.travelcompanion.api.HttpClientBuilder
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.*
import io.ktor.http.*

@Mockable
class RestCountriesClient {
    private val client = HttpClientBuilder()
        .withJsonSerialization()
        .withLogging()
        .build()

    suspend fun fetchCountryDetails(countryCode: String): Result<CountryResponse> {
        val urlComponents = RestCountriesConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/${urlComponents.path}"

        val response = HttpResponseHandler(client).request(
            baseUrl = baseUrl,
            urlString = countryCode,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
        )

        return try {
            response.body()
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }
}
