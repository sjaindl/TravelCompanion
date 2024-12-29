package com.sjaindl.travelcompanion.api.google

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod

class SecureRequestClient {

    val client = HttpClient { }

    suspend fun performSecureRequest(
        requestHash: String,
        token: String,
    ): Result<String> {
        val requestParams = buildParams(
            requestHash = requestHash,
            token = token,
        )

        return try {
            val response = client.request {
                method = HttpMethod.Get
                url(urlString = "http://0.0.0.0:8080/secure")

                requestParams.forEach {
                    parameter(key = it.first, value = it.second)
                }
            }

            Result.success(response.bodyAsText())
        } catch (exception: NoTransformationFoundException) {
            Result.failure(exception)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    private fun buildParams(
        requestHash: String,
        token: String,
    ): List<Pair<String, String>> {
        return listOf(
            "requestHash" to requestHash,
            "token" to token,
        )
    }
}
