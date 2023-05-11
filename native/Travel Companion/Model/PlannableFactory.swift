//
//  PlannableFactory.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Foundation
import shared

class PlannableFactory {
    static func makePlannable(of type: String, data: [String: Any]) throws -> Plannable {
      switch type {
        case Constants.Plannables.flight:
            return try FirestoreDecoder().decode(Flight.self, from: data)
        case Constants.Plannables.publicTransport:
            return try FirestoreDecoder().decode(PublicTransport.self, from: data)
        case Constants.Plannables.attraction:
            return try FirestoreDecoder().decode(GooglePlace.self, from: data)
        case Constants.Plannables.hotel:
            return try FirestoreDecoder().decode(GooglePlace.self, from: data)
        case Constants.Plannables.restaurant:
            return try FirestoreDecoder().decode(GooglePlace.self, from: data)
         
        default:
            throw NSError(domain: "Plannable type not supported", code: -1, userInfo: [:])
        }
    }
}
