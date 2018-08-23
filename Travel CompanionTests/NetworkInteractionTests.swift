//
//  testNetworkInteractions.swift
//  Travel CompanionTests
//
//  Created by Stefan Jaindl on 20.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import XCTest

@testable import Travel_Companion

class NetworkInteractionTests: XCTestCase {
    
//    var sessionUnderTest: URLSession!
    var mockDataController: DataController!
    
    lazy var managedObjectModel: NSManagedObjectModel = {
        let managedObjectModel = NSManagedObjectModel.mergedModel(from: [Bundle(for: type(of: self))] )!
        return managedObjectModel
    }()
    
    lazy var mockPersistantContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "TravelCompanion", managedObjectModel: self.managedObjectModel)
        let description = NSPersistentStoreDescription()
        description.type = NSInMemoryStoreType
        description.shouldAddStoreAsynchronously = false // Make it simpler in test env
        
        container.persistentStoreDescriptions = [description]
        container.loadPersistentStores { (description, error) in
            // Check if the data store is in memory
            precondition( description.type == NSInMemoryStoreType )
            
            // Check if creating container wrong
            if let error = error {
                fatalError("Create an in-mem coordinator failed \(error)")
            }
        }
        return container
    }()
    
    override func setUp() {
        super.setUp()
        mockDataController = DataController(persistentContainer: mockPersistantContainer)
        mockDataController.backgroundContext = mockPersistantContainer.newBackgroundContext()
    }
    
    override func tearDown() {
        flushData()
        super.tearDown()
    }
    
    func testGeoNamesClientResponse() {
        // given
        let latitude = 46.617
        let longitude = 14.26
        let expectedCountryCode = "AT"
        var actualCountryCode: String?
        var errorResponse: String?
        
        let promise = expectation(description: "Country code successfully returned")
        
        // when
        GeoNamesClient.sharedInstance.fetchCountryCode(latitude: latitude, longitude: longitude) { (error, code) in
            if let error = error {
                errorResponse = error.description
            }
            
            if let code = code as? String {
                actualCountryCode = code
            } else {
                errorResponse = "no country code returned"
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 5, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertEqual(actualCountryCode, expectedCountryCode)
    }
    
    func testRestCountriesClientResponse() {
        // given
        let countryCode = "AT"
        
        let expectedCapital = "Vienna"
        let expectedLanguage = "German"
        let expectedCurrency = "Euro (EUR/€)"
        let expectedArea: Float = 83871.0
        let expectedTimezones = "UTC+01:00"
        let expectedRegion = "Europe, Western Europe"
        let expectedIsoCode = "AT"
        let expectedCallingCodes = "+43"
        let expectedDomains = ".at"
        let expectedNativeName = "Österreich"
        let expectedRegionalBlocks = "European Union (EU)"
        
        var country: Country?
        var errorResponse: String?
        
        let promise = expectation(description: "Country data successfully returned")
        
        // when
        RestCountriesClient.sharedInstance.fetchCountryDetails(of: countryCode) { (error, isEmpty, result) in
            DispatchQueue.main.async {
                if let error = error {
                    errorResponse = error.description
                } else {
                    if let result = result {
                        let pin = Pin(context: self.mockDataController.viewContext)
                        country = Country(result: result, pin: pin, insertInto: self.mockDataController.viewContext)
                        
                        try? self.mockDataController.save()
                    } else {
                        errorResponse = "No country data returned"
                    }
                }
                
                promise.fulfill()
            }
        }
        
        waitForExpectations(timeout: 5, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertEqual(country?.capital, expectedCapital)
        XCTAssertEqual(country?.languages, expectedLanguage)
        XCTAssertEqual(country?.currencies, expectedCurrency)
        XCTAssertEqual(country?.area, expectedArea)
        XCTAssertEqual(country?.timezones, expectedTimezones)
        XCTAssertEqual(country?.region, expectedRegion)
        XCTAssertEqual(country?.isoCode, expectedIsoCode)
        XCTAssertEqual(country?.callingCodes, expectedCallingCodes)
        XCTAssertEqual(country?.domains, expectedDomains)
        XCTAssertEqual(country?.nativeName, expectedNativeName)
        XCTAssertEqual(country?.regionalBlocks, expectedRegionalBlocks)
    }
    
    func testFlickrClientResponse() {
        // given
        let fetchType = FetchType.Country.rawValue
        let latitude = 46.617
        let longitude = 14.26
        
        var pin: Pin!
        var photo: Photos!
        var errorResponse: String?
        
        let promise = expectation(description: "Country code successfully returned")
        
        // when
        var queryItems = FlickrClient.sharedInstance.buildQueryItems()
        queryItems[FlickrConstants.FlickrParameterKeys.Text] = "Austria"
        
        FlickrClient.sharedInstance.fetchPhotos(with: queryItems) { (error, isEmpty, photos) in
            
            if let error = error {
                errorResponse = error.description
            } else if let photos = photos, photos.count > 0 {
                pin = CoreDataClient.sharedInstance.storePin(self.mockDataController, placeId: "fakePlaceId", latitude: latitude, longitude: longitude)
                photo = CoreDataClient.sharedInstance.storePhoto(self.mockDataController, photo: photos[0], pin: pin, fetchType: fetchType)
            } else {
                errorResponse = "no country code returned"
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 50, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(photo)
        XCTAssertNotNil(photo.title)
        XCTAssertNotNil(photo.imageUrl)
        
        if let title = photo.title, let url = photo.imageUrl {
            XCTAssertFalse(title.isEmpty)
            XCTAssertFalse(url.isEmpty)
        }
    }
    
    func testCountryFlags() {
        // given
        let alphaCode = "AT"
        let flag = "https://www.countryflags.io/\(alphaCode)/flat/64.png"
        var flagData: Data?
        
        // when
        if let url = URL(string: flag) {
            try? flagData = Data(contentsOf: url)
        }
        
        // then
        XCTAssertNotNil(flagData)
    }
    
    func flushData() {
        let fetchRequest:NSFetchRequest<NSFetchRequestResult> = NSFetchRequest<NSFetchRequestResult>(entityName: "Country")
        let objs = try! mockPersistantContainer.viewContext.fetch(fetchRequest)
        for case let obj as NSManagedObject in objs {
            mockPersistantContainer.viewContext.delete(obj)
        }
        
        try! mockPersistantContainer.viewContext.save()
    }
}
