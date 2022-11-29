//
//  CoreDataClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation
import GooglePlaces
import shared

class CoreDataClient {
    
    static let sharedInstance = CoreDataClient()
    
    private init() {}
    
    func storePin(_ dataController: DataController, place: PlacesDetailsResponse, placeId: String, countryCode: String?) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = place.result.geometry.location.lat
        pin.longitude = place.result.geometry.location.lng
        pin.name = place.result.name
//        pin.phoneNumber = place.phoneNumber
//        pin.rating = place.rating
        pin.address = place.result.formattedAddress
        
        pin.countryCode = countryCode
        
        if let addressComponents = place.result.addressComponents {
            for component in addressComponents {
//                for type in component.types {
//                    let placeType = PlaceType(context: dataController.viewContext)
//                    placeType.pin = pin
//                    placeType.type = type
                    
                    if component.types.contains("country") {
                        pin.country = component.longName
                    }
//                }
                
            }
        }
        
        pin.placeId = placeId
        pin.url = place.result.url
        
        try? dataController.save()
        
        return pin
    }
    
    func storePin(_ dataController: DataController, placeId: String, latitude: Double, longitude: Double) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = latitude
        pin.longitude = longitude
        pin.placeId = placeId
        
        try? dataController.save()
        
        return pin
    }
    
    func storeCountry(_ dataController: DataController, pin: Pin, result: [String: AnyObject]) -> Country {
        let country = Country(result: result, pin: pin, insertInto: dataController.viewContext)
        
        try? dataController.save()
        
        return country
    }
    
    func storePhoto(_ dataController: DataController, placePhoto: GMSPlacePhotoMetadata, pin: Pin, fetchType: Int, completion: @escaping (_ error: String?) -> Void) {
        GMSPlacesClient.shared().loadPlacePhoto(placePhoto, callback: {
            (placePhoto, error) -> Void in
            if let error = error {
                completion(error.localizedDescription)
            } else {
                let photo = Photos(context: dataController.viewContext)
                
                photo.pin = pin
                photo.type = Int16(fetchType)
                photo.title = pin.name
                photo.imageData = placePhoto!.pngData()
                try? dataController.save()
            }
        })
    }
    
    func storePhoto(_ dataController: DataController, photo: [String: AnyObject], pin: Pin, fetchType: Int) -> Photos? {
        if let title = photo[FlickrConstants.ResponseKeys.title] as? String,
            /* Does the photo have a key for the specified image size? */
            let imageUrlString = photo[FlickrConstants.ResponseKeys.imageSize] as? String {
            
            let photo = Photos(context: dataController.viewContext)
            
            photo.pin = pin
            photo.type = Int16(fetchType)
            photo.title = title
            photo.imageUrl = imageUrlString
            
            try? dataController.save()
            
            let photoId = photo.objectID
            
            if let url = URL(string: imageUrlString) {
                let backgroundContext:NSManagedObjectContext = dataController.backgroundContext
                
                backgroundContext.perform {
                    if let backgroundPhoto = backgroundContext.object(with: photoId) as? Photos {
                        try? backgroundPhoto.imageData = Data(contentsOf: url)
                        try? backgroundContext.save()
                    }
                }
            }
            
            return photo
        }
        
        return nil
    }
    
    func findPinByName(_ name: String, pins: [Pin]) -> Pin? {
        for pin in pins {
            if let pinName = pin.name {
                if pinName == name {
                    return pin
                }
            }
        }
        
        return nil
    }
}
