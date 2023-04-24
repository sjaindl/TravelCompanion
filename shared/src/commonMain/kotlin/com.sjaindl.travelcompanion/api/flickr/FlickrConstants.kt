package com.sjaindl.travelcompanion.api.flickr

object FlickrConstants {
    object UrlComponents {
        const val urlProtocol = "https"
        const val domain = "api.flickr.com"
        const val path = "/services/rest"
    }

    object Location {
        const val searchBBoxHalfWidth = 0.01
        const val searchBBoxHalfHeight = 0.01
        val searchLatitudeRange = -90.0 to 90.0
        val searchLongitudeRange = -180.0 to 180.0
    }

    object ParameterKeys {
        const val method = "method"
        const val apiKey = "api_key"
        const val extras = "extras"
        const val format = "format"
        const val noJsonCallback = "nojsoncallback"
        const val sortOrder = "sort"
        const val safeSearch = "safe_search"
        const val text = "text"
        const val boundingBox = "bbox"

        const val limit = "per_page"
        const val offset = "page"
    }

    object ParameterValues {
        const val searchMethod = "flickr.photos.search"
        const val responseFormat = "json"
        const val disableJsonCallback = "1" /* 1 means "yes" */
        const val imageSize = "url_m"
        const val useSafeSearch = "1"
        const val sortOrder = "relevance"

        const val limit = 20
    }

    object ResponseKeys {
        const val photos = "photos"
        const val photo = "photo"
        const val title = "title"
        const val imageSize = "url_m"
    }
}
