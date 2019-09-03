import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.model.GeoNames.GeoNamesConstants

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import io.ktor.http.*
import kotlinx.io.core.use
import kotlinx.serialization.json.Json
import main.model.GeoNames.GeoCodeRepository
import main.model.GeoNames.WebClient

//  GeoNamesClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

class GeoNamesClient() {

    companion object {
        val instance = GeoNamesClient()
    }

    val urlComponents = GeoNamesConstants.UrlComponents()
    val endPoint = "${urlComponents.urlProtocol}://${urlComponents.domain}${urlComponents.path}"

    /* // Initialization not possible here because of InvalidMutabilityException on iOS
    private var client = HttpClient {
        install(JsonFeature) {
            serializer = JsonKotlinxSerializer().apply {
                setMapper<Geocode>(Geocode.serializer())
            }
        }
        install(HttpCallValidator)
    }
*/

    suspend fun fetchCountryCode(latitude: Double, longitude: Double, client: HttpClient): GeoCodeRepository = client.request<HttpResponse> {
        method = HttpMethod.Get

        val params = Parameters.build {
            append(GeoNamesConstants.ParameterKeys().latitude, "${latitude}")
            append(GeoNamesConstants.ParameterKeys().longitude, "${longitude}")
            append(GeoNamesConstants.ParameterKeys().username, SecretConstants().userNameGeoNames)
        }.formUrlEncode()

        val url = "${endPoint}?${params}"

        url {
            takeFrom(url)
            //path("lat", latitude.toString(), "lng", longitude.toString(), "username", "jaindl.stefan")
        }

    }.use { response ->

        val json = response.readText()
        WebClient.instance.performBasicWebResponseChecks(response, json)

        val list = Json.nonstrict.parse(GeoCodeRepository.serializer(), json)

        return@use list
    }
}
