//
//  PlanConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct PlanConstants {
    
    struct Trips {
        enum TripTitles: String {
            case upcoming = "upcomingTrips"
            case past = "pastTrips"
        }
    }
    
    struct TripDetails {
        enum TripTitles: String {
            case flights = "flights"
            case publicTransport = "publicTransport"
            case hotels = "hotels"
            case restaurants = "restaurants"
            case attractions = "attractions"
        }
    }
}
