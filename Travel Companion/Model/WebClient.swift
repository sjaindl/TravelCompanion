//
//  WebClient.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 16.05.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

class WebClient {
    
    static let sharedInstance = WebClient()
    
    func createUrl(forScheme scheme: String, forHost host: String, forMethod method: String, withQueryItems queryItems: [String: String]?) -> URL {
        var urlComponent = URLComponents()
        
        urlComponent.scheme = scheme
        urlComponent.host = host
        urlComponent.path = method
        
        urlComponent.queryItems = [URLQueryItem]()
        
        if let queryItems = queryItems {
            for (key, value) in queryItems {
                let queryItem = URLQueryItem(name: key, value: "\(value)")
                urlComponent.queryItems!.append(queryItem)
            }
        }
        
        return urlComponent.url!
    }
    
    func buildRequest(withUrl url: URL, withHttpMethod httpMethod: String) -> URLRequest {
        var request = URLRequest(url: url)
        
        request.addValue(WebConstants.ParameterValues.TYPE_JSON, forHTTPHeaderField: WebConstants.ParameterKeys.ACCEPT_TYPE)
        request.addValue(WebConstants.ParameterValues.TYPE_JSON, forHTTPHeaderField: WebConstants.ParameterKeys.CONTENT_TYPE)
        
        request.httpMethod = httpMethod
        
        return request
    }
    
    func buildJsonString(from jsonObject: Any, withKey key: String? = nil) -> String {
        var json: String = ""
        var jsonPrefix: String?
        var jsonPostfix: String?
        
        if let key = key {
            jsonPrefix = "{\"\(key)\": "
            jsonPostfix = "}"
        }
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject)
            if let jsonPrefix = jsonPrefix {
                json = jsonPrefix + String(data: jsonData, encoding: .utf8)!
            } else {
                json = String(data: jsonData, encoding: .utf8)!
            }
            
            if let jsonPostfix = jsonPostfix {
                json += jsonPostfix
            }
        } catch {
            json = "{}"
        }
        
        return json
    }
    
    func buildJson(from jsonObject: Any, withKey key: String? = nil) -> Data? {
        return buildJsonString(from: jsonObject, withKey: key).data(using: .utf8)
    }
    
    // given raw JSON, return a usable Foundation object
    func convertDataWithCompletionHandler(_ data: Data, withOffset offset: Int, completionHandlerForConvertData: (_ result: AnyObject?, _ error: NSError?) -> Void) {
        
        var parsedResult: AnyObject! = nil
        do {
            let range = Range(offset ..< data.count)
            let newData = data.subdata(in: range) /* subset response data! */
            
            parsedResult = try JSONSerialization.jsonObject(with: newData, options: .allowFragments) as AnyObject
        } catch {
            let userInfo = [NSLocalizedDescriptionKey : "Could not parse the data as JSON: '\(data)'"]
            completionHandlerForConvertData(nil, NSError(domain: "convertDataWithCompletionHandler", code: 1, userInfo: userInfo))
        }
        
        completionHandlerForConvertData(parsedResult, nil)
    }
    
    func performBasicWebResponseChecks(data: Data?, response: URLResponse?, error: Error?, errorDomain: String, withOffset offset: Int) -> NSError? {
        if let error = error {
            return NSError(domain: errorDomain, code: 1, userInfo: [NSLocalizedDescriptionKey: error.localizedDescription])
        }
        
        /* GUARD: Was there any data returned? */
        guard let data = data else {
            return NSError(domain: errorDomain, code: 2, userInfo: [NSLocalizedDescriptionKey: "No data was returned in the response"])
        }
        
        /* GUARD: Did we get a valid response? */
        guard let statusCode = (response as? HTTPURLResponse)?.statusCode else {
            return NSError(domain: errorDomain, code: 3, userInfo: [NSLocalizedDescriptionKey: "No status code returned in response"])
        }
        
        /* GUARD: Did we get a successful 2XX response? */
        guard statusCode >= 200 && statusCode <= 299 else {
            var statusCodeError = NSError(domain: errorDomain, code: 4, userInfo: [NSLocalizedDescriptionKey: "Response returned status code " + String(statusCode)])
            
            //get detailled status code error message, if available
            convertDataWithCompletionHandler(data, withOffset: offset) { (parsedResult, errorDetail) in
                if let parsedResult = parsedResult as? [String: AnyObject], let status = parsedResult[WebConstants.ParameterKeys.STATUS] as? Int, let errorDescription = parsedResult[WebConstants.ParameterKeys.ERROR] as? String {
                        statusCodeError = NSError(domain: errorDomain, code: 5, userInfo: [NSLocalizedDescriptionKey: errorDescription +  " (" + String(status) + ")"])
                }
            }
            
            return statusCodeError
        }
        
        return nil
    }
    
    func taskForWebRequest(_ request: URLRequest, errorDomain: String, withOffset offset: Int = 0, completionHandlerForRequest: @escaping (_ result: AnyObject?, _ error: NSError?) -> Void) -> Void {
        
        /* Perform Web request */
        let task = URLSession.shared.dataTask(with: request as URLRequest) { (data, response, error) in
            
            if let basicError = self.performBasicWebResponseChecks(data: data, response: response, error: error, errorDomain: errorDomain, withOffset: offset) {
                completionHandlerForRequest(nil, basicError)
                return
            }
            
            /* Parse and use data (happens in completion handler) */
            self.convertDataWithCompletionHandler(data!, withOffset: offset, completionHandlerForConvertData: completionHandlerForRequest)
        }
        
        /* Start request */
        task.resume()
    }
}
