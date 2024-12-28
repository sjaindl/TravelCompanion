package com.sjaindl.travelcompanion.api.country.countryapi

class CountryApiConstants {
    object UrlComponents {
        const val urlProtocol = "https"
        const val domain = "countryapi.io"
        const val path = "api/all"
    }

    object RequestKeys {
        const val accessKey = "access_key"
    }

    object ResponseKeys {
        const val name = "name"
        const val officialName = "official_name"
        const val topLevelDomain = "topLevelDomain"

        const val alpha2Code = "alpha2Code"
        const val alph32Code = "alpha3Code"

        const val cioc = "cioc"
        const val numericCode = "numericCode"
        const val callingCode = "callingCode"
        const val capital = "capital"

        const val altSpellings = "altSpellings"

        const val region = "region"
        const val subregion = "subregion"
        const val population = "population"

        // demonyms

        const val latLng = "nativeName"
        const val country = "country"

        const val area = "area"
        const val gini = "gini"

        const val timezones = "timezones"
        const val borders = "borders"

        const val nativeNames = "nativeNames"
        const val official = "official"
        const val common = "common"

        const val currencies = "currencies"
        const val currencyName = "name"
        const val currencySymbol = "symbol"

        const val languages = "languages"

        // translations

        const val flag = "flag"
        const val flagSmall = "small"
        const val flagMedium = "medium"
        const val flagLarge = "large"

        const val regionalBlocks = "regionalBlocs"
        const val regionalBlocksName = "name"
        const val regionalBlocksAcronym = "acronym"
    }
}
