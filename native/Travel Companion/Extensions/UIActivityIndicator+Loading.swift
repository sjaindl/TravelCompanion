//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension UIActivityIndicatorView {
    var isLoading: Bool {
        get {
            isAnimating
        }

        set {
            if newValue {
                isHidden = false
                startAnimating()
            } else {
                stopAnimating()
            }
        }
    }
}
