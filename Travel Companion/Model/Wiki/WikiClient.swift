//
//  WikiClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class WikiClient {
    static let sharedInstance = WikiClient()
    
    private init() { }
    
    func fetchWikiLink(country: String, domain: String, completionHandler: @escaping (_ errorString: String?, _ result: String?) -> Void) {
        
        let queryItems: [String: String] = [WikiConstants.ParameterKeys.Action: WikiConstants.ParameterValues.QUERY,
                                            WikiConstants.ParameterKeys.Inprop: WikiConstants.ParameterValues.Inprop,
                                            WikiConstants.ParameterKeys.Prop: WikiConstants.ParameterValues.PropInfo,
                                            WikiConstants.ParameterKeys.Format: WikiConstants.ParameterValues.ResponseFormat,
                                            WikiConstants.ParameterKeys.Titles: country]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: WikiConstants.UrlComponents.PROTOCOL, forHost: domain, forMethod: WikiConstants.UrlComponents.PATH, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.HTTP_GET)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchWiki", stringResponse: true) { (result, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                if let responseElements = result?.components(separatedBy: "\""), let index = responseElements.index(of: WikiConstants.ResponseKeys.FullUrl), index <= responseElements.count + 2 {
                    completionHandler(nil, responseElements[index + 2])
                } else {
                    print("Could not find result in \(result!)")
                    completionHandler("Fetching of wiki link failed (no results).", nil)
                }
            }
        }
    }
    
    
}
