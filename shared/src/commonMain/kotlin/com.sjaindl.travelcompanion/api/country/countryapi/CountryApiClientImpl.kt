package com.sjaindl.travelcompanion.api.country.countryapi

import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.filesystem.platformFileSystem
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

class CountryApiClientImpl(private val responseHandler: HttpResponseHandler) : CountryApiClient {
    override suspend fun fetchCountryDetails(countryCode: String): Result<CountryApiResponse> {
        val urlComponents = CountryApiConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}/${urlComponents.path}"

        val response = responseHandler.request(
            baseUrl = baseUrl,
            urlString = countryCode,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = listOf(CountryApiConstants.RequestKeys.accessKey to SecretConstants.countryApiKey)
        )

        return try {
            Result.success(response.body())
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }

    override suspend fun fetchCountryDetailsLocal(countryCode: String): CountryApiResponse? {
        val path = "assets/countrydata.json".toPath()

        val readmeContent = platformFileSystem().read(path) {
            readUtf8()
        }

        val countries: List<CountryApiResponse> = Json.decodeFromString(readmeContent)

        val country = countries.firstOrNull { it.alpha2Code == countryCode }

        return country
    }
}
