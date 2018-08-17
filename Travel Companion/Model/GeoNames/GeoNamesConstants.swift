//
//  GeoNamesConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class GeoNamesConstants {
    
    struct UrlComponents {
        static let PROTOCOL = "https"
        static let DOMAIN = "secure.geonames.org"
        static let PATH = "/countryCode"
    }
    
    struct ParameterKeys {
        static let LATITUDE = "lat"
        static let LONGITUDE = "lng"
        static let USERNAME = "username"
    }
    
    struct ParameterValues {
        static let USERNAME = "jaindl.stefan"
    }
}
