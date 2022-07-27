//
//  UIView+Render.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension UIView {
    func renderImage() -> UIImage? {
        let format = UIGraphicsImageRendererFormat.default()
        format.opaque = isOpaque
        let renderer = UIGraphicsImageRenderer(size: bounds.size, format: format)
        let image = renderer.image { context in
            layer.render(in: context.cgContext)
        }
        return image
    }
}
