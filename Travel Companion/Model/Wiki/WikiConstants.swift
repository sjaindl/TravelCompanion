//
//  WikiConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct WikiConstants {
    
    struct UrlComponents {
        static let urlProtocol = "https"
        static let domainWikipedia = "en.wikipedia.org"
        static let domainWikiVoyage = "en.wikivoyage.org"
        static let path = "/w/api.php"
    }
    
    struct ParameterKeys {
        static let action = "action"
        static let format = "format"
        static let titles = "titles"
        static let prop = "prop"
        static let inprop = "inprop"
    }
    
    struct ParameterValues {
        static let query = "query"
        static let responseFormat = "json"
        static let propInfo = "info"
        static let inprop = "url"
    }
    
    struct ResponseKeys {
        static let fullUrl = "fullurl"
    }
}
