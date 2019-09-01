package com.sjaindl.travelcompanion

import io.ktor.client.*
import GeoNamesClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
  return "Kotlin Rocks on ${platformName()}"
}

suspend fun fetchCode(): String {
  val client = GeoNamesClient()

  client.fetchCountryCode(0.0, 0.0) { error, result ->
    val res = result
    val err = error
  }

  GlobalScope.launch {

  }

  return ""
}
