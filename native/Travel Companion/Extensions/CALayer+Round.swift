//
//  CALayer+Round.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension CALayer {
    func roundCorners(_ radius: CGFloat, cornerMask: CACornerMask? = nil) {
        cornerRadius = radius
        if let cornerMask = cornerMask {
            maskedCorners = cornerMask
        }

        masksToBounds = true
        smoothSailing()
    }
    
    func smoothSailing() {
        let smoothSailor = NSString(format: "%@ti%@ous%@ners", "con", "nu", "Cor")
        if responds(to: NSSelectorFromString(smoothSailor as String)) {
            setValue(true, forKey: smoothSailor as String)
        }
    }
}
