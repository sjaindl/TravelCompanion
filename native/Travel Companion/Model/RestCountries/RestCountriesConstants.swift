//
//  RestCountriesConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 16.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class RestCountriesConstants {
    
    struct UrlComponents {
        static let urlProtocol = "http"
        static let domain = "api.countrylayer.com"
        static let path = "/v2/alpha/"
    }
    
    struct RequestKeys {
        static let accessKey = "access_key"
        static let code = "code"
    }
    
    struct ResponseKeys {
        static let alphaCode = "alpha2Code"
        static let name = "name"
        static let callingCodes = "callingCodes"
        
        static let region = "region"
        static let subregion = "subregion"
        
        static let regionalBlocks = "regionalBlocs"
        static let regionalBlocksName = "name"
        static let regionalBlocksAcronym = "acronym"
        
        static let nativeName = "nativeName"
        
        static let languages = "languages"
        static let languageName = "name"
        
        static let topLevelDomain = "topLevelDomain"
        static let flag = "flag"
        static let timezones = "timezones"
        
        static let currencies = "currencies"
        static let currencyName = "name"
        static let currencyCode = "code"
        static let currencySymbol = "symbol"
        
        static let population = "population"
        static let area = "area"
        static let capital = "capital"
    }
}
