import com.sjaindl.travelcompanion.model.GeoNames.GeoNamesConstants

import io.ktor.client.engine.HttpClientEngine

//import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.takeFrom
import io.ktor.client.HttpClient

//  GeoNamesClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//


class GeoNamesClient(/* private val engine: HttpClientEngine*/) {
    //val sharedInstance = GeoNamesClient()

    private val client by lazy {
        HttpClient() {
            //install(JsonFeature)
        }
    }

    suspend fun fetchCountryCode(latitude: Double, longitude: Double, completionHandler: ( errorString: String?, result: String?) -> Unit) {

        //val client = HttpClient()
        val builder = HttpRequestBuilder()

        val content = client.get<String> {
            builder.url("https://secure.geonames.org/countryCode?lat=37.0856432&lng=25.1488318&username=jaindl.stefan")
        }

        client.close()

        //val client = HttpClient(CIO)
        //val htmlContent = client.get<String>(GeoNamesConstants.UrlComponents().path)

        completionHandler("error.localizedDescription", content)
        /*
        val method = GeoNamesConstants.UrlComponents().path
        val queryItems: Map<String, String> = mapOf(GeoNamesConstants.ParameterKeys().latitude to String(latitude), GeoNamesConstants.ParameterKeys().longitude to String(longitude), GeoNamesConstants.ParameterKeys().username to SecretConstants.userNameGeoNames)
        val url = WebClient.sharedInstance.createUrl(forScheme = GeoNamesConstants.UrlComponents().urlProtocol, forHost = GeoNamesConstants.UrlComponents().domain, forMethod = method, withQueryItems = queryItems)
        val request = WebClient.sharedInstance.buildRequest(withUrl = url, withHttpMethod = WebConstants.ParameterKeys.httpGet)
        WebClient.sharedInstance.taskForDataWebRequest(request, errorDomain = "fetchCountryCode") { data, error  ->
            // Send the desired value(s) to completion handler
            val error = error
            if (error != null) {
                completionHandler(error.localizedDescription, null)
            } else {
                do {
                    val decoder = JSONDecoder()
                    val geocode = decoder.decode(Geocode.self, from = data!!)
                    completionHandler(null, geocode.countryCode)
                } catch {
                    print("Error: ${error}")
                    completionHandler(error.localizedDescription, null)
                }
            }
        }
        */
    }
}
