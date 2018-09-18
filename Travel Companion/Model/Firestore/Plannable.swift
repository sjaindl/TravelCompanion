//
//  Plannable.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

protocol Plannable: Codable {
    func description() -> String
    func details() -> String
    func imageUrl() -> String?
    func getId() -> String
    func getNotes() -> String
    func setNotes(notes: String)
    func encode() -> [String: Any]
}
