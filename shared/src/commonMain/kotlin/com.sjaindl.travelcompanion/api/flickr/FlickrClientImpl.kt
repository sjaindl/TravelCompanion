package com.sjaindl.travelcompanion.api.flickr

import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.*
import io.ktor.http.*

@Mockable
class FlickrClientImpl(private val responseHandler: HttpResponseHandler) : FlickrClient {
    override suspend fun fetchPhotos(text: String, offset: Int, limit: Int): Result<FlickrPhotoResponse> {
        val requestParams = buildQueryItems(offset = offset, limit = limit)
        requestParams.add(FlickrConstants.ParameterKeys.text to text)

        return fetch(requestParams = requestParams)
    }

    override suspend fun fetchPhotos(latitude: Double, longitude: Double, offset: Int, limit: Int): Result<FlickrPhotoResponse> {
        val requestParams = buildQueryItems(offset = offset, limit = limit)
        requestParams.add(FlickrConstants.ParameterKeys.boundingBox to boundingBoxString(latitude = latitude, longitude = longitude))

        return fetch(requestParams = requestParams)
    }

    private suspend fun fetch(requestParams: List<Pair<String, String>>): Result<FlickrPhotoResponse> {
        val urlComponents = FlickrConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://${urlComponents.domain}"

        return try {
            val response = responseHandler.request(
                baseUrl = baseUrl,
                urlString = urlComponents.path,
                httpMethod = HttpMethod.Get,
                requestHeaders = HttpResponseHandler.defaultHeaders,
                requestParams = requestParams,
            )

            Result.success(response.body())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun buildQueryItems(offset: Int, limit: Int): MutableList<Pair<String, String>> {
        return mutableListOf(
            FlickrConstants.ParameterKeys.method to FlickrConstants.ParameterValues.searchMethod,
            FlickrConstants.ParameterKeys.apiKey to SecretConstants.apiKeyFlickr,
            FlickrConstants.ParameterKeys.safeSearch to FlickrConstants.ParameterValues.useSafeSearch,
            FlickrConstants.ParameterKeys.extras to FlickrConstants.ParameterValues.imageSize,
            FlickrConstants.ParameterKeys.format to FlickrConstants.ParameterValues.responseFormat,
            FlickrConstants.ParameterKeys.noJsonCallback to FlickrConstants.ParameterValues.disableJsonCallback,
            FlickrConstants.ParameterKeys.sortOrder to FlickrConstants.ParameterValues.sortOrder,
            FlickrConstants.ParameterKeys.offset to offset.toString(),
            FlickrConstants.ParameterKeys.limit to limit.toString(),
            //"tags": "1025fav" //wow
        )
    }

    private fun boundingBoxString(latitude: Double, longitude: Double): String {
        // ensure bounding box is bound by minimum and maximum
        val location = FlickrConstants.Location
        val minimumLon = (longitude - location.searchBBoxHalfWidth).coerceAtLeast(location.searchLongitudeRange.first)
        val minimumLat = (latitude - location.searchBBoxHalfHeight).coerceAtLeast(location.searchLatitudeRange.first)
        val maximumLon = (longitude + location.searchBBoxHalfWidth).coerceAtMost(location.searchLongitudeRange.second)
        val maximumLat = (latitude + location.searchBBoxHalfHeight).coerceAtMost(location.searchLatitudeRange.second)
        return "$minimumLon,$minimumLat,$maximumLon,$maximumLat"
    }
}
