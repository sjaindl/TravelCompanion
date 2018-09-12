//
//  Flight.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation

class Flight: NSObject, Plannable, Codable {
    
    public var date: Timestamp
    public var depPlace: String
    public var arrPlace: String
    public var depTime: String
    public var arrTime: String
    public var flight: String
    public var duration: Int?
    public var airline: String
    
    public var layoverDuration: String?
    public var depTerminal: String?
    public var arrTerminal: String?
    public var aircraft: String?
    public var airlineUrl: String?
    
    init(date: Timestamp, depPlace: String, arrPlace: String, depTime: String, arrTime: String, flight: String, duration: Int, airline: String) {
        self.date = date
        self.depPlace = depPlace
        self.arrPlace = arrPlace
        self.depTime = depTime
        self.arrTime = arrTime
        self.flight = flight
        self.duration = duration
        self.airline = airline
    }
    
    func description() -> String {
        return "\(airline): \(flight)"
    }
    
    func details() -> String {
        return "\(UiUtils.formatTimestampForDisplay(timestamp: date)), \(depTime): \(depPlace) - \(arrPlace)"
    }
    
    func imageUrl() -> String? {
        if let airlineUrl = airlineUrl {
            return "\(Rome2RioConstants.UrlComponents.PROTOCOL)://\(Rome2RioConstants.UrlComponents.DOMAIN)\(airlineUrl)"
        }
        return ""
    }
}
