package com.sjaindl.travelcompanion.api

import com.sjaindl.travelcompanion.exception.ApiException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HttpResponseHandler(private val client: HttpClient) {
    companion object {
        val apiExceptionParser = { code: Int, json: String ->
            ApiException(code, json)
        }

        val defaultHeaders = listOf(
            Pair(HttpHeaders.Accept, "application/json"),
            Pair(HttpHeaders.AcceptLanguage, languageTag()),
            Pair(HttpHeaders.ContentType, "application/json")
        )
    }

    suspend fun request(
        baseUrl: String,
        urlString: String,
        httpMethod: HttpMethod,
        requestHeaders: List<Pair<String, String>> = defaultHeaders,
        requestParams: List<Pair<String, Any?>> = emptyList(),
    ): HttpResponse {
        try {
            val response: HttpResponse = client.request {
                method = httpMethod
                url("$baseUrl/$urlString")

                requestHeaders.forEach {
                    headers {
                        append(it.first, it.second)
                    }
                }

                requestParams.forEach {
                    parameter(it.first, it.second)
                }
            }

            return response.body()
        } catch (exception: Exception) {
            handleNativeException(exception)

            when (exception) {
                is RedirectResponseException -> throw handleErrorStatusCode(exception.response) // 3xx response
                is ClientRequestException -> throw handleErrorStatusCode(exception.response) // 4xx response
                is ServerResponseException -> throw handleErrorStatusCode(exception.response) // 5xx response
                else -> throw exception
            }
        }
    }

    private suspend fun handleErrorStatusCode(response: HttpResponse): Exception {
        val body: String = response.body()
        val errorBodyJsonString = body
            .takeUnless { it.isBlank() }
            ?: return ApiException(response.status.value, null)

        return try {
            apiExceptionParser.invoke(response.status.value, errorBodyJsonString)
        } catch (exception: Exception) {
            logError(exception)
            return ApiException(response.status.value, null)
        }
    }
}

expect fun handleNativeException(exception: Exception)
expect fun languageTag(): String
expect fun logError(exception: Exception)
