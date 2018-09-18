//
//  PlannableFactory.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Foundation

class PlannableFactory {
    static func createPlannable(of type: String, data: [String: Any]) throws -> Plannable {
      switch type {
        case Constants.PLANNABLES.FLIGHT:
            return try FirestoreDecoder().decode(Flight.self, from: data)
        case Constants.PLANNABLES.PUBLIC_TRANSPORT:
            return try FirestoreDecoder().decode(PublicTransport.self, from: data)
        //TODO
//        case Constants.PLANNABLES.ATTRACTION:
//            return try FirestoreDecoder().decode(Flight.self, from: data)
//        case Constants.PLANNABLES.HOTEL:
//            return try FirestoreDecoder().decode(Flight.self, from: data)
//        case Constants.PLANNABLES.RESTAURANT:
//            return try FirestoreDecoder().decode(Flight.self, from: data)
        default:
            throw NSError(domain: "Plannable type not supported", code: -1, userInfo: [:])
        }
    }
}
