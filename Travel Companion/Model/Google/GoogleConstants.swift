//
//  GoogleConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class GoogleConstants {
    struct UrlComponents {
        static let urlProtocol = "https"
        static let domain = "maps.googleapis.com"
        static let path = "/maps/api/place/"
        static let pathNearbySearch = path + "nearbysearch/json"
        static let pathPhotos = "https://maps.googleapis.com/maps/api/place/photo"
    }
    
    struct ParameterKeys {
        static let key = "key"
        static let name = "name"
        static let location = "location"
        static let rankBy = "rankby"
        static let radius = "radius"
        static let placeType = "type"
        static let strictBounds = "strictbounds"
        
        static let maxWidth = "maxwidth"
        static let photoReference = "photoreference"
    }
    
    struct ParameterValues {
        static let rankBy = "prominence"
        static let radius = "25000"
        static let strictBounds = "true"
        
        static let maxWidth = "400"
    }
}
