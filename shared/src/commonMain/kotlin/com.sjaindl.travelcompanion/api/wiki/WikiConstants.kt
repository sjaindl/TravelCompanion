package com.sjaindl.travelcompanion.api.wiki

object WikiConstants {
    object UrlComponents {
        val urlProtocol = "https"
        val domainWikipedia = "en.wikipedia.org"
        val domainWikiVoyage = "en.wikivoyage.org"
        val path = "/w/api.php"
        val wikiLinkPath = "curid"
    }

    object ParameterKeys {
        val action = "action"
        val format = "format"
        val titles = "titles"
    }

    object ParameterValues {
        val query = "query"
        val responseFormat = "json"
    }
}
