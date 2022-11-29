package com.sjaindl.travelcompanion.api.geonames

object GeoNamesConstants {
    object UrlComponents {
        val urlProtocol: String = "https"
        val domain: String = "secure.geonames.org"
        val path: String = "/countryCode"
    }

    object ParameterKeys {
        val latitude: String = "lat"
        val longitude: String = "lng"
        val username: String = "username"
    }
}
