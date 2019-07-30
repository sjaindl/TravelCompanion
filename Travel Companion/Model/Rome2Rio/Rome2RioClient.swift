//
//  Rome2RioClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 04.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import RxSwift

class Rome2RioClient {
    static let sharedInstance = Rome2RioClient()
    
    private init() {}
    
    func autocomplete(with query: String) -> Observable<[String]> {
        if query.count < AutocompleteConfig.autocompletionMinChars {
            let filterStrings: [String] = []
            return Observable.from(optional: filterStrings)
        }
        
        let queryItems = buildAutoCompleteQueryItems(query: query)
        
        let url = WebClient.sharedInstance.createUrl(forScheme: Rome2RioConstants.UrlComponents.urlProtocol, forHost: Rome2RioConstants.UrlComponents.domain, forMethod:
            Rome2RioConstants.UrlComponents.pathAutocomplete, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        return WebClient.sharedInstance.taskForRxDataWebRequest(with: request) { (data) in
            do {
                let decoder = JSONDecoder()
                let result = try decoder.decode(AutoCompleteResponse.self, from: data!)
                
                var filterStrings: [String] = []
                
                for place in (result.places) {
                    filterStrings.append(place.longName)
                }
                
                return filterStrings
            } catch {
                print("Error: \(error)")
                return []
            }
        }
    }
    
    func search(with queryItems: [String: String], completionHandler: @escaping (_ errorString: String?, _ searchResponse: SearchResponse?) -> Void) {
        
        let url = WebClient.sharedInstance.createUrl(forScheme: Rome2RioConstants.UrlComponents.urlProtocol, forHost: Rome2RioConstants.UrlComponents.domain, forMethod:
            Rome2RioConstants.UrlComponents.pathSearch, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
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
            Rome2RioConstants.ParameterKeys.key: SecretConstants.apiKeyRomeToRio,
            Rome2RioConstants.ParameterKeys.query: query
        ]
    }
}

