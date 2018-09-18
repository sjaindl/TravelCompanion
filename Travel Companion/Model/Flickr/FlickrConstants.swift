//
//  ParseConstants.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 23.05.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct FlickrConstants {

    struct UrlComponents {
        static let PROTOCOL = "https"
        static let DOMAIN = "api.flickr.com"
        static let PATH = "/services/rest"
    }
    
    struct Flickr {
        static let SearchBBoxHalfWidth = 0.01
        static let SearchBBoxHalfHeight = 0.01
        static let SearchLatRange = (-90.0, 90.0)
        static let SearchLonRange = (-180.0, 180.0)
    }
    
    struct FlickrParameterKeys {
        static let Method = "method"
        static let APIKey = "api_key"
        static let Extras = "extras"
        static let Format = "format"
        static let NoJSONCallback = "nojsoncallback"
        static let SortOrder = "sort"
        static let SafeSearch = "safe_search"
        static let Text = "text"
        static let BoundingBox = "bbox"
        static let Page = "page"
    }
    
    struct FlickrParameterValues {
        static let SearchMethod = "flickr.photos.search"
        static let ResponseFormat = "json"
        static let DisableJSONCallback = "1" /* 1 means "yes" */
        static let ImageSize = "url_m"
        static let UseSafeSearch = "1"
        static let SortOrder = "relevance"
    }
    
    struct FlickrResponseKeys {
        static let Status = "stat"
        static let Photos = "photos"
        static let Photo = "photo"
        static let Title = "title"
        static let ImageSize = "url_m"
        static let Pages = "pages"
        static let PerPage = "perpage"
        static let Total = "total"
    }

    struct ParameterKeys {
        static let RESULTS = "results"
    }
    
    static let MAX_NUMBER_PHOTOS = 4000
}
