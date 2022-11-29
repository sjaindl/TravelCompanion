//
//  PlacesAutocompleteResponse.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 02.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Foundation
import shared

struct PlacesAutoCompleteResponse: Codable {
    var predictions: [PlacesPredictions]
    var status: String
}

struct PlacesPredictions: Codable {
    var description: String
    var id: String?
    var placeId: String?
    var reference: String?
    var types: [String]
    var terms: [PlacesAutoCompleteTerm]
    
    private enum CodingKeys: String, CodingKey {
        case description
        case id
        case placeId = "place_id"
        case reference
        case types
        case terms
    }
}

struct PlacesAutoCompleteType: Codable {
    var type: String
}

struct PlacesAutoCompleteTerm: Codable {
    var offset: Int
    var value: String
}
