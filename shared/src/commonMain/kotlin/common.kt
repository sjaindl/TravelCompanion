package com.sjaindl.travelcompanion
//import io.ktor.client.*

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
  return "Kotlin Rocks on ${platformName()}"
}
