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
        static let pathPlace = "/maps/api/place/"
        static let pathNearbySearch = pathPlace + "nearbysearch/json"
        static let pathPhotos = "https://maps.googleapis.com/maps/api/place/photo"
        static let pathAutocomplete = pathPlace + "autocomplete/json"
        static let pathPlaceDetail = pathPlace + "details/json"
        
        static let domainSearch = "google.com"
        static let pathSearch = "/search"
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
        
        static let sessionToken = "sessiontoken"
        static let input = "input"
        static let types = "types"
        
        static let placeId = "placeid"
        static let fields = "fields"
        
        static let searchQuery = "q"
    }
    
    struct ParameterValues {
        static let rankBy = "prominence"
        static let strictBounds = "true"
        
        static let maxWidth = "400"
        
        static let autocompletePlaceType = "(regions)"
        
        static let placeDetailFields = "address_component,adr_address,formatted_address,geometry,icon,name,permanently_closed,photo,type,url,utc_offset,vicinity" // basic data
    }
}
