//
//  CountryApiConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public class CountryApiConstants {
    
    struct UrlComponents {
        static let urlProtocol = "https"
        static let domain = "countryapi.io"
        static let path = "/api/all"
    }
    
    struct RequestKeys {
        static let accessKey = "access_key"
    }
    
    struct ResponseKeys {
        static let name = "name"
        static let officialName = "official_name"
        static let topLevelDomain = "topLevelDomain"
        
        static let alpha2Code = "alpha2Code"
        static let alph32Code = "alpha3Code"
        
        static let cioc = "cioc"
        static let numericCode = "numericCode"
        static let callingCode = "callingCode"
        static let capital = "capital"
        
        static let altSpellings = "altSpellings"
        
        static let region = "region"
        static let subregion = "subregion"
        static let population = "population"
        
        // demonyms
        
        static let latLng = "nativeName"
        static let country = "country"
        
        static let area = "area"
        static let gini = "gini"
        
        static let timezones = "timezones"
        static let borders = "borders"
        
        static let nativeNames = "nativeNames"
        static let official = "official"
        static let common = "common"
        
        static let currencies = "currencies"
        static let currencyName = "name"
        static let currencySymbol = "symbol"
        
        static let languages = "languages"
        
        // translations
        
        static let flag = "flag"
        static let flagSmall = "small"
        static let flagMedium = "medium"
        static let flagLarge = "large"
        
        static let regionalBlocks = "regionalBlocs"
        static let regionalBlocksName = "name"
        static let regionalBlocksAcronym = "acronym"
    }
}
