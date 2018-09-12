//
//  AutoCompleteResponse.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 05.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct AutoCompleteResponse: Codable {
    var languageCode: String?
    var countryCode: String?
    var query: String
    var places: [AutoCompletePlace]
}

struct AutoCompletePlace: Codable {
    var kind: String
    var shortName: String
    var longName: String
    var canonicalName: String
    var code: String?
    var lat: Double
    var lng: Double
    var rad: Double
    var regionName: String?
    var regionCode: String?
    var countryName: String?
    var countryCode: String?
}
