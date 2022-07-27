//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public func assertNotNil<T>(_ item: T?, errorMessage: String, tag: String) -> T {
    if let item = item {
        return item
    } else {
        fatalError(errorMessage)
    }
}
