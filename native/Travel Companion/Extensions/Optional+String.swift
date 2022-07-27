//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public extension Optional where Wrapped: StringProtocol {
    var nilIfEmpty: String? {
        switch self {
        case let .some(string) where !string.isEmpty:
            return string as? String
        default:
            return nil
        }
    }
    
    var intValue: Int? {
        switch self {
        case let .some(string) where !string.isEmpty:
            return Int(string)
        default:
            return nil
        }
    }

    var isNullOrEmpty: Bool {
        switch self {
        case let .some(string):
            return string.isEmpty
        case .none:
            return true
        }
    }
    
    var int64Value: Int64? {
        switch self {
        case let .some(string) where !string.isEmpty:
            return Int64(string)
        default:
            return nil
        }
    }
}
