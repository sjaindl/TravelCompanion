//
//  GooglePlace.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Foundation

class GooglePlace: NSObject, Plannable {
    var id: String
    var icon: String?
    var name: String
    var place_id: String
    var rating: Double?
    var reference: String
    var scope: String
    var types: [String]?
    var vicinity: String
    var geometry: Geometry?
    var photos: [Photo]?
    var plus_code: PlusCode?
    //var html_attributions: [String] //TODO
    
    public var notes: String?
    
    init(id: String, name: String, placeId: String, reference: String, scope: String, vicinity: String) {
        self.id = id
        self.name = name
        self.place_id = placeId
        self.reference = reference
        self.scope = scope
        self.vicinity = vicinity
    }
    
    func getId() -> String {
        return place_id
    }
    
    func description() -> String {
        return "\(name), \(vicinity)"
    }
    
    func details() -> String {
        if let rating = rating {
            return "Rating: \(rating)"
        }
        return "No rating available"
    }
    
    func imageUrl() -> String? {
        if let photos = photos, photos.count > 0, let photoReference = photos[0].photo_reference {
            //TODO: fetch from google
            //return "\(Rome2RioConstants.UrlComponents.PROTOCOL)://\(Rome2RioConstants.UrlComponents.DOMAIN)\(agencyUrl)"
            return "\(GoogleConstants.UrlComponents.PATH_PHOTOS)?\(GoogleConstants.ParameterKeys.MaxWidth)=\(GoogleConstants.ParameterValues.MaxWidth)&\(GoogleConstants.ParameterKeys.PhotoReference)=\(photoReference)&\(GoogleConstants.ParameterKeys.Key)=\(SecretConstants.GOOGLE_PLACES_API_KEY)"
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
    var photo_reference: String?
    var html_attributions: [String]?
}

struct PlusCode: Codable {
    var compound_code: String
    var global_code: String
}

/*
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
