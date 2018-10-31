//
//  FormatUtils.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation

class FormatUtils {
    
    static func formatTimestampRangeForDisplay(begin: Timestamp, end: Timestamp) -> String {
        return "\(formatTimestampForDisplay(timestamp: begin)) - \(formatTimestampForDisplay(timestamp: end))"
    }
    
    static func formatTimestampForDisplay(timestamp: Timestamp) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        
        let formattedDate = dateFormatter.string(from: timestamp.dateValue())
        return formattedDate
    }
    
    static func getLink(_ text: String) -> String? {
        let linkBeginIndex = text.range(of: "<a href=\"",
                                        options: NSString.CompareOptions.literal,
                                        range: text.startIndex..<text.endIndex,
                                        locale: nil)
        
        let linkEndIndex = text.range(of: "\">",
                                      options: NSString.CompareOptions.literal,
                                      range: text.startIndex..<text.endIndex,
                                      locale: nil)
        
        if let linkBeginIndex = linkBeginIndex, let linkEndIndex = linkEndIndex {
            return String(text[(linkBeginIndex.upperBound)..<(linkEndIndex.lowerBound)])
        }
        
        return nil
    }
    
    static func getLinkAttributedText(_ text: String) -> NSMutableAttributedString? {
        let attributionBeginIndex = text.range(of: "\">",
                                               options: NSString.CompareOptions.literal,
                                               range: text.startIndex..<text.endIndex,
                                               locale: nil)
        
        let attributionEndIndex = text.range(of: "</a>",
                                             options: NSString.CompareOptions.literal,
                                             range: text.startIndex..<text.endIndex,
                                             locale: nil)
        
        if let attributionBeginIndex = attributionBeginIndex, let attributionEndIndex = attributionEndIndex {
            let attributionString = NSMutableAttributedString(string: "\n" + String(text[(attributionBeginIndex.upperBound)..<(attributionEndIndex.lowerBound)]))
            
            if let linkString = getLink(text), let url = URL(string: linkString) {
                attributionString.addAttribute(.link, value: url, range: NSMakeRange(0, attributionString.string.count))
                return attributionString
            }
        }
        
        return nil
    }
}
