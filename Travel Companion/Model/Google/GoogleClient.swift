//
//  GoogleClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreLocation
import Foundation

class GoogleClient {
    
    static let sharedInstance = GoogleClient()
    
    private init() {}
    
    func searchPlaces(for text: String?, coordinate: CLLocationCoordinate2D, type: String, completionHandler: @escaping (_ errorString: String?, _ places: [GooglePlace]) -> Void) {
        
        let queryItems = buildPlaceSearchQueryItems(for: text, coordinate: coordinate, type: type)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GoogleConstants.UrlComponents.urlProtocol, forHost: GoogleConstants.UrlComponents.domain, forMethod:
            GoogleConstants.UrlComponents.pathNearbySearch, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForDataWebRequest(request, errorDomain: "googlePlacesSearch") { (data, webError) in
            
            /* Send the desired value(s) to completion handler */
            if let webError = webError {
                completionHandler(webError.localizedDescription, [])
            } else {
                if let data = data {
                    let decoder = JSONDecoder()
                    do {
                        let placesSearchResponse = try decoder.decode(PlacesNearbySearchResponse.self, from: data)
                        let places = placesSearchResponse.results
                        
                        for place in places {
                            place.htmlAttributions = placesSearchResponse.htmlAttributions
                        }
                        
                        completionHandler(nil, places)
                    } catch {
                        debugPrint(error)
                        completionHandler(error.localizedDescription, [])
                    }
                } else {
                    completionHandler("Search failed (no data).", [])
                }
            }
        }
    }
    
    private func buildPlaceSearchQueryItems(for text: String?, coordinate: CLLocationCoordinate2D, type: String) -> [String: String] {
        var parameters = [
            GoogleConstants.ParameterKeys.rankBy: GoogleConstants.ParameterValues.rankBy,
            GoogleConstants.ParameterKeys.radius: GoogleConstants.ParameterValues.radius,
            GoogleConstants.ParameterKeys.placeType: type,
            GoogleConstants.ParameterKeys.key: SecretConstants.apiKeyGooglePlaces,
            GoogleConstants.ParameterKeys.strictBounds: GoogleConstants.ParameterValues.strictBounds
        ]
        
        if CLLocationCoordinate2DIsValid(coordinate) {
            parameters[GoogleConstants.ParameterKeys.location] = "\(coordinate.latitude),\(coordinate.longitude)"
        }
        
        if let text = text, text.count > 0, text != " " {
            parameters[GoogleConstants.ParameterKeys.name] = text
        }
        
        return parameters
    }
}
