package com.sjaindl.travelcompanion.api.wiki

import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.util.Mockable
import io.ktor.client.call.*
import io.ktor.http.*

@Mockable
class WikiClientImpl(private val httpResponseHandler: HttpResponseHandler) : WikiClient {
    override suspend fun fetchWikiLink(name: String, domain: String): Result<WikiResponse> {
        val requestParams = buildRequestParams(name = name)

        val urlComponents = WikiConstants.UrlComponents
        val baseUrl = "${urlComponents.urlProtocol}://$domain"

        return try {
            val response = httpResponseHandler.request(
                baseUrl = baseUrl,
                urlString = urlComponents.path,
                httpMethod = HttpMethod.Get,
                requestHeaders = HttpResponseHandler.defaultHeaders,
                requestParams = requestParams
            )

            Result.success(response.body())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun buildRequestParams(name: String): List<Pair<String, String>> {
        return listOf(
            WikiConstants.ParameterKeys.action to WikiConstants.ParameterValues.query,
            WikiConstants.ParameterKeys.format to WikiConstants.ParameterValues.responseFormat,
            WikiConstants.ParameterKeys.titles to name,
        )
    }
}
