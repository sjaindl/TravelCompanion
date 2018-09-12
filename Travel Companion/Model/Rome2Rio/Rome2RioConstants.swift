//
//  Rome2RioConstants.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 04.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct Rome2RioConstants {
    struct UrlComponents {
        static let PROTOCOL = "https"
        static let DOMAIN = "free.rome2rio.com"
        static let PATH = "/api/1.4/json/"
        static let PATH_AUTOCOMPLETE = PATH + "Autocomplete"
        static let PATH_SEARCH = PATH + "Search"
        static let PATH_FLIGHTS = PATH + "Flights"
    }
    
    struct ParameterKeys {
        static let Key = "key"
        static let Query = "query"
        
        static let OriginName = "oName"
        static let DestinationName = "dName"
        
        static let Action = "action"
        static let FlightOriginName = "origin"
        static let FlightDestinationName = "destination"
        static let DepartureDate = "outDate"
        static let ReturnDate = "retDate"
        static let NumberOfAdults = "adults"
        
        static let noAir = "noAir"
        static let noAirLeg = "noAirLeg"
        static let noRail = "noRail"
        static let noBus = "noBus"
        static let noFerry = "noFerry"
        static let noCar = "noCar"
        static let noBikeshare = "noBikeshare"
        static let noRideshare = "noRideshare"
        static let noTowncar = "noTowncar"
        static let noCommuter = "noCommuter"
        static let noSpecial = "noSpecial"
        static let noMinorStart = "noMinorStart"
        static let noMinorEnd = "noMinorEnd"
        static let noPath = "noPath"
    }
    
    struct ParameterValues {
        static let ActionStartSearch = "StartSearch"
        static let ActionPollSearch = "PollSearch"
    }
    
    struct ResponseKeys {
        static let Places = "places"
        static let PlaceType = "kind"
        static let PlaceNameShort = "shortName"
        static let PlaceNameLong = "longName"
        static let StationCode = "code"
        static let Latitude = "lat"
        static let Longitude = "lng"
    }
}
