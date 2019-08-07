//
//  GlobalCache.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 03.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

class GlobalCache {
    
    static let imageCache = NSCache<NSString, UIImage>()
    
    private init() {
    }
}
