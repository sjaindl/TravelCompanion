//
//  CountryApiClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 12.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

class CountryApiClient {
    
    static let sharedInstance = CountryApiClient()
    
    private init() { }
    
    func fetchCountryDetails(
        of country: String,
        completionHandler: @escaping (_ errorString: String?, _ isEmtpy: Bool, _ result: [String: AnyObject]?) -> Void
    ) {
        let url = WebClient.sharedInstance.createUrl(
            forScheme: CountryApiConstants.UrlComponents.urlProtocol,
            forHost: CountryApiConstants.UrlComponents.domain,
            forMethod: CountryApiConstants.UrlComponents.path
        )
        
        let request = WebClient.sharedInstance.buildRequest(
            withUrl: url,
            withHttpMethod: WebConstants.ParameterKeys.httpGet,
            withAuth: "Bearer \(SecretConstants.countryApiApiKey)"
        )
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchCountryData") { (results, error) in
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, true, nil)
            } else {
                if let result = results as? [String: [String: AnyObject]] {
                    completionHandler(nil, false, result[country.lowercased()])
                } else {
                    completionHandler("Fetching of Countries failed (no results).", true, nil)
                }
            }
        }
    }
}
