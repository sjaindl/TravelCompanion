package com.sjaindl.travelcompanion.api.geonames

object GeoNamesConstants {
    object UrlComponents {
        const val urlProtocol: String = "https"
        const val domain: String = "secure.geonames.org"
        const val path: String = "/countryCode"

        const val urlProtocolHttp: String = "http"
        const val domainHttp: String = "api.geonames.org"
    }

    object ParameterKeys {
        const val latitude: String = "lat"
        const val longitude: String = "lng"
        const val username: String = "username"
    }
}
