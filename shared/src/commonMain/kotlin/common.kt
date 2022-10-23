package com.sjaindl.travelcompanion

import JsonKotlinxSerializer
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import main.model.GeoNames.Geocode

private var client = HttpClient {
    install(JsonFeature) {
        serializer = JsonKotlinxSerializer().apply {
            setMapper<Geocode>(Geocode.serializer())
        }
    }
    install(HttpCallValidator)
}

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin Rocks on ${platformName()}"
}

suspend fun fetchGeoCode(latitude: Double, longitude: Double): String {

    val client = createClient(JsonKotlinxSerializer().apply {
        setMapper<Geocode>(Geocode.serializer())
    })

    /*
    GeoNamesClient.instance.fetchCountryCode(latitude, longitude, client).let { repositories ->
        return repositories.countryCode
    }
     */

    return "AT" //TODO
}

internal suspend fun helloCoroutine() {
    println("Hello Coroutines!")
}

class Api {
    private val client = HttpClient()

    internal suspend fun request(urlString: String): String {
        val result = client.request<HttpResponse>(urlString) {
            method = HttpMethod.Get
        }.readText()
        return result
    }
}

fun createClient(jsonSerializer: JsonSerializer): HttpClient {
    return HttpClient {
        install(JsonFeature) {
            serializer = jsonSerializer
        }
        install(HttpCallValidator)
    }
}
