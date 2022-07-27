//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Down
import Foundation
import UIKit

public extension StringProtocol where Index == String.Index {
    func nsRange(from range: Range<Index>) -> NSRange {
        NSRange(range, in: self)
    }
}

public extension NSMutableAttributedString {
    /// Applys `attributes` to the string range `startIndex..<endIndex`
    func addAttributes(_ attributes: [NSAttributedString.Key: Any]) {
        let range = string.startIndex ..< string.endIndex
        addAttributes(attributes, range: string.nsRange(from: range))
    }
}

public extension NSAttributedString {
    convenience init(markdown: String, textStyle: ThemeTextStyle?) {
        let text: NSMutableAttributedString
        
        do {
            let attributedMarkdownString = try Down(markdownString: markdown).toAttributedString(stylesheet: textStyle?.cssStyle)
            text = NSMutableAttributedString(attributedString: attributedMarkdownString)
        } catch {
            text = NSMutableAttributedString(string: markdown)
            if let style = textStyle {
                text.addAttributes(style.attributes)
            }
        }

        self.init(attributedString: text.trimmedAttributedString(set: .whitespacesAndNewlines))
    }

    private static func createSubText(string: String, textStyle: ThemeTextStyle?, linkValue: String) -> NSMutableAttributedString {
        let subText = NSMutableAttributedString(string: string)
        subText.addAttributes(textStyle?.attributes ?? [:])
        subText.addAttribute(.link, value: linkValue.escaped(), range: NSRange(location: 0, length: subText.length))

        return subText
    }
}

public extension UIFont {
    func withTraits(_ traits: UIFontDescriptor.SymbolicTraits) -> UIFont {
        // create a new font descriptor with the given traits
        if let fd = fontDescriptor.withSymbolicTraits(traits) {
            // return a new font with the created font descriptor
            return UIFont(descriptor: fd, size: pointSize)
        }

        // the given traits couldn't be applied, return self
        return self
    }

    func italics() -> UIFont {
        withTraits(.traitItalic)
    }

    func bold() -> UIFont {
        withTraits(.traitBold)
    }

    func boldItalics() -> UIFont {
        withTraits([.traitBold, .traitItalic])
    }
}
