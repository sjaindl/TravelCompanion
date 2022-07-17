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
        
        if var allPins = self.pins?.allObjects as? [Pin] {
            allPins.append(pin)
            self.pins = NSSet(array: allPins)
        }
        
        pin.countryOfPin = self
        
        storeResultArray(result: result)
    }
    
    func storeResultArray(result: [String: AnyObject]) {
        if let countryName = result[CountryApiConstants.ResponseKeys.name] as? String {
            self.country = countryName
        }
        
        if let capital = result[CountryApiConstants.ResponseKeys.capital] as? String {
            self.capital = capital
        }
        
        if let area = result[CountryApiConstants.ResponseKeys.area] as? Float {
            self.area = area
        }
        
        if let population = result[CountryApiConstants.ResponseKeys.population] as? Int32 {
            self.population = population
        }
        
        if let alphaCode = result[CountryApiConstants.ResponseKeys.alpha2Code] as? String {
            self.isoCode = alphaCode
        }
        
        if let flags = result[CountryApiConstants.ResponseKeys.flag],
            let flagUrl = flags[CountryApiConstants.ResponseKeys.flagMedium] as? String,
            let url = URL(string: flagUrl) {
                try? self.flag = Data(contentsOf: url)
        } else if let isoCode = isoCode {
            //let flag = "https://www.countryflags.io/\(alphaCode)/flat/64.png"
            // https://www.countryflags.io is down, using backed up github replacement:
            let flag = "https://github.com/nphotchkin/countryflags.io/blob/main/flags/\(isoCode.lowercased()).png"
            
            if let url = URL(string: flag) {
                try? self.flag = Data(contentsOf: url)
            }
        }
        
        if let nativeNames = result[CountryApiConstants.ResponseKeys.nativeNames] as? [String: AnyObject],
           let nativeName = nativeNames.first?.value[CountryApiConstants.ResponseKeys.official] as? String {
               self.nativeName = nativeName
        }

        if let region = result[CountryApiConstants.ResponseKeys.region] as? String {
            self.region = region
            if let subregion = result[CountryApiConstants.ResponseKeys.subregion] as? String {
                self.region?.append(", \(subregion)")
            }
        }
        
        if let languages = result[CountryApiConstants.ResponseKeys.languages] as? [String: String], languages.count > 0 {
            var countryLanguage = ""
            
            for language in languages {
                countryLanguage.append(language.value + ", ")
            }
            self.languages = countryLanguage.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let currencies = result[CountryApiConstants.ResponseKeys.currencies] as? [String: AnyObject], !currencies.isEmpty {
            var countryCurrency = ""
            
            currencies.forEach {
                if let currency = $0.value as? [String: AnyObject],
                    let name = currency[CountryApiConstants.ResponseKeys.currencyName],
                    let symbol = currency[CountryApiConstants.ResponseKeys.currencySymbol] {
                    countryCurrency.append("\(name) (\($0.key)/\(symbol)), ")
                }
            }
            self.currencies = countryCurrency.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let timezones = result[CountryApiConstants.ResponseKeys.timezones] as? [String], timezones.count > 0 {
            var countryTimezones = ""
            
            for timezone in timezones {
                countryTimezones.append(timezone + ", ")
            }
            self.timezones = countryTimezones.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let domains = result[CountryApiConstants.ResponseKeys.topLevelDomain] as? [String], domains.count > 0 {
            var countryDomains = ""
            
            for domain in domains {
                countryDomains.append(domain + ", ")
            }
            self.domains = countryDomains.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let callingCode = result[CountryApiConstants.ResponseKeys.callingCode] as? String {
            self.callingCodes = callingCode
        }
        
        if let blocks = result[CountryApiConstants.ResponseKeys.regionalBlocks] as? [[String:AnyObject]], blocks.count > 0 {
            var regionalBlocks = ""
            
            for block in blocks {
                if let name = block[CountryApiConstants.ResponseKeys.regionalBlocksName] as? String, let acronym = block[RestCountriesConstants.ResponseKeys.regionalBlocksAcronym] as? String {
                    regionalBlocks.append("\(name) (\(acronym)), ")
                }
            }
            self.regionalBlocks = regionalBlocks.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
    }
}
