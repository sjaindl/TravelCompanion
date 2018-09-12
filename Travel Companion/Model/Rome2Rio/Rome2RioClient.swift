//
//  Rome2RioClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 04.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class Rome2RioClient {
    static let sharedInstance = Rome2RioClient()
    
    private init() {}
    
    func autocomplete(with query: String, completionHandler: @escaping (_ errorString: String?, _ autoCompleteResponse: AutoCompleteResponse?) -> Void) {
        
        let queryItems = buildAutoCompleteQueryItems(query: query)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: Rome2RioConstants.UrlComponents.PROTOCOL, forHost: Rome2RioConstants.UrlComponents.DOMAIN, forMethod:
            Rome2RioConstants.UrlComponents.PATH_AUTOCOMPLETE, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForDataWebRequest(request, errorDomain: "placesAutocomplete") { (data, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                if let data = data {
                    let decoder = JSONDecoder()
                    do {
                        let autoCompleteResponse = try decoder.decode(AutoCompleteResponse.self, from: data)
                        completionHandler(nil, autoCompleteResponse)
                    } catch {
                        debugPrint(error)
                        completionHandler(error.localizedDescription, nil)
                    }
                    
                    
                } else {
                    completionHandler("Search failed (no data).", nil)
                }
            }
        }
    }
    
    func search(origin: String, destination: String, flight: Bool, completionHandler: @escaping (_ errorString: String?, _ searchResponse: SearchResponse?) -> Void) {
        
        let queryItems = buildSearchQueryItems(origin: origin, destination: destination, flight: flight)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: Rome2RioConstants.UrlComponents.PROTOCOL, forHost: Rome2RioConstants.UrlComponents.DOMAIN, forMethod:
            Rome2RioConstants.UrlComponents.PATH_SEARCH, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForDataWebRequest(request, errorDomain: "travelSearch") { (data, webError) in
            
            /* Send the desired value(s) to completion handler */
            if let webError = webError {
                completionHandler(webError.localizedDescription, nil)
            } else {
                if let data = data {
                    let decoder = JSONDecoder()
                    do {
                        let searchResponse = try decoder.decode(SearchResponse.self, from: data)
                        completionHandler(nil, searchResponse)
                    } catch {
                        debugPrint(error)
                        completionHandler(error.localizedDescription, nil)
                    }
                    
                    
                } else {
                    completionHandler("Search failed (no data).", nil)
                }
            }
        }
    }
    
    func buildAutoCompleteQueryItems(query: String) -> [String: String] {
        return [
            Rome2RioConstants.ParameterKeys.Key: SecretConstants.ROME2RIO_API_KEY,
            Rome2RioConstants.ParameterKeys.Query: query
        ]
    }
    
    func buildSearchQueryItems(origin: String, destination: String, flight: Bool) -> [String: String] {
        return [
            Rome2RioConstants.ParameterKeys.Key: SecretConstants.ROME2RIO_API_KEY,
            Rome2RioConstants.ParameterKeys.OriginName: origin,
            Rome2RioConstants.ParameterKeys.DestinationName: destination,
            Rome2RioConstants.ParameterKeys.noRail: String(flight),
            Rome2RioConstants.ParameterKeys.noBus: String(flight),
            Rome2RioConstants.ParameterKeys.noFerry: String(flight),
            Rome2RioConstants.ParameterKeys.noCar: String(flight),
            Rome2RioConstants.ParameterKeys.noBikeshare: String(flight),
            Rome2RioConstants.ParameterKeys.noRideshare: String(flight),
            Rome2RioConstants.ParameterKeys.noTowncar: String(flight),
            Rome2RioConstants.ParameterKeys.noCommuter: String(flight),
            Rome2RioConstants.ParameterKeys.noSpecial: String(flight),
            Rome2RioConstants.ParameterKeys.noMinorStart: String(flight),
            Rome2RioConstants.ParameterKeys.noMinorEnd: String(flight),
            Rome2RioConstants.ParameterKeys.noPath: String(flight)
        ]
    }
}
