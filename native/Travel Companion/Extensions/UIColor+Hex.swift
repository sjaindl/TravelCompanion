//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

// swiftlint:disable identifier_name
// Source: https://cocoacasts.com/from-hex-to-uicolor-and-back-in-swift
public extension UIColor {
    // MARK: - Initialization

    convenience init?(hex: String) {
        if let cgColor = hex.cgColor {
            self.init(cgColor: cgColor)
        } else {
            return nil
        }
    }

    // MARK: - Computed Properties

    var toHex: String? {
        toHex()
    }

    // MARK: - From UIColor to String

    func toHex(alpha _: Bool = false) -> String? {
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0

        getRed(&r, green: &g, blue: &b, alpha: &a)

        let rgb: Int = (Int)(r * 255) << 16 | (Int)(g * 255) << 8 | (Int)(b * 255) << 0

        return String(format: "#%06x", rgb)
    }
}

public extension CGColor {
    var hex: String? {
        guard let rgba = self.rgba else {
            return nil
        }

        let rgb: Int = (Int)(rgba.rColor * 255) << 16 | (Int)(rgba.gColor * 255) << 8 | (Int)(rgba.bColor * 255) << 0
        let aColor: Int = (Int)(rgba.aColor * 255)

        if aColor == 255 {
            return String(format: "#%06x", rgb)
        } else {
            return String(format: "#%06x", rgb) + String(format: "%02x", aColor)
        }
    }
}

public extension String {
    var cgColor: CGColor? {
        var hexSanitized = trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0

        let length = hexSanitized.count

        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else {
            return nil
        }

        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0
        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0
        } else {
            return nil
        }

        guard let cs = CGColorSpace(name: CGColorSpace.extendedSRGB) else {
            return nil
        }

        return CGColor(
            colorSpace: cs,
            components: [r, g, b, a]
        )
    }
}
