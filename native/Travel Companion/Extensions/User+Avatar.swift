//
//  User+Avatar.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation
import UIKit

public extension User {
    func initialsAvatarImage(
        size: CGSize,
        backgroundColor: UIColor = UIColor.white,
        textColor: UIColor = UIColor.appTextColorDefault()
    ) -> UIImage? {
        UIImage.initialsImage(initials: initials, size: size, backgroundColor: backgroundColor, textColor: textColor)
    }
}
