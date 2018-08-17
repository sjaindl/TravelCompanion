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
    
    func fetchCountryCode(latitude: Double, longitude: Double, completionHandler: @escaping (_ errorString: String?, _ result: AnyObject?) -> Void) {
        
        let method = GeoNamesConstants.UrlComponents.PATH
        
        let queryItems: [String: String] = [GeoNamesConstants.ParameterKeys.LATITUDE: String(latitude),
                                            GeoNamesConstants.ParameterKeys.LONGITUDE: String(longitude),
                                            GeoNamesConstants.ParameterKeys.USERNAME: GeoNamesConstants.ParameterValues.USERNAME]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GeoNamesConstants.UrlComponents.PROTOCOL, forHost: GeoNamesConstants.UrlComponents.DOMAIN, forMethod: method, withQueryItems: queryItems)
        
        let request = buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchCountryCode") { (result, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                completionHandler(nil, result)
            }
        }
    }
    
    private func buildRequest(withUrl url: URL, withHttpMethod httpMethod: String) -> URLRequest {
        return WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: httpMethod)
    }
}
