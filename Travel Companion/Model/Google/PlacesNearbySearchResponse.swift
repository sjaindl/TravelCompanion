//
//  PlacesNearbySearchResponse.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 28.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class PlacesNearbySearchResponse: Codable {
    var htmlAttributions: [String]
    var nextPageToken: String?
    var results: [GooglePlace]
    var status: String
    
    private enum CodingKeys: String, CodingKey {
        case htmlAttributions = "html_attributions"
        case nextPageToken = "next_page_token"
        case results
        case status
    }
}
