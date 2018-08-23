//
//  Country+Extensions.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 15.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Foundation

@objc(Country)
extension Country {
    public override func awakeFromInsert() {
        super.awakeFromInsert()
        creationDate = Date()
    }
    
    convenience init(result: [String: AnyObject], pin: Pin, insertInto context: NSManagedObjectContext!) {
        self.init(context: context)
        
        var pins = self.pins?.allObjects as! [Pin]
        pins.append(pin)
        self.pins = NSSet(array: pins)
        
        pin.countryOfPin = self
        
        storeResultArray(result: result)
    }
    
    func storeResultArray(result: [String: AnyObject]) {
        if let countryName = result[RestCountriesConstants.ResponseKeys.NAME] as? String {
            self.country = countryName
        }
        
        if let capital = result[RestCountriesConstants.ResponseKeys.CAPITAL] as? String {
            self.capital = capital
        }
        
        if let area = result[RestCountriesConstants.ResponseKeys.AREA] as? Float {
            self.area = area
        }
        
        if let population = result[RestCountriesConstants.ResponseKeys.POPULATION] as? Int32 {
            self.population = population
        }
        
        if let alphaCode = result[RestCountriesConstants.ResponseKeys.ALPHA_CODE] as? String {
            self.isoCode = alphaCode
            
            let flag = "https://www.countryflags.io/\(alphaCode)/flat/64.png"
            if let url = URL(string: flag) {
                try? self.flag = Data(contentsOf: url)
            }
        }
        
        if let nativeName = result[RestCountriesConstants.ResponseKeys.NATIVE_NAME] as? String {
            self.nativeName = nativeName
        }
        
        if let region = result[RestCountriesConstants.ResponseKeys.REGION] as? String {
            self.region = region
            if let subregion = result[RestCountriesConstants.ResponseKeys.SUBREGION] as? String {
                self.region?.append(", \(subregion)")
            }
        }
        
        if let languages = result[RestCountriesConstants.ResponseKeys.LANGUAGES] as? [[String:String]], languages.count > 0 {
            var countryLanguage = ""
            
            for language in languages {
                if let language = language[RestCountriesConstants.ResponseKeys.LANGUAGE_NAME] {
                    countryLanguage.append(language + ", ")
                }
            }
            self.languages = countryLanguage.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let currencies = result[RestCountriesConstants.ResponseKeys.CURRENCIES] as? [[String:String]], currencies.count > 0 {
            var countryCurrency = ""
            
            for currency in currencies {
                if let name = currency[RestCountriesConstants.ResponseKeys.CURRENCY_NAME], let code = currency[RestCountriesConstants.ResponseKeys.CURRENCY_CODE], let symbol = currency[RestCountriesConstants.ResponseKeys.CURRENCY_SYMBOL] {
                    countryCurrency.append("\(name) (\(code)/\(symbol)), ")
                }
            }
            self.currencies = countryCurrency.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let timezones = result[RestCountriesConstants.ResponseKeys.TIMEZONES] as? [String], timezones.count > 0 {
            var countryTimezones = ""
            
            for timezone in timezones {
                countryTimezones.append(timezone + ", ")
            }
            self.timezones = countryTimezones.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let domains = result[RestCountriesConstants.ResponseKeys.DOMAINS] as? [String], domains.count > 0 {
            var countryDomains = ""
            
            for domain in domains {
                countryDomains.append(domain + ", ")
            }
            self.domains = countryDomains.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let callingCodes = result[RestCountriesConstants.ResponseKeys.CALLING_CODES] as? [String], callingCodes.count > 0 {
            var countrCallingCodes = ""
            
            for callingCode in callingCodes {
                countrCallingCodes.append("+" + callingCode + ", ")
            }
            self.callingCodes = countrCallingCodes.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let blocks = result[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS] as? [[String:AnyObject]], blocks.count > 0 {
            var regionalBlocks = ""
            
            for block in blocks {
                if let name = block[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS_NAME] as? String, let acronym = block[RestCountriesConstants.ResponseKeys.REGIONAL_BLOCKS_ACRONYM] as? String {
                    regionalBlocks.append("\(name) (\(acronym)), ")
                }
            }
            self.regionalBlocks = regionalBlocks.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
    }
}
