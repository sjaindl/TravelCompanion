package com.sjaindl.travelcompanion.api.country.restcountries

import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

@Mockable
class RestCountriesClientImpl(private val responseHandler: HttpResponseHandler) : RestCountriesClient {
    override suspend fun fetchCountryDetails(countryCode: String): Result<CountryResponse> {
        val urlComponents = RestCountriesConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/${urlComponents.path}"

        val response = responseHandler.request(
            baseUrl = baseUrl,
            urlString = countryCode,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
        )

        return try {
            Result.success(response.body())
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }
}
