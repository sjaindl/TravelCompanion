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
    public var startDate: Timestamp
    public var endDate: Timestamp
    public var imageRef: String
    
    init(name: String, startDate: Timestamp, endDate: Timestamp) {
        self.name = name
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = ""
    }
    
    init(name: String, startDate: Timestamp, endDate: Timestamp, imageRef: String) {
        self.name = name
        self.startDate = startDate
        self.endDate = endDate
        self.imageRef = imageRef
    }
}
