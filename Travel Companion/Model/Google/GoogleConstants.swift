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
        static let PROTOCOL = "https"
        static let DOMAIN = "maps.googleapis.com"
        static let PATH = "/maps/api/place/"
        static let PATH_NEARBYSEARCH = PATH + "nearbysearch/json"
        static let PATH_PHOTOS = "https://maps.googleapis.com/maps/api/place/photo"
    }
    
    struct ParameterKeys {
        static let Key = "key"
        static let Name = "name"
        static let Location = "location"
        static let RankBy = "rankby"
        static let Radius = "radius"
        static let PlaceType = "type"
        static let StrictBounds = "strictbounds"
        
        static let MaxWidth = "maxwidth"
        static let PhotoReference = "photoreference"
    }
    
    struct ParameterValues {
        static let RankBy = "prominence"
        static let Radius = "50000"
        static let StrictBounds = "true"
        
        static let MaxWidth = "400"
    }
}
