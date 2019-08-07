//
//  Pin+Extensions.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 17.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation

@objc(Pin)
extension Pin {
    public override func awakeFromInsert() {
        super.awakeFromInsert()
        creationDate = Date()
    }
}
