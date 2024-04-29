package com.sjaindl.travelcompanion.di

import com.sjaindl.travelcompanion.api.HttpClientBuilder
import com.sjaindl.travelcompanion.api.HttpResponseHandler
import com.sjaindl.travelcompanion.api.country.countryapi.CountryApiClient
import com.sjaindl.travelcompanion.api.country.countryapi.CountryApiClientImpl
import com.sjaindl.travelcompanion.api.country.restcountries.RestCountriesClient
import com.sjaindl.travelcompanion.api.country.restcountries.RestCountriesClientImpl
import com.sjaindl.travelcompanion.api.flickr.FlickrClient
import com.sjaindl.travelcompanion.api.flickr.FlickrClientImpl
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClient
import com.sjaindl.travelcompanion.api.geonames.GeoNamesClientImpl
import com.sjaindl.travelcompanion.api.google.GoogleClient
import com.sjaindl.travelcompanion.api.google.GoogleClientImpl
import com.sjaindl.travelcompanion.api.wiki.WikiClient
import com.sjaindl.travelcompanion.api.wiki.WikiClientImpl
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object ApiModule {
    val container = DI.Module(name = "api") {
        bindProvider {
            HttpClientBuilder()
                .withJsonSerialization()
                .withLogging()
                .build()
        }

        bindSingleton { HttpResponseHandler(instance()) }

        bindSingleton<RestCountriesClient> { RestCountriesClientImpl(instance()) }
        bindSingleton<FlickrClient> { FlickrClientImpl(instance()) }
        bindSingleton<WikiClient> { WikiClientImpl(instance()) }
        bindSingleton<GeoNamesClient> { GeoNamesClientImpl(instance()) }
        bindSingleton<GoogleClient> { GoogleClientImpl(instance()) }
        bindSingleton<CountryApiClient> { CountryApiClientImpl(instance()) }
    }
}
