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
        static let users = "users"
        static let places = "places"
        static let plans = "plans"
        
        static let fligths = "fligths"
        static let publicTransport = "publicTransport"
        static let hotels = "hotels"
        static let restaurants = "restaurants"
        static let attractions = "attractions"
        
        static let photos = "photos"
    }
    
    struct Ids {
        struct Place {
            static let placeId = "placeId"
            static let latitude = "latitude"
            static let longitude = "longitude"
            static let name = "name"
        }
        
        struct Plan {
            static let name = "name"
            static let pinName = "pinName"
            static let startDate = "startDate"
            static let endDate = "endDate"
            static let imageReference = "image"
            
            static let path = "path"
        }
        
        struct User {
            static let userId = "uid"
            static let email = "email"
            static let displayName = "displayName"
            static let providerId = "providerID"
            static let photoUrl = "photoURL"
            static let phoneNumber = "phoneNumber"
        }
    }    
}
