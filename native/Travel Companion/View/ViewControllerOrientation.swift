//
//  ViewControllerOrientation.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 05.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

extension UIViewController {
    var isPortrait: Bool {
        let orientation = UIDevice.current.orientation
        switch orientation {
            case .portrait, .portraitUpsideDown:
                return true
            case .landscapeLeft, .landscapeRight:
                return false
            default: // unknown, faceUp or faceDown
                guard let window = self.view.window else {
                    return false
                }
                
                return window.frame.size.width < window.frame.size.height
        }
    }
}
