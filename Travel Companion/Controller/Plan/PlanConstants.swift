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
            case upcoming = "Upcoming Trips"
            case past = "Past Trips"
        }
    }
    
    struct TripDetails {
        enum TripTitles: String {
            case flights = "Flights"
            case publicTransport = "Public Transport"
            case hotels = "Hotels"
            case restaurants = "Restaurants"
            case attractions = "Attractions"
        }
    }
}
