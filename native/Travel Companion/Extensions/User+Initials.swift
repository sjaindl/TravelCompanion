//
//  User+Initials.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import Foundation

extension User {
    public var initials: String {
        let characters = (displayName ?? "TC").split(separator: " ").compactMap { $0.first }
        if characters.count > 1,
           let first = characters.first,
           let last = characters.last {
            return String([first, last]).localizedUppercase
        } else {
            return String(characters).localizedUppercase
        }
    }
}
