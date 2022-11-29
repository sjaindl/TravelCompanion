package com.sjaindl.travelcompanion.api.google

object GoogleConstants {
    object UrlComponents {
        val urlProtocol: String = "https"
        val domain: String = "maps.googleapis.com"
        val pathPlace: String = "/maps/api/place/"
        val pathNearbySearch: String = pathPlace + "nearbysearch/json"
        val pathPhotos: String = "https://maps.googleapis.com/maps/api/place/photo"
        val pathAutocomplete: String = pathPlace + "autocomplete/json"
        val pathPlaceDetail: String = pathPlace + "details/json"
        val domainSearch: String = "google.com"
        val pathSearch: String = "/search"
    }

    object ParameterKeys {
        val key: String = "key"
        val name: String = "name"
        val location: String = "location"
        val rankBy: String = "rankby"
        val radius: String = "radius"
        val placeType: String = "type"
        val strictBounds: String = "strictbounds"
        val maxWidth: String = "maxwidth"
        val photoReference: String = "photoreference"
        val sessionToken: String = "sessiontoken"
        val input: String = "input"
        val types: String = "types"
        val placeId: String = "placeid"
        val fields: String = "fields"
        val searchQuery: String = "q"
    }

    object ParameterValues {
        val rankBy: String = "prominence"
        val strictBounds: String = "true"
        val maxWidth: String = "400"
        val autocompletePlaceType: String = "(regions)"
        val placeDetailFields: String = "address_component,adr_address,formatted_address,geometry,icon,name,permanently_closed,photo,type,url,utc_offset,vicinity"
    }
}
