package main.model.GeoNames

import io.ktor.client.response.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.JsonException

//  WebClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

class WebClient() {

    companion object {
        val instance = WebClient()
    }

    /*
    fun performBasicWebResponseChecks(response: HttpResponse, json: String) {

        if (response.status.value < HttpStatusCode.OK.value || response.status.value >= HttpStatusCode.MultipleChoices.value) {

            var errorMessage =
                "Response returned status code ${response.status.value} (${response.status.description})"

            try {
                val error = Json.nonstrict.parse(ErrorRepository.serializer(), json)
                errorMessage = "${error.error} (${error.status})"
            } catch (jsonError: JsonException) {
                print("No detail error message in json response available")
            }

            throw Exception(errorMessage)
        }
    }

     */

}
