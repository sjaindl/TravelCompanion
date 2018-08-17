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
        static let PROTOCOL = "https"
        static let DOMAIN = "restcountries.eu"
        static let PATH = "/rest/v2/alpha/"
    }
    
    struct ResponseKeys {
        static let ALPHA_CODE = "alpha2Code"
        static let NAME = "name"
        static let CALLING_CODES = "callingCodes"
        
        static let REGION = "region"
        static let SUBREGION = "subregion"
        
        static let REGIONAL_BLOCKS = "regionalBlocs"
        static let REGIONAL_BLOCKS_NAME = "name"
        static let REGIONAL_BLOCKS_ACRONYM = "acronym"
        
        static let NATIVE_NAME = "nativeName"
        
        static let LANGUAGES = "languages"
        static let LANGUAGE_NAME = "name"
        
        static let DOMAINS = "topLevelDomain"
        static let FLAG = "flag"
        static let TIMEZONES = "timezones"
        
        static let CURRENCIES = "currencies"
        static let CURRENCY_NAME = "name"
        static let CURRENCY_CODE = "code"
        static let CURRENCY_SYMBOL = "symbol"
        
        static let POPULATION = "population"
        static let AREA = "area"
        static let CAPITAL = "capital"
    }
}
