//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension NSTextAlignment {
    var cssValue: String {
        switch self {
        case .center:
            return "center"
        case .left:
            return "left"
        case .right:
            return "right"
        case .natural:
            return "initial"
        case .justified:
            return "justify"
        @unknown default:
            return "left"
        }
    }
}
