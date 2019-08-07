//
//  PublicTransport.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 18.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import Foundation

class PublicTransport: NSObject, Plannable {
    public var id: String
    public var date: Timestamp
    public var depPlace: String
    public var arrPlace: String
    public var agencyName: String?
    public var agencyUrl: String?
    
    public var stopDuration: Double?
    public var transitDuration: Double?
    public var stopPlace:String?
    public var vehicle: String
    
    public var notes: String?
    
    init(date: Timestamp, vehicle: String, depPlace: String, arrPlace: String, agencyName: String?, agencyUrl: String?, stopDuration: Double?, transitDuration: Double?, stopPlace: String?) {
        self.date = date
        self.vehicle = vehicle
        self.depPlace = depPlace
        self.arrPlace = arrPlace
        self.agencyName = agencyName
        self.agencyUrl = agencyUrl
        self.stopDuration = stopDuration
        self.transitDuration = transitDuration
        self.stopPlace = stopPlace
        
        self.id = "\(self.vehicle)-\(self.agencyName ?? "noAgency")-\(self.depPlace)-\(self.arrPlace)-\(self.stopPlace ?? "noStop")-\(self.date)" //this should be unique
    }
    
    func getId() -> String {
        return id
    }
    
    func description() -> String {
        return "\(FormatUtils.formatTimestampForDisplay(timestamp: date)), \(depPlace) - \(arrPlace)" //TODO: Move to FormatUtils
    }
    
    func details() -> NSMutableAttributedString {
        var durationInfo = ""
        
        if let stopDuration = stopDuration {
            var duration: Int = Int(stopDuration)
            if let transitDuration = transitDuration {
                duration += Int(transitDuration)
            }
            
            durationInfo = "\(duration / 60)h \(duration % 60) min"
        }
        
        var detailText = ""
        if let agencyName = agencyName {
            detailText = "\(agencyName), "
        }
        
        if let stopPlace = stopPlace {
            detailText += "\(stopPlace), "
        }
        
        return NSMutableAttributedString(string: "\(vehicle), \(detailText), \(durationInfo)".trimmingCharacters(in: CharacterSet(charactersIn: ", ")))
    }
    
    func imageUrl() -> String? {
        if let agencyUrl = agencyUrl {
            return "\(Rome2RioConstants.UrlComponents.urlProtocol)://\(Rome2RioConstants.UrlComponents.domain)\(agencyUrl)"
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
