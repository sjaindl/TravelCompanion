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
        static let urlProtocol = "https"
        static let domain = "api.flickr.com"
        static let path = "/services/rest"
    }
    
    struct Location {
        static let searchBBoxHalfWidth = 0.01
        static let searchBBoxHalfHeight = 0.01
        static let searchLatitudeRange = (-90.0, 90.0)
        static let searchLongitudeRange = (-180.0, 180.0)
    }
    
    struct ParameterKeys {
        static let method = "method"
        static let apiKey = "api_key"
        static let extras = "extras"
        static let format = "format"
        static let noJsonCallback = "nojsoncallback"
        static let sortOrder = "sort"
        static let safeSearch = "safe_search"
        static let text = "text"
        static let boundingBox = "bbox"
    }
    
    struct ParameterValues {
        static let searchMethod = "flickr.photos.search"
        static let responseFormat = "json"
        static let disableJsonCallback = "1" /* 1 means "yes" */
        static let imageSize = "url_m"
        static let useSafeSearch = "1"
        static let sortOrder = "relevance"
    }
    
    struct ResponseKeys {
        static let photos = "photos"
        static let photo = "photo"
        static let title = "title"
        static let imageSize = "url_m"
    }
}
