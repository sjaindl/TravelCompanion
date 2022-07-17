//
//  WebConstants.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 24.05.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct WebConstants {
    
    struct ParameterKeys {
        static let acceptType = "Accept"
        static let contentType = "Content-Type"
        static let httpPost = "POST"
        static let httpPut = "PUT"
        static let httpGet = "GET"
        static let httpDelete = "DELETE"
        
        static let status = "status"
        static let error = "error"
        
        static let authorization = "Authorization"
    }
    
    struct ParameterValues {
        static let typeJson = "application/json"
        static let contentType = "Content-Type"
    }
}
