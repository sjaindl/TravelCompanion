package com.sjaindl.travelcompanion

import GeoNamesClient
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.response.readText
import io.ktor.http.HttpMethod

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
  return "Kotlin Rocks on ${platformName()}"
}

suspend fun fetchCode(latitude: Double, longitude: Double): String {
  val client = GeoNamesClient()

  //client.fetchCountryCode(37.0856432, 25.1488318)

  try {
    client.fetchCountryCode(latitude, longitude)?.let { repositories ->

      //val r = repositories
      return repositories.countryCode
      //view?.displayRepos(repositories)
    }
  }catch (e: Exception){
    val err = e
    //view?.showError(e)
  }

  /*
  client.fetchCountryCode(0.0, 0.0) { error, result ->
    val res = result
    val err = error
  }

  GlobalScope.launch {

  }
  */

  return "no"
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
