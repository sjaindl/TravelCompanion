//
//  Plannable.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

protocol Plannable {
    func description() -> String
    func details() -> String
    func imageUrl() -> String?
}
