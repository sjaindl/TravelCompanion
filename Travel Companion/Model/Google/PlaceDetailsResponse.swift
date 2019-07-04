//
//  PlaceDetailsResponse.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 03.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Foundation

struct PlacesDetailsResponse: Codable {
    var html_attributions: [String]
    var result: PlaceDetailResult
    var status: String
    
    private enum CodingKeys: String, CodingKey {
        case html_attributions = "html_attributions"
        case result
        case status
    }
}

struct PlaceDetailResult: Codable {
    var addressComponents: [PlaceDetailAddressComponent]?
    var adrAddress: String?
    var formattedAddress: String?
    var geometry: PlaceDetailGeometry
    var icon: String?
    var name: String?
    var permanentlyClosed: String?
    var url: String?
    var utcOffset: Int?
    var vicinity: String?
    var types: [String?]
    //photos
    
    private enum CodingKeys: String, CodingKey {
        case addressComponents = "address_components"
        case adrAddress = "adr_address"
        case formattedAddress = "formatted_address"
        case geometry
        case icon
        case name
        case permanentlyClosed = "permanently_closed"
        case utcOffset = "utc_offset"
        case vicinity
        case types
    }
}

struct PlaceDetailGeometry: Codable {
    var location: PlaceDetailLocation
    //viewport
}

struct PlaceDetailLocation: Codable {
    var lat: Double
    var lng: Double
}

struct PlaceDetailAddressComponent: Codable {
    var longName: String
    var shortName: String
    var types: [String]
    
    private enum CodingKeys: String, CodingKey {
        case longName = "long_name"
        case shortName = "short_name"
        case types
    }
}
