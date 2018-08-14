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
        static let ACCEPT_TYPE = "Accept"
        static let CONTENT_TYPE = "Content-Type"
        static let HTTP_POST = "POST"
        static let HTTP_PUT = "PUT"
        static let HTTP_GET = "GET"
        static let HTTP_DELETE = "DELETE"
        
        static let STATUS = "status"
        static let ERROR = "error"
    }
    
    struct ParameterValues {
        static let TYPE_JSON = "application/json"
        static let CONTENT_TYPE = "Content-Type"
    }
}
