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
    }
    
    struct Ids {
        struct Place {
            static let PLACE_ID = "placeId"
            static let LATITUDE = "latitude"
            static let LONGITUDE = "longitude"
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
