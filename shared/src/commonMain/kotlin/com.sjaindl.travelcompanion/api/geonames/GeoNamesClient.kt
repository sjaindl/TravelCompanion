package com.sjaindl.travelcompanion.api.geonames

interface GeoNamesClient {

    suspend fun fetchCountryCode(latitude: Double, longitude: Double): String?
}
