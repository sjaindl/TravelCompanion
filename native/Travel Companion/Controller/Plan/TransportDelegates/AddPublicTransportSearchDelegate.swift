//
//  AddPublicTransportSearchDelegate.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class AddPublicTransportSearchDelegate: NSObject, AddTransportSearchDelegate {
    
    func buildSearchQueryItems(origin: String, destination: String) -> [String: String] {
        return [
            Rome2RioConstants.ParameterKeys.key: SecretConstants.apiKeyRomeToRio,
            Rome2RioConstants.ParameterKeys.originName: origin,
            Rome2RioConstants.ParameterKeys.destinationName: destination,
            Rome2RioConstants.ParameterKeys.noAir: "true",
            Rome2RioConstants.ParameterKeys.noAirLeg: "true"
        ]
    }
}
