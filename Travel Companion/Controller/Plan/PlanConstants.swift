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
//        enum Trips: Int {
//            case UPCOMING = 0
//            case PAST = 1
//        }
//
        enum TripTitles: String {
            case UPCOMING = "Upcoming Trips"
            case PAST = "Past Trips"
        }
    }
    
    struct TripDetails {
//        enum TripDetails: Int {
//            case FLIGHTS = 0
//            case PUBLIC_TRANSPORT = 1
//            case HOTELS = 2
//            case ATTRACTIONS = 3
//        }
//
        enum TripTitles: String {
            case FLIGHTS = "Flights"
            case PUBLIC_TRANSPORT = "Public Transport"
            case HOTELS = "Hotels"
            case RESTAURANTS = "Restaurants"
            case ATTRACTIONS = "Attractions"
        }
    }
}

