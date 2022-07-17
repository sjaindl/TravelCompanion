//
//  RestCountriesClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 16.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class RestCountriesClient {
    
    static let sharedInstance = RestCountriesClient()
    
    private init() { }
    
    func fetchCountryDetails(
        of country: String,
        completionHandler: @escaping (_ errorString: String?, _ isEmtpy: Bool, _ result: [String: AnyObject]?) -> Void
    ) {
        let url = WebClient.sharedInstance.createUrl(
            forScheme: RestCountriesConstants.UrlComponents.urlProtocol,
            forHost: RestCountriesConstants.UrlComponents.domain,
            forMethod: RestCountriesConstants.UrlComponents.path,
            withQueryItems: [
                RestCountriesConstants.RequestKeys.accessKey: SecretConstants.restCountriesApiKey,
                RestCountriesConstants.RequestKeys.code: country
            ]
        )
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchCountryData") { (results, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, true, nil)
            } else {
                if let result = results as? [String:AnyObject] {
                    completionHandler(nil, false, result)
                } else {
                    completionHandler("Fetching of Countries failed (no results).", true, nil)
                }
            }
        }
    }
}
