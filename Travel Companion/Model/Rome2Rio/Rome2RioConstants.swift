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
        static let urlProtocol = "https"
        static let domain = "free.rome2rio.com"
        static let path = "/api/1.4/json/"
        static let pathAutocomplete = path + "Autocomplete"
        static let pathSearch = path + "Search"
    }
    
    struct ParameterKeys {
        static let key = "key"
        static let query = "query"
        
        static let originName = "oName"
        static let destinationName = "dName"
        
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
}
