package com.sjaindl.travelcompanion.api.google

object GoogleConstants {
    object UrlComponents {
        const val urlProtocol = "https"
        const val domain = "maps.googleapis.com"
        const val pathPlace = "/maps/api/place/"
        const val pathNearbySearch = pathPlace + "nearbysearch/json"
        const val pathPhotos = "https://maps.googleapis.com/maps/api/place/photo"
        const val pathAutocomplete = pathPlace + "autocomplete/json"
        const val pathPlaceDetail = pathPlace + "details/json"
        const val domainSearch = "google.com"
        const val pathSearch = "/search"
        const val pathReverseGeocode = "maps/api/geocode/json"
    }

    object ParameterKeys {
        const val key = "key"
        const val name = "name"
        const val location = "location"
        const val rankBy = "rankby"
        const val radius = "radius"
        const val placeType = "type"
        const val strictBounds = "strictbounds"
        const val maxWidth = "maxwidth"
        const val photoReference = "photoreference"
        const val sessionToken = "sessiontoken"
        const val input = "input"
        const val types = "types"
        const val placeId = "placeid"
        const val fields = "fields"
        const val searchQuery = "q"

        const val latLng = "latlng"
        const val resultType = "result_type"
    }

    object ParameterValues {
        const val rankBy = "prominence"
        const val strictBounds = "true"
        const val maxWidth = "400"
        const val autocompletePlaceType = "(regions)"
        const val placeDetailFields =
            "address_component,adr_address,formatted_address,geometry,icon,name,permanently_closed,photo,type,url,utc_offset,vicinity"
        const val resultType = "country|administrative_area_level_1|administrative_area_level_2|administrative_area_level_3|locality"
    }
}
