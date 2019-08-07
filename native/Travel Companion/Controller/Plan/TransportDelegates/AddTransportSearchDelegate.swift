//
//  AddTransportSearchDelegate.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

protocol AddTransportSearchDelegate {
    func buildSearchQueryItems(origin: String, destination: String) -> [String: String]
}
