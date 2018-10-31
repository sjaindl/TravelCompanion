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

class GooglePlace: NSObject, Plannable {
    var id: String
    var icon: String?
    var name: String
    var placeId: String
    var rating: Double?
    var reference: String
    var scope: String
    var types: [String]?
    var vicinity: String
    var geometry: Geometry?
    var photos: [Photo]?
    var plusCode: PlusCode?
    var priceLevel: Int? // 0: free, 1: inexpensive, 2: moderate, 3: expensive, 4: very Expensive
    var permanentlyClosed: Bool?
    var htmlAttributions: [String]? = []
    
    public var notes: String?
    
    private enum CodingKeys: String, CodingKey {
        case id
        case icon
        case name
        case placeId = "place_id"
        case rating
        case reference
        case scope
        case types
        case vicinity
        case geometry
        case photos
        case plusCode = "plus_code"
        case priceLevel = "price_level"
        case permanentlyClosed = "permanently_closed"
        case htmlAttributions = "html_attributions"
        case notes
    }
    
    init(id: String, name: String, placeId: String, reference: String, scope: String, vicinity: String) {
        self.id = id
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
        var details = NSMutableAttributedString(string: vicinity)
        
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
            return "\(GoogleConstants.UrlComponents.pathPhotos)?\(GoogleConstants.ParameterKeys.maxWidth)=\(GoogleConstants.ParameterValues.maxWidth)&\(GoogleConstants.ParameterKeys.photoReference)=\(photoReference)&\(GoogleConstants.ParameterKeys.key)=\(SecretConstants.apiKeyGooglePlaces)"
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
    var latitude: Double?
    var longitude: Double?
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

/*
 This would be the PlaceDetails, if it is used in future:
 
class PlaceDetails {
    let formattedAddress: String
    var name: String? = nil
    
    var streetNumber: String? = nil
    var route: String? = nil
    var postalCode: String? = nil
    var country: String? = nil
    var countryCode: String? = nil
    
    var locality: String? = nil
    var subLocality: String? = nil
    var administrativeArea: String? = nil
    var administrativeAreaCode: String? = nil
    var subAdministrativeArea: String? = nil
    
    var coordinate: CLLocationCoordinate2D? = nil
 
 var description: String {
 return "\nAddress: \(formattedAddress)\ncoordinate: (\(coordinate?.latitude ?? 0), \(coordinate?.longitude ?? 0))\n"
 }
}
*/
