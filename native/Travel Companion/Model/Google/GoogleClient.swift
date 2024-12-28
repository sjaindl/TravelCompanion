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
import shared

// TODO: Can we move it to shared?
class GoogleClient {
    
    static let sharedInstance = GoogleClient()
    
    private init() {}
    
    func autocomplete(input: String, token: String) -> Observable<[PlacesPredictions]> {
        if input.count < AutocompleteConfig.autocompletionMinChars {
            let filterStrings: [PlacesPredictions] = []
            return Observable.from(optional: filterStrings)
        }
        
        let sharedClient = TCInjector.shared.googleClient
        
        let urlComponents = GoogleConstants.UrlComponents()
        let jsonObject: [String: Any] = [
            "input": input,
            "sessionToken": token
        ]
        let body = try! JSONSerialization.data(withJSONObject: jsonObject, options: [])
        
        let queryItems: [String: String] = [GoogleConstants.ParameterKeys().X_KEY: SecretConstants.apiKeyGooglePlaces]
        
        let url: URL = WebClient.sharedInstance.createUrl(
            forScheme: urlComponents.URL_PROTOCOL,
            forHost: urlComponents.DOMAIN_PLACES,
            forMethod: "/\(urlComponents.PATH_AUTOCOMPLETE)",
            withQueryItems: queryItems
        )
        
        var request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpPost)
        request.httpBody = body
        
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
    
    func placeDetail(placeId: String, token: String) -> Observable<PlacesDetailsResponse?> {
        let sharedClient = TCInjector.shared.googleClient
        
        let queryItems = sharedClient.buildPlaceDetailRequestParams(placeId: placeId, token: token)
        let urlComponents = GoogleConstants.UrlComponents()
        
        let url = WebClient.sharedInstance.createUrlWithKotlinQueryItems(
            forScheme: urlComponents.URL_PROTOCOL,
            forHost: urlComponents.DOMAIN_PLACES,
            forMethod:
            urlComponents.PATH_PLACE_DETAILS,
            withQueryItems: queryItems
        )
        
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
}
