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
        static let PROTOCOL = "https"
        static let DOMAIN_WIKIPEDIA = "en.wikipedia.org"
        static let DOMAIN_WIKIVOYAGE = "en.wikivoyage.org"
        static let PATH = "/w/api.php"
    }
    
    struct ParameterKeys {
        static let Action = "action"
        static let Format = "format"
        static let Titles = "titles"
        static let Prop = "prop"
        static let Inprop = "inprop"
    }
    
    struct ParameterValues {
        static let QUERY = "query"
        static let ResponseFormat = "json"
        static let PropInfo = "info"
        static let Inprop = "url"
    }
    
    struct ResponseKeys {
        static let Query = "query"
        static let Pages = "pages"
        static let PageId = "pageid"
        static let FullUrl = "fullurl"
    }
}
