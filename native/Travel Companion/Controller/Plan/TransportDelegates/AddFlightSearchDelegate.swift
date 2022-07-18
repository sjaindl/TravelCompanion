//
//  AddFlightSearchDelegate.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class AddFlightSearchDelegate: NSObject, AddTransportSearchDelegate {
    func buildSearchQueryItems(origin: String, destination: String) -> [String: String] {
        [
            Rome2RioConstants.ParameterKeys.key: SecretConstants.apiKeyRomeToRio,
            Rome2RioConstants.ParameterKeys.originName: origin,
            Rome2RioConstants.ParameterKeys.destinationName: destination,
            Rome2RioConstants.ParameterKeys.noRail: "true",
            Rome2RioConstants.ParameterKeys.noBus: "true",
            Rome2RioConstants.ParameterKeys.noFerry: "true",
            // better to disable because there may be a car drive necessary to the next airport:
            // Rome2RioConstants.ParameterKeys.noCar: "true",
            Rome2RioConstants.ParameterKeys.noBikeshare: "true",
            Rome2RioConstants.ParameterKeys.noRideshare: "true",
            Rome2RioConstants.ParameterKeys.noTowncar: "true",
            Rome2RioConstants.ParameterKeys.noCommuter: "true",
            Rome2RioConstants.ParameterKeys.noSpecial: "true",
            Rome2RioConstants.ParameterKeys.noMinorStart: "true",
            Rome2RioConstants.ParameterKeys.noMinorEnd: "true",
            Rome2RioConstants.ParameterKeys.noPath: "true"
        ]
    }
}
