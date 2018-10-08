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
        static let urlProtocol = "https"
        static let domain = "secure.geonames.org"
        static let path = "/countryCode"
    }
    
    struct ParameterKeys {
        static let latitude = "lat"
        static let longitude = "lng"
        static let username = "username"
    }
}
