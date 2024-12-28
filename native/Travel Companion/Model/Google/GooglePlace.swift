//
//  GooglePlace.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Foundation
import HTMLEntities
import shared

class GooglePlace: NSObject, Plannable {
    var placeId: String
    var businessStatus: String?
    var icon: String?
    var name: String
    var rating: Double?
    var reference: String
    var scope: String
    var types: [String]?
    var userRatingsTotal: Int?
    var vicinity: String?
    var geometry: Geometry?
    var photos: [Photo]?
    var plusCode: PlusCode?
    var priceLevel: Int? // 0: free, 1: inexpensive, 2: moderate, 3: expensive, 4: very Expensive
    var htmlAttributions: [String]? = []
    
    var notes: String?
    
    // https://developers.google.com/maps/documentation/places/web-service/search-nearby
    private enum CodingKeys: String, CodingKey {
        case businessStatus = "business_status"
        case icon
        case name
        case placeId = "place_id"
        case rating
        case reference
        case scope
        case types
        case userRatingsTotal = "user_ratings_total"
        case vicinity
        case geometry
        case photos
        case plusCode = "plus_code"
        case priceLevel = "price_level"
        case htmlAttributions = "html_attributions"
        case notes
    }
    
    init(placeId: String, name: String, reference: String, scope: String, vicinity: String?) {
        self.placeId = placeId
        self.name = name
        self.placeId = placeId
        self.reference = reference
        self.scope = scope
        self.vicinity = vicinity
    }
    
    func getId() -> String {
        return placeId
    }
    
    func description() -> String {
        return "\(name)"
    }
    
    func details() -> NSMutableAttributedString {
        var details = NSMutableAttributedString(string: vicinity ?? "")
        
        if let rating = rating {
            details = NSMutableAttributedString(string: "\(details.string). \(rating)/5*")
        }
        
        if let photos = photos, photos.count > 0, let photoAttribution = photos[0].htmlAttributions?[0].htmlUnescape(), let linkText = FormatUtils.getLinkAttributedText(photoAttribution) {
            details.append(linkText)
        }
        
        return details
    }
    
    func getLink() -> String? {
        if let photos = photos, photos.count > 0, let photoAttribution = photos[0].htmlAttributions?[0].htmlUnescape() {
            return FormatUtils.getLink(photoAttribution)
        }
        
        return nil
    }
    
    func getLinkText() -> NSMutableAttributedString? {
        if let photos = photos, photos.count > 0, let photoAttribution = photos[0].htmlAttributions?[0] {
            return FormatUtils.getLinkAttributedText(photoAttribution)
        }
        
        return nil
    }
    
    func imageUrl() -> String? {
        if let photos = photos, photos.count > 0, let photoReference = photos[0].photoReference {
            return "\(GoogleConstants.UrlComponents().PATH_PHOTOS)?\(GoogleConstants.ParameterKeys().MAX_WIDTH)=\(GoogleConstants.ParameterValues().MAX_WIDTH)&\(GoogleConstants.ParameterKeys().PHOTO_REFERENCE)=\(photoReference)&\(GoogleConstants.ParameterKeys().KEY)=\(SecretConstants.apiKeyGooglePlaces)"
        }
        return ""
    }
    
    func getNotes() -> String {
        return notes ?? ""
    }
    
    func setNotes(notes: String) {
        self.notes = notes
    }
    
    func encode() -> [String: Any] {
        return try! FirestoreEncoder().encode(self)
    }
}

struct Geometry: Codable {
    var location: Location
}

struct Location: Codable {
    var lat: Double?
    var lng: Double?
}

struct Photo: Codable {
    var photoReference: String?
    var htmlAttributions: [String]?
    var height: Int?
    var width: Int?
    
    private enum CodingKeys: String, CodingKey {
        case photoReference = "photo_reference"
        case htmlAttributions = "html_attributions"
        case height
        case width
    }
}

struct PlusCode: Codable {
    var compoundCode: String
    var globalCode: String
    
    private enum CodingKeys: String, CodingKey {
        case compoundCode = "compound_code"
        case globalCode = "global_code"
    }
}
