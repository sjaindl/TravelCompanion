//
//  PlacesNearbySearchResponse.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 28.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class PlacesNearbySearchResponse: Codable {
    var html_attributions: [String]
    var next_page_token: String?
    var results: [GooglePlace]
    var status: String
}
