package com.sjaindl.travelcompanion.api.google

object GoogleConstants {
    object UrlComponents {
        private const val PATH_PLACE = "/maps/api/place/"

        const val URL_PROTOCOL = "https"
        const val DOMAIN_PLACES = "places.googleapis.com"
        const val PATH_PLACE_DETAILS = "v1/places"
        const val DOMAIN_MAPS = "maps.googleapis.com"
        const val PATH_NEARBY_SEARCH = PATH_PLACE + "nearbysearch/json"
        const val PATH_PHOTOS = "https://maps.googleapis.com/maps/api/place/photo"
        const val PATH_AUTOCOMPLETE = "$PATH_PLACE_DETAILS:autocomplete"

        const val DOMAIN_SEARCH = "google.com"
        const val PATH_SEARCH = "/search"
        const val PATH_REVERSE_GEOCODE = "maps/api/geocode/json"
    }

    object ParameterKeys {
        const val KEY = "key"
        const val X_KEY = "X-Goog-Api-Key"
        const val NAME = "name"
        const val LOCATION = "location"
        const val RANK_BY = "rankby"
        const val RADIUS = "radius"
        const val PLACE_TYPE = "type"
        const val STRICT_BOUNDS = "strictbounds"
        const val MAX_WIDTH = "maxwidth"
        const val PHOTO_REFERENCE = "photoreference"
        const val SESSION_TOKEN = "sessionToken"
        const val FIELDS = "fields"
        const val SEARCH_QUERY = "q"

        const val LAT_LNG = "latlng"
        const val RESULT_TYPE = "result_type"
    }

    object ParameterValues {
        const val RANK_BY = "prominence"
        const val STRICT_BOUNDS = "true"
        const val MAX_WIDTH = "400"
        const val PLACE_DETAIL_FIELDS =
            "id,name,displayName,addressComponents,adrFormatAddress,formattedAddress,location,iconBackgroundColor,iconMaskBaseUri,photos,primaryType,websiteUri,utcOffsetMinutes"
        const val RESULT_TYPE = "country|administrative_area_level_1|administrative_area_level_2|administrative_area_level_3|locality"
    }
}
