//
//  CoreDataClient.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import GooglePlacePicker

class CoreDataClient {
    
    static func storePin(_ dataController: DataController, place: GMSPlace) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = place.coordinate.latitude
        pin.longitude = place.coordinate.longitude
        pin.name = place.name
        pin.phoneNumber = place.phoneNumber
        pin.rating = place.rating
        pin.address = place.formattedAddress
        
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
    
    static func storePin(_ dataController: DataController, placeId: String, latitude: Double, longitude: Double) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = latitude
        pin.longitude = longitude
        pin.placeId = placeId

        try? dataController.save()
        
        return pin
    }
}

