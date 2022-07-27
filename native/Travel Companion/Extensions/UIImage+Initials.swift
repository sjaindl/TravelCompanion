//
//  UIImage+Initials.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension UIImage {
    static func initialsImage(
        initials: String?,
        size: CGSize,
        backgroundColor: UIColor = UIColor.white,
        textColor: UIColor = UIColor.appTextColorDefault()
    ) -> UIImage? {
        let avatar = PersonAvatarView.loadXib()
        avatar.translatesAutoresizingMaskIntoConstraints = false
        avatar.heightAnchor.constraint(equalToConstant: size.height).isActive = true
        avatar.widthAnchor.constraint(equalToConstant: size.width).isActive = true
        avatar.initials = initials
        avatar.tintColor = backgroundColor
        avatar.textLabel?.textColor = textColor
        avatar.layoutIfNeeded()
        let image = avatar.renderImage()
        return image
    }
}
