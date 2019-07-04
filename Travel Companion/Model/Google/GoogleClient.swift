//
//  GoogleClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreLocation
import Foundation
import RxSwift

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
    
    func autocomplete(for input: String, token: String) -> Observable<[PlacesPredictions]> {
        let queryItems = buildAutoCompleteQueryItems(for: input, token: token)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GoogleConstants.UrlComponents.urlProtocol, forHost: GoogleConstants.UrlComponents.domain, forMethod:
            GoogleConstants.UrlComponents.pathAutocomplete, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        return WebClient.sharedInstance.taskForRxDataPlacesPredictionsWebRequest(with: request) { (data) in
            do {
                let decoder = JSONDecoder()
                let result = try decoder.decode(PlacesAutoCompleteResponse.self, from: data!)
                
                var filterStrings: [PlacesPredictions] = []
                
                for prediction in (result.predictions) {
                    filterStrings.append(prediction/* .description */)
                }
                
                return filterStrings
            } catch {
                print("Error: \(error)")
                return []
            }
        }
    }
    
    private func buildAutoCompleteQueryItems(for input: String, token: String) -> [String: String] {
        let parameters = [
            //GoogleConstants.ParameterKeys.radius: GoogleConstants.ParameterValues.radius,
            GoogleConstants.ParameterKeys.types: GoogleConstants.ParameterValues.autocompletePlaceType,
            GoogleConstants.ParameterKeys.input: input,
            GoogleConstants.ParameterKeys.sessionToken: token,
            GoogleConstants.ParameterKeys.key: SecretConstants.apiKeyGooglePlaces,
            //GoogleConstants.ParameterKeys.strictBounds: GoogleConstants.ParameterValues.strictBounds
        ]
        
        return parameters
    }
    
    func placeDetail(for placeId: String, token: String) -> Observable<PlacesDetailsResponse?> {
        let queryItems = buildPlaceDetailQueryItems(for: placeId, token: token)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GoogleConstants.UrlComponents.urlProtocol, forHost: GoogleConstants.UrlComponents.domain, forMethod:
            GoogleConstants.UrlComponents.pathPlaceDetail, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        return WebClient.sharedInstance.taskForRxDataPlaceDetailsWebRequest(with: request) { (data) in
            
            do {
                let decoder = JSONDecoder()
                let result = try decoder.decode(PlacesDetailsResponse.self, from: data!)
                
                return result
            } catch {
                print("Error: \(error)")
                return nil
            }
        }
    }
    
    private func buildPlaceDetailQueryItems(for placeId: String, token: String) -> [String: String] {
        let parameters = [
            GoogleConstants.ParameterKeys.placeId: placeId,
            GoogleConstants.ParameterKeys.fields: GoogleConstants.ParameterValues.placeDetailFields,
            GoogleConstants.ParameterKeys.sessionToken: token,
            GoogleConstants.ParameterKeys.key: SecretConstants.apiKeyGooglePlaces
        ]
        
        return parameters
    }
}
