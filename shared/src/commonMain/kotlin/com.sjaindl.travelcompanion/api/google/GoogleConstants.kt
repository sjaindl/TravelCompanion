package com.sjaindl.travelcompanion.api.google

object GoogleConstants {
    object UrlComponents {
        const val urlProtocol: String = "https"
        const val domain: String = "maps.googleapis.com"
        const val pathPlace: String = "/maps/api/place/"
        const val pathNearbySearch: String = pathPlace + "nearbysearch/json"
        const val pathPhotos: String = "https://maps.googleapis.com/maps/api/place/photo"
        const val pathAutocomplete: String = pathPlace + "autocomplete/json"
        const val pathPlaceDetail: String = pathPlace + "details/json"
        const val domainSearch: String = "google.com"
        const val pathSearch: String = "/search"
    }

    object ParameterKeys {
        const val key: String = "key"
        const val name: String = "name"
        const val location: String = "location"
        const val rankBy: String = "rankby"
        const val radius: String = "radius"
        const val placeType: String = "type"
        const val strictBounds: String = "strictbounds"
        const val maxWidth: String = "maxwidth"
        const val photoReference: String = "photoreference"
        const val sessionToken: String = "sessiontoken"
        const val input: String = "input"
        const val types: String = "types"
        const val placeId: String = "placeid"
        const val fields: String = "fields"
        const val searchQuery: String = "q"
    }

    object ParameterValues {
        const val rankBy: String = "prominence"
        const val strictBounds: String = "true"
        const val maxWidth: String = "400"
        const val autocompletePlaceType: String = "(regions)"
        const val placeDetailFields: String =
            "address_component,adr_address,formatted_address,geometry,icon,name,permanently_closed,photo,type,url,utc_offset,vicinity"
    }
}
