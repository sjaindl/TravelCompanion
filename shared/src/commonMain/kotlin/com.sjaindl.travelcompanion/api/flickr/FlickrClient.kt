package com.sjaindl.travelcompanion.api.flickr

import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpClientBuilder
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.*
import io.ktor.http.*

@Mockable
class FlickrClient {
    private val client = HttpClientBuilder()
        .withJsonSerialization()
        .withLogging()
        .build()

    suspend fun fetchPhotos(text: String): Result<String> { // TODO:  Result
        val requestParams = buildQueryItems()
        requestParams.add(FlickrConstants.ParameterKeys.text to text)

        return fetch(requestParams = requestParams)
    }

    suspend fun fetchPhotos(latitude: Double, longitude: Double): Result<String> { // TODO:  Result
        val requestParams = buildQueryItems()
        requestParams.add(FlickrConstants.ParameterKeys.boundingBox to bboxString(latitude = latitude, longitude = longitude))

        return fetch(requestParams = requestParams)
    }

    private suspend fun fetch(requestParams: List<Pair<String, String>>): Result<String> { // TODO:  Result
        val urlComponents = FlickrConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}"

        val response = HttpResponseHandler(client).request(
            baseUrl = baseUrl,
            urlString = urlComponents.path,
            httpMethod = HttpMethod.Get,
            requestHeaders = HttpResponseHandler.defaultHeaders,
            requestParams = requestParams,
        )

        return try {
            Result.success(response.body())
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        }
    }


    fun buildQueryItems(): MutableList<Pair<String, String>> {
        return mutableListOf(
            FlickrConstants.ParameterKeys.method to FlickrConstants.ParameterValues.searchMethod,
            FlickrConstants.ParameterKeys.apiKey to SecretConstants.apiKeyFlickr,
            FlickrConstants.ParameterKeys.safeSearch to FlickrConstants.ParameterValues.useSafeSearch,
            FlickrConstants.ParameterKeys.extras to FlickrConstants.ParameterValues.imageSize,
            FlickrConstants.ParameterKeys.format to FlickrConstants.ParameterValues.responseFormat,
            FlickrConstants.ParameterKeys.noJsonCallback to FlickrConstants.ParameterValues.disableJsonCallback,
            FlickrConstants.ParameterKeys.sortOrder to FlickrConstants.ParameterValues.sortOrder,
            //"tags": "1025fav" //wow
        )
    }

    fun bboxString(latitude: Double, longitude: Double): String {
        // ensure bbox is bounded by minimum and maximums
        val location = FlickrConstants.Location
        val minimumLon = (longitude - location.searchBBoxHalfWidth).coerceAtLeast(location.searchLongitudeRange.first)
        val minimumLat = (latitude - location.searchBBoxHalfHeight).coerceAtLeast(location.searchLatitudeRange.first)
        val maximumLon = (longitude + location.searchBBoxHalfWidth).coerceAtLeast(location.searchLongitudeRange.second)
        val maximumLat = (latitude + location.searchBBoxHalfHeight).coerceAtLeast(location.searchLatitudeRange.second)
        return "$minimumLon,$minimumLat,$maximumLon,$maximumLat"
    }
}
