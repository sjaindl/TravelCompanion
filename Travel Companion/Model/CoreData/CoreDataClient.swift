//
//  CoreDataClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation
import GooglePlacePicker

class CoreDataClient {
    
    static let sharedInstance = CoreDataClient()
    
    private init() {}
    
    func storePin(_ dataController: DataController, place: GMSPlace, countryCode: String?) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = place.coordinate.latitude
        pin.longitude = place.coordinate.longitude
        pin.name = place.name
        pin.phoneNumber = place.phoneNumber
        pin.rating = place.rating
        pin.address = place.formattedAddress
        
        pin.countryCode = countryCode
        
        if let addressComponents = place.addressComponents {
            for component in addressComponents {
                if component.type == "country" {
                    pin.country = component.name
                    break
                }
            }
        }
        
        pin.placeId = place.placeID
        pin.url = place.website
        
        for type in place.types {
            let placeType = PlaceType(context: dataController.viewContext)
            placeType.pin = pin
            placeType.type = type
        }
        
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
    
    func storePhoto(_ dataController: DataController, placePhoto: GMSPlacePhotoMetadata, pin: Pin, fetchType: Int) {
        GMSPlacesClient.shared().loadPlacePhoto(placePhoto, callback: {
            (placePhoto, error) -> Void in
            if let error = error {
                // TODO: handle the error.
                print("Error: \(error.localizedDescription)")
            } else {
                let photo = Photos(context: dataController.viewContext)
                
                photo.pin = pin
                photo.type = Int16(fetchType)
                photo.title = pin.name
                photo.imageData = UIImagePNGRepresentation(placePhoto!)
                try? dataController.save()
            }
        })
    }
    
    func storePhoto(_ dataController: DataController, photo: [String: AnyObject], pin: Pin, fetchType: Int) -> Photos? {
        if let title = photo[FlickrConstants.FlickrResponseKeys.Title] as? String,
            /* Does the photo have a key for 'url_l'? */
            let imageUrlString = photo[FlickrConstants.FlickrResponseKeys.ImageSize] as? String {
            
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
}

