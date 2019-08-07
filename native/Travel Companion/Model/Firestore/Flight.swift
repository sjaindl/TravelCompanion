//
//  Flight.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import Foundation

class Flight: NSObject, Plannable {
    
    public var id: String
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
    
    public var notes: String?
    
    init(date: Timestamp, depPlace: String, arrPlace: String, depTime: String, arrTime: String, flight: String, duration: Int, airline: String) {
        self.date = date
        self.depPlace = depPlace
        self.arrPlace = arrPlace
        self.depTime = depTime
        self.arrTime = arrTime
        self.flight = flight
        self.duration = duration
        self.airline = airline
        self.id = "\(self.flight)-\(self.depPlace)-\(self.depTime)-\(self.date)" //this should be unique
    }
    
    func getId() -> String {
        return id
    }
    
    func description() -> String {
        return "\(FormatUtils.formatTimestampForDisplay(timestamp: date)), \(depPlace) - \(arrPlace)"
    }
    
    func details() -> NSMutableAttributedString {
        var durationInfo = ""
        
        if let duration = duration {
            durationInfo = "\(duration / 60)h \(duration % 60) min"
        }
        
        return NSMutableAttributedString(string: "\(airline) \(flight): \(depTime)-\(arrTime)\n"
            + "\(aircraft ?? ""), \(durationInfo)")
    }
    
    func imageUrl() -> String? {
        if let airlineUrl = airlineUrl {
            return "\(Rome2RioConstants.UrlComponents.urlProtocol)://\(Rome2RioConstants.UrlComponents.domain)\(airlineUrl)"
        }
        return ""
    }
    
    func getNotes() -> String {
        return notes ?? ""
    }
    
    func setNotes(notes: String) {
        self.notes = notes
    }
    
    func encode() -> [String: Any] {
        return try! FirestoreEncoder().encode(self)
    }
}
