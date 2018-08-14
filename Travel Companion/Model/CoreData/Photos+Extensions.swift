//
//  FlickrPhoto.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 19.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation

@objc(Photos)
extension Photos {
    public override func awakeFromInsert() {
        super.awakeFromInsert()
        creationDate = Date()
    }
}
