//
//  ExploreDetailViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 09.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import UIKit

public class ExploreDetailViewController: UIViewController {
    
    var pin: Pin!
    var dataController: DataController!
    
    var fetchedResultsController: NSFetchedResultsController<Country>!
    
    @IBOutlet weak var placeName: UILabel!
    @IBOutlet weak var placeType: UILabel!
    @IBOutlet weak var phoneTitle: UILabel!
    @IBOutlet weak var phone: UILabel!
    @IBOutlet weak var urlTitle: UILabel!
    @IBOutlet weak var url: UILabel!
    @IBOutlet weak var latitudeLongitude: UILabel!
    //    @IBOutlet weak var rating: UIProgressView!
    
    @IBOutlet weak var countryName: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var capital: UILabel!
    @IBOutlet weak var language: UILabel!
    @IBOutlet weak var currency: UILabel!
    @IBOutlet weak var area: UILabel!
    @IBOutlet weak var population: UILabel!
    @IBOutlet weak var timezones: UILabel!
    @IBOutlet weak var region: UILabel!
    @IBOutlet weak var isoCode: UILabel!
    @IBOutlet weak var callingCodes: UILabel!
    @IBOutlet weak var domains: UILabel!
    @IBOutlet weak var nativeName: UILabel!
    @IBOutlet weak var regionalBlocks: UILabel!
    
    func setPinData(_ pin: Pin) {
        placeName.text = pin.name
        
        if let placeTypes = pin.placetypes, let placeTypeArray = Array(placeTypes) as? [PlaceType], placeTypeArray.count > 0 {
            placeType.text = ""
            
            for type in placeTypeArray {
                placeType.text?.append(type.type! + ", ")
            }
            placeType.text = placeType.text?.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
        }
        
        if let phoneNumber = pin.phoneNumber {
            phone.text = phoneNumber
        } else {
            phone.isHidden = true
            phoneTitle.isHidden = true
        }
        
        let latitudePostfix = pin.latitude < 0 ? "S" : "N"
        let longitudePostfix = pin.longitude < 0 ? "W" : "E"
        latitudeLongitude.text = "\(pin.latitude)° \(latitudePostfix), \(pin.longitude)° \(longitudePostfix)"
        
        if let website = pin.url?.absoluteString {
            let linkString = NSMutableAttributedString(string: website)
            linkString.addAttribute(.link, value: URL(string: website) ?? website, range: NSMakeRange(0, website.count))
            url.attributedText = linkString
            
            url.isUserInteractionEnabled = true
            url.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(openLink)))
        } else {
            url.isHidden = true
            urlTitle.isHidden = true
        }
        
//        rating.progress = pin.rating / 5 //max rating is 5 --> divide by 5 to get a value between 0.0 and 1.0
        
        countryName.text = pin.country
    }
    
    @objc
    func openLink() {
        if let link = url.text, let url = URL(string: link) {
            UIApplication.shared.open(url, options: convertToUIApplicationOpenExternalURLOptionsKeyDictionary([:]), completionHandler: nil)
        }
    }
    
    func setCountryData(_ country: Country) {
        countryName.text = country.country
        
        if let flag = country.flag {
            imageView.image = UIImage(data: flag)
        }
        
        if let capital = country.capital, !capital.isEmpty {
            self.capital.text = capital
        }
        
        if let languages = country.languages, !languages.isEmpty {
            self.language.text = languages
        }
        
        if let currency = country.currencies, !currency.isEmpty {
            self.currency.text = currency
        }
        
        self.area.text = (country.area as NSNumber).description(withLocale: Locale.current) + " km²"
        self.population.text = (country.population as NSNumber).description(withLocale: Locale.current)
        
        if let timezones = country.timezones, !timezones.isEmpty {
            self.timezones.text = timezones
        }
        
        if let region = country.region, !region.isEmpty {
            self.region.text = region
        }
        
        if let isoCode = country.isoCode, !isoCode.isEmpty {
            self.isoCode.text = isoCode
        }
        
        if let callingCodes = country.callingCodes, !callingCodes.isEmpty {
            self.callingCodes.text = callingCodes
        }
        
        if let domains = country.domains, !domains.isEmpty {
            self.domains.text = domains
        }
        
        if let nativeName = country.nativeName, !nativeName.isEmpty {
            self.nativeName.text = nativeName
        }
        
        if let regionalBlocks = country.regionalBlocks, !regionalBlocks.isEmpty {
            self.regionalBlocks.text = regionalBlocks
        }
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        enableTabs(false)
        initResultsController()
        setPinData(pin)
        
        if let country: Country = pin.countryOfPin {
            setCountryData(country)
            enableTabs(true)
        } else {
            fetchCountry()
        }
    }
    
    func enableTabs(_ enable: Bool) {
        let arrayOfTabBarItems = self.tabBarController?.tabBar.items
        
        if let barItems = arrayOfTabBarItems, barItems.count > 0 {
            let tabBarItem = barItems[0]
            tabBarItem.isEnabled = enable
        }
    }
    
    func initResultsController() {
        var cacheName: String? = nil
        if let countryCode = pin.countryCode {
            cacheName = Constants.CoreData.CACHE_NAME_COUNTRIES + countryCode
        }
        
        let fetchRequest: NSFetchRequest<Country> = Country.fetchRequest()
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.SORT_KEY, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        fetchRequest.predicate = NSPredicate(format: "country == %@", pin.countryCode ?? "")
        fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: cacheName)
    }

    func fetchCountry() {
        do {
            try fetchedResultsController.performFetch()
            if let result = fetchedResultsController.fetchedObjects, result.count == 1 {
                let country = result[0]
                
                pin.countryOfPin = country
                try? dataController.save()
                
                setCountryData(country)
                enableTabs(true)
            } else {
                fetchNewCountryData()
            }

        } catch {
            fatalError("The fetch could not be performed: \(error.localizedDescription)")
        }
    }
    
    func fetchNewCountryData() {
        guard let newCountry = pin.countryCode else {
            debugPrint("This location is not in a country. Can't fetch country details.")
            self.enableTabs(true)
            return
        }
        
        RestCountriesClient.sharedInstance.fetchCountryDetails(of: newCountry) { (error, isEmpty, result) in
            DispatchQueue.main.async {
                print(result.debugDescription)
                if let error = error {
                    self.showError(error)
                } else {
                    guard let result = result else {
                        self.showError("No country data available!")
                        self.enableTabs(true)
                        return
                    }
                    
                    let country = CoreDataClient.sharedInstance.storeCountry(self.dataController, pin: self.pin, result: result)
                    self.setCountryData(country)
                }
                
                self.enableTabs(true)
            }
        }
    }
    
    func showError(_ error: String) {
        //show alertview with error message
        let alert = UIAlertController(title: "Error", message: error, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(alert, animated: true)
    }
}

fileprivate func convertToUIApplicationOpenExternalURLOptionsKeyDictionary(_ input: [String: Any]) -> [UIApplication.OpenExternalURLOptionsKey: Any] {
	return Dictionary(uniqueKeysWithValues: input.map { key, value in (UIApplication.OpenExternalURLOptionsKey(rawValue: key), value)})
}
