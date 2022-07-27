//
//  Constants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 15.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct Constants {
    
    struct Plannables {
        static let flight = "flight"
        static let publicTransport = "publicTransport"
        static let hotel = "hotel"
        static let restaurant = "restaurant"
        static let attraction = "attraction"
    }
    
    struct ReuseIds {
        static let planCell = "planReuseCellId"
        static let planDetailCell = "planDetailReuseCellId"
        static let planDetailWithImageCell = "planDetailWithImageReuseCellId"
        static let albumCell = "photoCell"
        static let transportDetailWithoutImageCell = "transportDetail"
        static let transportDetailWithImageCell = "transportDetailSection"
        static let originCellReuseIdCell = "originCellReuseId"
        static let destinationCellReuseIdCell = "destinationCellReuseId"
        static let googlePlaceCellReuseId = "googlePlaceCell"
    }
    
    struct Segues {
        static let explore = "explore"
        static let explorePhotos = "explorePhotos"
        static let exploreWiki = "wiki"
        static let exploreDetail = "exploreDetail"
        static let searchPlaces = "searchPlaces"
        
        static let plan = "plan"
        static let addPlan = "addPlan"
        static let planDetail = "planDetail"
        static let planChoosePhoto = "choosePlanPhoto"
        static let planAddFlight = "addFlight"
        static let planAddPublicTransport = "addPublicTransport"
        static let planAddDestination = "addDestination"
        static let planTransportDate = "transportDate"
        static let planAddTransportDetail = "addTransportDetail"
        static let planAddNotes = "addNotes"
        static let planAddPlace = "addPlace"
        static let planChangeDate = "changeDate"
        
        static let remember = "remember"
        static let rememberDetail = "rememberDetail"
        static let photoDetail = "photoDetail"
    }
    
    struct ControllerIds {
        static let addPlacePreview = "addPlacePreview"
        static let personalInformationViewController = "PersonalInformationViewController"
        static let profileViewController = "ProfileViewController"
    }
    
    struct CoreData {
        static let placeholderImage = "placeholder"
        static let sortKey = "creationDate"
        static let cacheNamePins = "pin"
        static let cacheNamePhotos = "photos"
        static let cacheNameCountries = "countries"
        
        static let photoLimit = 40
    }
    
    struct UserDefaults {
        static let launchedBefore = "hasLaunchedBefore"
        static let mapLatitude = "mapLatitude"
        static let mapLongitude = "mapLongitude"
        static let zoomLevel = "zoomLevel"
        
        static let zoomLevelStandard = 6.0
        static let zoomLevelStandardGooglePlaceAdd = 14.0
        //Coordinates of Apple headquarter
        static let mapLatitudeStandard = 37.33182
        static let mapLongitudeStandard = -122.03118
    }
    
    static let zoomLevelDetail = 10.0
    
    struct RemoteConfig {
        struct Keys {
            static let photoResizingHeight = "firestorePhotoResizingHeight"
            static let photoResizingWidth = "firestorePhotoResizingWidth"
            static let transportSearchAutocomplete = "transportSearchAutocomplete"
            static let numberOfPhotosToDownload = "numberOfPhotosToDownload"
        }
        
        struct LocalDefaultValues {
            static let photoResizingHeight = 800
            static let photoResizingWidth = 800
            static let transportSearchAutocomplete = true
        }
    }
}
