//
//  GeoNamesClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class GeoNamesClient {
    
    static let sharedInstance = GeoNamesClient()
    
    private init() { }
    
    func fetchCountryCode(latitude: Double, longitude: Double, completionHandler: @escaping (_ errorString: String?, _ result: AnyObject?) -> Void) {
        
        let method = GeoNamesConstants.UrlComponents.path
        
        let queryItems: [String: String] = [GeoNamesConstants.ParameterKeys.latitude: String(latitude),
                                            GeoNamesConstants.ParameterKeys.longitude: String(longitude),
                                            GeoNamesConstants.ParameterKeys.username: SecretConstants.userNameGeoNames]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GeoNamesConstants.UrlComponents.urlProtocol, forHost: GeoNamesConstants.UrlComponents.domain, forMethod: method, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchCountryCode", stringResponse: true) { (result, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                completionHandler(nil, result)
            }
        }
    }
}
