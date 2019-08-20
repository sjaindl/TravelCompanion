package com.sjaindl.travelcompanion.model.GeoNames

//
//  GeoNamesConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

class GeoNamesConstants {

    data class UrlComponents(
        val urlProtocol: String = "https",
        val domain: String = "secure.geonames.org",
        val path: String = "/countryCode")

    data class ParameterKeys(
        val latitude: String = "lat",
        val longitude: String = "lng",
        val username: String = "username")

}
