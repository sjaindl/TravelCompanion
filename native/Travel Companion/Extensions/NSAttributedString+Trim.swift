//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public extension NSMutableAttributedString {
    func trimmedAttributedString(set: CharacterSet) -> NSMutableAttributedString {
        let invertedSet = set.inverted

        var range = (string as NSString).rangeOfCharacter(from: invertedSet)
        let loc = range.length > 0 ? range.location : 0

        range = (string as NSString).rangeOfCharacter(from: invertedSet, options: .backwards)
        let len = (range.length > 0 ? NSMaxRange(range) : string.count) - loc

        let r = attributedSubstring(from: NSRange(location: loc, length: len))
        return NSMutableAttributedString(attributedString: r)
    }
}
