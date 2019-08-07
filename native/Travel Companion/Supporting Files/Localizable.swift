//
//  Localizable.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 13.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

protocol Localizable {
    var localized: String { get }
}

//used for storyboard localization:
extension String: Localizable {
    var localized: String {
        return NSLocalizedString(self, comment: "")
    }
}

protocol XIBLocalizable {
    var loc: String? { get set }
}

//used for code localization:
extension String {
    func localized(bundle: Bundle = .main, tableName: String = "Localizable") -> String {
        return NSLocalizedString(self, tableName: tableName, value: "**\(self)**", comment: "")
    }
}

extension UILabel: XIBLocalizable {
    @IBInspectable var loc: String? {
        get { return nil }
        set(key) {
            text = key?.localized
        }
    }
}

extension UIButton: XIBLocalizable {
    @IBInspectable var loc: String? {
        get { return nil }
        set(key) {
            setTitle(key?.localized, for: .normal)
        }
    }
}
