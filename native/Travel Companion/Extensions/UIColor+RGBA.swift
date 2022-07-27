//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import CoreGraphics
import Foundation

public extension CGColor {
    var rgba: RGBA? {
        guard let cs = CGColorSpace(name: CGColorSpace.genericRGBLinear),
              let color = converted(to: cs, intent: .defaultIntent, options: nil),
              let components = color.components,
              components.count == 4 else {
            return nil
        }

        return RGBA(rColor: components[0], gColor: components[1], bColor: components[2], aColor: components[3])
    }
}

public struct RGBA {
    public let rColor: CGFloat
    public let gColor: CGFloat
    public let bColor: CGFloat
    public let aColor: CGFloat

    public var color: CGColor? {
        guard let cs = CGColorSpace(name: CGColorSpace.genericRGBLinear) else {
            return nil
        }

        return CGColor(
            colorSpace: cs,
            components: [rColor, gColor, bColor, aColor]
        )
    }
}
