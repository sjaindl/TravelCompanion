//
//  LocalizableString.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 28.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

extension String {
    func localized(bundle: Bundle = .main, tableName: String = "Localizable") -> String {
        return NSLocalizedString(self, tableName: tableName, value: "**\(self)**", comment: "")
    }
}
