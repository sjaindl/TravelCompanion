//
//  Constants.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 15.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct Constants {
    
    struct PLANNABLES {
        static let FLIGHT = "flight"
        static let PUBLIC_TRANSPORT = "publicTransport"
        static let HOTEL = "hotel"
        static let RESTAURANT = "restaurant"
        static let ATTRACTION = "attraction"
    }
    
    struct REUSE_IDS {
        static let PLAN_CELL_REUSE_ID = "planReuseCellId"
        static let PLAN_DETAIL_CELL_REUSE_ID = "planDetailReuseCellId"
        static let PLAN_DETAIL_WITH_IMAGE_CELL_REUSE_ID = "planDetailWithImageReuseCellId"
        static let ALBUM_CELL_REUSE_ID = "photoCell"
        static let TRANSPORT_DETAIL_WITHOUT_IMAGE_CELL_REUSE_ID = "transportDetail"
        static let TRANSPORT_DETAIL_WITH_IMAGE_CELL_REUSE_ID = "transportDetailSection"
    }
    
    struct SEGUES {
        static let EXPLORE_PHOTOS_SEGUE_ID = "explorePhotos"
        static let EXPLORE_WIKI_SEGUE_ID = "wiki"
        static let EXPLORE_SEGUE_ID = "explore"
        static let EXPLORE_DETAIL_SEGUE_ID = "exploreDetail"
        static let PLAN_SEGUE_ID = "plan"
        static let REMEMBER_SEGUE_ID = "remember"
        static let PHOTO_DETAIL_SEGUE_ID = "photoDetail"
        static let ADD_PLAN_SEGUE_ID = "addPlan"
        static let PLAN_DETAIL_SEGUE_ID = "planDetail"
        static let PLAN_CHOOSE_PHOTO_SEGUE_ID = "choosePlanPhoto"
        static let PLAN_ADD_FLIGHT = "addFlight"
        static let PLAN_ADD_PUBLIC_TRANSPORT = "addPublicTransport"
        static let PLAN_ADD_TRANSPORT_DETAIL = "addTransportDetail"
        static let PLAN_ADD_NOTES = "addNotes"
    }
    
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
}
