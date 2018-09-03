//
//  FirestoreConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class FirestoreConstants {
    struct Collections {
        static let USERS = "users"
        static let PLACES = "places"
        static let PLANS = "plans"
        
        static let FLIGTHS = "fligths"
        static let PUBLIC_TRANSPORT = "publicTransport"
        static let HOTELS = "hotels"
        static let RESTAURANTS = "restaurants"
        static let ATTRACTIONS = "attractions"
    }
    
    struct Ids {
        struct Place {
            static let PLACE_ID = "placeId"
            static let LATITUDE = "latitude"
            static let LONGITUDE = "longitude"
            static let NAME = "name"
        }
        
        struct Plan {
            static let NAME = "name"
            static let PIN_NAME = "pinName"
            static let START_DATE = "startDate"
            static let END_DATE = "endDate"
            static let IMAGE_REFERENCE = "image"
        }
        
        struct User {
            static let UID = "uid"
            static let EMAIL = "email"
            static let NAME = "displayName"
            static let PROVIDER = "providerID"
            static let PHOTO_URL = "photoURL"
            static let PHONE = "phoneNumber"
        }
    }    
}
