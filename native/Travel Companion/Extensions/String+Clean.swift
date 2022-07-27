//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation

public extension String {
    func cleanAndLowercase(additional: [String] = []) -> String {
        lowercased().clean(additional: additional)
    }
    
    func clean(additional: [String] = []) -> String {
        var result = components(separatedBy: .whitespacesAndNewlines).joined()
        for sub in additional {
            result = result.replacingOccurrences(of: sub, with: "")
        }
        return result
    }
    
    // Default json chars are currently used to decode payment method json data from Datatrans
    // If we need to support different/more characters in future we can add it here or pass in a different set into removeSpecialChars
    func defaultJsonChars() -> String {
        "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLKMNOPQRSTUVWXYZ1234567890+-*=(){}[].:!_,\""
    }
        
    func removeSpecialChars(validChars: Set<Element>? = nil) -> String {
        let set = validChars ?? Set(defaultJsonChars())
        return filter { set.contains($0) }
    }
    
    func cleanedUrl() -> String {
        guard self.starts(with: "/"), let range = range(of: "/") else {
            return self
        }
        
        return self.replacingCharacters(in: range, with: "").clean()
    }
    
    func escaped() -> String {
        self.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? self
    }
}
