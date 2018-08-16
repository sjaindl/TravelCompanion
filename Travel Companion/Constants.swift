//
//  Constants.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 15.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct Constants {
    
    static let ALBUM_CELL_REUSE_ID = "photoCell"
    static let EXPLORE_PHOTOS_SEGUE_ID = "explorePhotos"
    static let EXPLORE_SEGUE_ID = "explore"
    static let EXPLORE_DETAIL_SEGUE_ID = "exploreDetail"
    static let PLAN_SEGUE_ID = "plan"
    static let REMEMBER_SEGUE_ID = "remember"
    
    struct CoreData {
        static let PLACEHOLDER_IMAGE = "placeholder"
        static let SORT_KEY = "creationDate"
        static let CACHE_NAME_PINS = "pin"
        static let CACHE_NAME_PHOTOS = "photos"
        static let CACHE_NAME_COUNTRIES = "countries"
        
        static let PHOTO_LIMIT = 40
    }
    
    struct UserDefaults {
        static let USER_DEFAULT_LAUNCHED_BEFORE = "hasLaunchedBefore"
        static let USER_DEFAULT_MAP_LATITUDE = "mapLatitude"
        static let USER_DEFAULT_MAP_LONGITUDE = "mapLongitude"
        static let USER_DEFAULT_ZOOM_LEVEL = "zoomLevel"
        
        static let STANDARD_ZOOM_LEVEL = 6.0
        static let STANDARD_LATITUDE = 47.0
        static let STANDARD_LONGITUDE = 15.5
    }
    
    enum FetchType: Int {
        case Country = 1
        case Place = 2
        case LatLong = 3
    }
}
