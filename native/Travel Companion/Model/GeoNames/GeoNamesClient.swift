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
    
    func fetchCountryCode(latitude: Double, longitude: Double, completionHandler: @escaping (_ errorString: String?, _ result: String?) -> Void) {
        
        let method = GeoNamesConstants.UrlComponents.path
        
        let queryItems: [String: String] = [GeoNamesConstants.ParameterKeys.latitude: String(latitude),
                                            GeoNamesConstants.ParameterKeys.longitude: String(longitude),
                                            GeoNamesConstants.ParameterKeys.username: SecretConstants.userNameGeoNames]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: GeoNamesConstants.UrlComponents.urlProtocol, forHost: GeoNamesConstants.UrlComponents.domain, forMethod: method, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForDataWebRequest(request, errorDomain: "fetchCountryCode") { (data, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                do {
                    let decoder = JSONDecoder()
                    let geocode = try decoder.decode(Geocode.self, from: data!)
                    
                    completionHandler(nil, geocode.countryCode)
                } catch {
                    debugPrint("Error: \(error)")    
                    completionHandler("No country code found", nil)
                }
            }
        }
    }
}
