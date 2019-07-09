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
        
        let queryItems: [String: String] = [WikiConstants.ParameterKeys.action: WikiConstants.ParameterValues.query,
                                            WikiConstants.ParameterKeys.inprop: WikiConstants.ParameterValues.inprop,
                                            WikiConstants.ParameterKeys.prop: WikiConstants.ParameterValues.propInfo,
                                            WikiConstants.ParameterKeys.format: WikiConstants.ParameterValues.responseFormat,
                                            WikiConstants.ParameterKeys.titles: country]
        
        let url = WebClient.sharedInstance.createUrl(forScheme: WikiConstants.UrlComponents.urlProtocol, forHost: domain, forMethod: WikiConstants.UrlComponents.path, withQueryItems: queryItems)
        
        let request = WebClient.sharedInstance.buildRequest(withUrl: url, withHttpMethod: WebConstants.ParameterKeys.httpGet)
        
        WebClient.sharedInstance.taskForWebRequest(request, errorDomain: "fetchWiki", stringResponse: true) { (result, error) in
            
            /* Send the desired value(s) to completion handler */
            if let error = error {
                completionHandler(error.localizedDescription, nil)
            } else {
                if let responseElements = result?.components(separatedBy: "\""), let index = responseElements.firstIndex(of: WikiConstants.ResponseKeys.fullUrl), index <= responseElements.count + 2 {
                    completionHandler(nil, responseElements[index + 2])
                } else {
                    print("Could not find result in \(result!)")
                    completionHandler("Fetching of wiki link failed (no results).", nil)
                }
            }
        }
    }
}
