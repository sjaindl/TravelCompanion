//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit
// @raphi Is this really something that needs to be different between themes?

public struct ThemeTextStyle {
    // MARK: Lifecycle

    public init(
        textSize: CGFloat,
        lineSpacing: CGFloat,
        letterSpacing: CGFloat,
        foregroundColor: UIColor,
        alignment: NSTextAlignment?
    ) {
        self.textSize = textSize
        lineHeight = lineSpacing
        self.letterSpacing = letterSpacing
        self.foregroundColor = foregroundColor
        textAlignment = alignment ?? .natural
    }

    // MARK: Public

    public let textSize: CGFloat
    public let lineHeight: CGFloat
    public let letterSpacing: CGFloat
    public let foregroundColor: UIColor
    public let textAlignment: NSTextAlignment
}

public extension ThemeTextStyle {
    var font: UIFont {
        UIFont.systemFont(ofSize: textSize, weight: .regular)
    }

    var attributes: [NSAttributedString.Key: Any] {
        let paragraph = NSParagraphStyle.default.mutableCopy() as? NSMutableParagraphStyle
        paragraph?.lineHeightMultiple = lineHeight
        paragraph?.alignment = textAlignment
        return [
            .font: font,
            .foregroundColor: foregroundColor,
            .paragraphStyle: paragraph ?? NSParagraphStyle.default
        ]
    }

    var cssStyle: String {
        """
        * {
            font-family: "\(font.familyName)";
            font-size: \(font.pointSize)px;
            color: \(foregroundColor.toHex ?? "#000");
            letter-spacing: \(letterSpacing)px;
            line-height: \(lineHeight)px;
            text-align: \(textAlignment.cssValue);
        }
        strong { color: #000 !important }
        code, pre { font-family: Menlo }
        """
    }
}

public extension ThemeTextStyle {
    init(like label: UILabel) {
        let attributes = label.attributedText?.attributes(at: 0, effectiveRange: nil)
        let kerning = attributes?[.kern] as? CGFloat
        self.init(
            textSize: label.font.pointSize,
            lineSpacing: label.font.lineHeight,
            letterSpacing: kerning ?? 0.0,
            foregroundColor: label.textColor,
            alignment: label.textAlignment
        )
    }

    init(like textView: UITextView) {
        let attributes = textView.attributedText?.attributes(at: 0, effectiveRange: nil)
        let kerning = attributes?[.kern] as? CGFloat
        self.init(
            textSize: textView.font?.pointSize ?? 14,
            lineSpacing: textView.font?.lineHeight ?? 1.0,
            letterSpacing: kerning ?? 0.0,
            foregroundColor: textView.textColor ?? .black,
            alignment: textView.textAlignment
        )
    }

    init(like textView: UITextView, alignment: NSTextAlignment) {
        let attributes = textView.attributedText?.attributes(at: 0, effectiveRange: nil)
        let kerning = attributes?[.kern] as? CGFloat
        self.init(
            textSize: textView.font?.pointSize ?? 14,
            lineSpacing: textView.font?.lineHeight ?? 1.0,
            letterSpacing: kerning ?? 0.0,
            foregroundColor: textView.textColor ?? .black,
            alignment: alignment
        )
    }
}

public extension ThemeTextStyle {
    func copy(
        textSize: CGFloat? = nil,
        lineHeight: CGFloat? = nil,
        letterSpacing: CGFloat? = nil,
        foregroundColor: UIColor? = nil,
        alignment: NSTextAlignment? = nil
    ) -> ThemeTextStyle {
        ThemeTextStyle(
            textSize: textSize ?? self.textSize,
            lineSpacing: lineHeight ?? self.lineHeight,
            letterSpacing: letterSpacing ?? self.letterSpacing,
            foregroundColor: foregroundColor ?? self.foregroundColor,
            alignment: alignment ?? textAlignment
        )
    }
}
