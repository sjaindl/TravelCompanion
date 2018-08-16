//
//  Country+Extensions.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 15.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation

@objc(Country)
extension Country {
    public override func awakeFromInsert() {
        super.awakeFromInsert()
        creationDate = Date()
    }
}
