package com.sjaindl.travelcompanion.di

import com.sjaindl.travelcompanion.api.country.countryapi.CountryApiClient
import com.sjaindl.travelcompanion.api.country.restcountries.RestCountriesClient
import com.sjaindl.travelcompanion.api.flickr.FlickrClient
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClient
import com.sjaindl.travelcompanion.api.google.GoogleClient
import com.sjaindl.travelcompanion.api.wiki.WikiClient
import org.kodein.di.DI
import org.kodein.di.direct
import org.kodein.di.instance
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object TCInjector {
    private val container = DI.lazy {
        import(ApiModule.container)
    }

    val countryApiClient = container.direct.instance<CountryApiClient>()
    val restCountriesClient = container.direct.instance<RestCountriesClient>()
    val flickrClient = container.direct.instance<FlickrClient>()
    val wikiClient = container.direct.instance<WikiClient>()
    val geoNamesClient = container.direct.instance<GeoNamesClient>()
    val googleClient = container.direct.instance<GoogleClient>()
}
