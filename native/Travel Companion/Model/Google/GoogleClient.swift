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
        
        let sharedClient = shared.GoogleClient()
        
        let queryItems = sharedClient.buildAutoCompleteRequestParams(input: input, token: token)
        let urlComponents = GoogleConstants.UrlComponents()
        
        let url = WebClient.sharedInstance.createUrlWithKotlinQueryItems(
            forScheme: urlComponents.urlProtocol,
            forHost: urlComponents.domain,
            forMethod: urlComponents.pathAutocomplete,
            withQueryItems: queryItems
        )
        
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
    
    func placeDetail(placeId: String, token: String) -> Observable<PlacesDetailsResponse?> {
        let sharedClient = shared.GoogleClient()
        
        let queryItems = sharedClient.buildPlaceDetailRequestParams(placeId: placeId, token: token)
        let urlComponents = GoogleConstants.UrlComponents()
        
        let url = WebClient.sharedInstance.createUrlWithKotlinQueryItems(
            forScheme: urlComponents.urlProtocol,
            forHost: urlComponents.domain,
            forMethod:
            urlComponents.pathPlaceDetail,
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
