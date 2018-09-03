//
//  Plan.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation

class Plan {
    public var name: String
    public var pinName: String
    public var startDate: Timestamp
    public var endDate: Timestamp
    public var imageRef: String
    public var imageData: Data?
    
    init(name: String, originalName: String, startDate: Timestamp, endDate: Timestamp) {
        self.name = name
        self.pinName = originalName
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = ""
    }
    
    init(name: String, originalName: String, startDate: Timestamp, endDate: Timestamp, imageRef: String) {
        self.name = name
        self.pinName = originalName
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = imageRef
    }
}
