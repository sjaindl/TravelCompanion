package com.sjaindl.travelcompanion

import GeoNamesClient
import JsonKotlinxSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.features.HttpCallValidator
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.response.readText
import io.ktor.http.HttpMethod
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

  GeoNamesClient.instance.fetchCountryCode(latitude, longitude, client)?.let { repositories ->
    return repositories.countryCode
    }
}

internal suspend fun helloCoroutine() {
    println("Hello Coroutines!")
}

class Api {
  private val client = HttpClient()

  internal suspend fun request(urlString: String): String {
    val result: String = client.call(urlString) {
      method = HttpMethod.Get
    }.response.readText()
    return result
  }
}

fun createClient(jsonSerializer: JsonSerializer) : HttpClient {
  return HttpClient {
    install(JsonFeature) {
      serializer = jsonSerializer
    }
    install(HttpCallValidator)
  }
}
