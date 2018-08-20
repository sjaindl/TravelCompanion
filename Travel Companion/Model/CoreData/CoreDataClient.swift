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
    
    static func storePin(_ dataController: DataController, place: GMSPlace, countryCode: String?) -> Pin {
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
    
    static func storePin(_ dataController: DataController, placeId: String, latitude: Double, longitude: Double) -> Pin {
        let pin = Pin(context: dataController.viewContext)
        pin.latitude = latitude
        pin.longitude = longitude
        pin.placeId = placeId

        try? dataController.save()
        
        return pin
    }
    
    static func storeCountry(_ dataController: DataController, pin: Pin, result: [String: AnyObject]) -> Country {
        let country = Country(context: dataController.viewContext)
        
        var pins = country.pins?.allObjects as! [Pin]
        pins.append(pin)
        country.pins = NSSet(array: pins)
        
        pin.countryOfPin = country
        
        if let countryName = result[RestCountriesConstants.ResponseKeys.NAME] as? String {
            country.country = countryName
        }
        
        if let capital = result[RestCountriesConstants.ResponseKeys.CAPITAL] as? String {
            country.capital = capital
        }
        
        if let area = result[RestCountriesConstants.ResponseKeys.AREA] as? Float {
            country.area = area
        }
        
        if let population = result[RestCountriesConstants.ResponseKeys.POPULATION] as? Int32 {
            country.population = population
        }
        
        if let alphaCode = result[RestCountriesConstants.ResponseKeys.ALPHA_CODE] as? String {
            country.isoCode = alphaCode
            
            let flag = "https://www.countryflags.io/\(alphaCode)/flat/64.png"
            if let url = URL(string: flag) {
                try? country.flag = Data(contentsOf: url)
            }
        }
        
        if let nativeName = result[RestCountriesConstants.ResponseKeys.NATIVE_NAME] as? String {
            country.nativeName = nativeName
        }
        
        if let region = result[RestCountriesConstants.ResponseKeys.REGION] as? String {
            country.region = region
            if let subregion = result[RestCountriesConstants.ResponseKeys.SUBREGION] as? String {
                country.region?.append(", \(subregion)")
            }
        }
        
        if let languages = result[RestCountriesConstants.ResponseKeys.LANGUAGES] as? [[String:String]], languages.count > 0 {
            var countryLanguage = ""
            
            for language in languages {
                if let language = language[RestCountriesConstants.ResponseKeys.LANGUAGE_NAME] {
                    countryLanguage.append(language + ", ")
                }
            }
            country.languages = countryLanguage.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let currencies = result[RestCountriesConstants.ResponseKeys.CURRENCIES] as? [[String:String]], currencies.count > 0 {
            var countryCurrency = ""
            
            for currency in currencies {
                if let name = currency[RestCountriesConstants.ResponseKeys.CURRENCY_NAME], let code = currency[RestCountriesConstants.ResponseKeys.CURRENCY_CODE], let symbol = currency[RestCountriesConstants.ResponseKeys.CURRENCY_SYMBOL] {
                    countryCurrency.append("\(name) (\(code)/\(symbol)), ")
                }
            }
            country.currencies = countryCurrency.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let timezones = result[RestCountriesConstants.ResponseKeys.TIMEZONES] as? [String], timezones.count > 0 {
            var countryTimezones = ""
            
            for timezone in timezones {
                countryTimezones.append(timezone + ", ")
            }
            country.timezones = countryTimezones.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let domains = result[RestCountriesConstants.ResponseKeys.DOMAINS] as? [String], domains.count > 0 {
            var countryDomains = ""
            
            for domain in domains {
                countryDomains.append(domain + ", ")
            }
            country.domains = countryDomains.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let callingCodes = result[RestCountriesConstants.ResponseKeys.CALLING_CODES] as? [String], callingCodes.count > 0 {
            var countrCallingCodes = ""
            
            for callingCode in callingCodes {
                countrCallingCodes.append("+" + callingCode + ", ")
            }
            country.callingCodes = countrCallingCodes.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let blocks = result[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS] as? [[String:AnyObject]], blocks.count > 0 {
            var regionalBlocks = ""
            
            for block in blocks {
                if let name = block[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS_NAME] as? String, let acronym = block[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS_ACRONYM] as? String {
                    regionalBlocks.append("\(name) (\(acronym)), ")
                }
            }
            country.regionalBlocks = regionalBlocks.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        try? dataController.save()
        
        return country
    }
}

