//
//  Travel_CompanionTests.swift
//  Travel CompanionTests
//
//  Created by Stefan Jaindl on 12.04.20.
//  Copyright © 2020 Stefan Jaindl. All rights reserved.
//

import CoreData
import CoreLocation
import Firebase
import RxSwift
import XCTest

@testable import Travel_Companion
class NetworkInteractionTest: XCTestCase {
    
    var mockDataController: DataController!
    var mockPersistantContainer: NSPersistentContainer!
    
    override func setUp() {
        super.setUp()
        
        let managedObjectModel: NSManagedObjectModel = {
            let managedObjectModel = NSManagedObjectModel.mergedModel(from: [Bundle(for: type(of: self))] )!
            return managedObjectModel
        }()
        
        mockPersistantContainer = {
            let container = NSPersistentContainer(name: "TravelCompanion", managedObjectModel: managedObjectModel)
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
        
        mockDataController = DataController(persistentContainer: mockPersistantContainer)
        mockDataController.backgroundContext = mockPersistantContainer.newBackgroundContext()
        
        FirebaseApp.configure()
    }
    
    override func tearDown() {
        flushData(for: "Country")
        flushData(for: "Pin")
        
        mockDataController = nil
        mockPersistantContainer = nil
        
        FirebaseApp.app()?.delete() { _ in }
        
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
            
            if let code = code {
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
        
        var photo: Photos!
        var errorResponse: String?
        
        let promise = expectation(description: "Country code successfully returned")
        
        // when
        var queryItems = FlickrClient.sharedInstance.buildQueryItems()
        queryItems[FlickrConstants.ParameterKeys.text] = "Austria"
        
        FlickrClient.sharedInstance.fetchPhotos(with: queryItems) { (error, isEmpty, photos) in
            
            if let error = error {
                errorResponse = error.description
            } else if let photos = photos, photos.count > 0 {
                let pin = CoreDataClient.sharedInstance.storePin(self.mockDataController!, placeId: "fakePlaceId", latitude: latitude, longitude: longitude)
                photo = CoreDataClient.sharedInstance.storePhoto(self.mockDataController, photo: photos[0], pin: pin, fetchType: fetchType)
            } else {
                errorResponse = "no country code returned"
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 5, handler: nil)
        
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
    
    func testWikiClientResponse() {
        // given
        let wikiArticleName = "Austria"
        let domain = WikiConstants.UrlComponents.domainWikipedia
        var errorResponse: String?
        
        var url: URL?
        
        let promise = expectation(description: "Wiki page URL successfully returned")
        
        // when
        WikiClient.sharedInstance.fetchWikiLink(country: wikiArticleName, domain: domain) { (error, wikiLink) in
            if let error = error {
                errorResponse = error.description
            } else {
                url = URL(string: wikiLink!)
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 5, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(url)
    }
    
    func testRome2RioSearchClientResponse() {
        // given
        let origin = "Graz"
        let destination = "Singapore"
        let transportDelegate = AddFlightSearchDelegate()
        
        var response: SearchResponse?
        var errorResponse: String?
        
        let promise = expectation(description: "Search results successfully returned")
        
        // when
        let queryItems = transportDelegate.buildSearchQueryItems(origin: origin, destination: destination)
        
        Rome2RioClient.sharedInstance.search(with: queryItems) { (error, searchResponse) in
            if let error = error {
                errorResponse = error.description
            }
            
            if searchResponse == nil {
                errorResponse = "No search response returned"
            } else {
                response = searchResponse
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 15, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(response)
        
        XCTAssertTrue(response!.places.count > 0)
        XCTAssertNotNil(response!.airlines.count > 0)
        XCTAssertNotNil(response!.routes.count > 0)
    }
    
    func testRome2RioAutoCompleteClientResponse() {
        // given
        let textToAutocomplete = "Gra"
        
        var response: [String]?
        var errorResponse: String?
        
        let promise = expectation(description: "Autocomplete results successfully returned")
        
        // when
        _ = Rome2RioClient.sharedInstance.autocomplete(with: textToAutocomplete).catchErrorJustReturn([])
            .observeOn(MainScheduler.instance)
            .subscribe(onNext: { autoCompleteResponse in
                
                if autoCompleteResponse.count == 0 {
                    errorResponse = "No autocomplete response returned"
                } else {
                    response = autoCompleteResponse
                }
                
                promise.fulfill()
            })
        
        waitForExpectations(timeout: 15, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(response)
        
        XCTAssertTrue(response!.count > 0)
    }
    
    func testFirestore() {
        // given
        let path = "test/" + FirestoreClient.storageByPath(path: FirestoreConstants.Collections.plans, fileName: "testFile")
        let storageRef = Storage.storage().reference()
        let url = URL(string: "https://www.countryflags.io/AT/flat/64.png")
        let data = try! Data(contentsOf: url!)
            //FirestoreClient.newDatabaseInstance().collection("test").document("testDoc").collection(FirestoreConstants.Collections.plans)
        
        var metadataResponse: StorageMetadata?
        var errorResponse: String?
        
        let promise = expectation(description: "Photo successfully stored in Firestore")
        
        // when
        FirestoreClient.storePhoto(storageRef: storageRef, path: path, photoData: data) { (metadata, error) in
            if let error = error {
                errorResponse = error.localizedDescription
            }
            
            if metadata == nil {
                errorResponse = "No metadata returned"
            } else {
                metadataResponse = metadata
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 30, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(metadataResponse)
        
        XCTAssertNotNil(metadataResponse!.path)
    }
    
    func testGooglePlacesClientResponse() {
        // given
        let searchText = "Marina Bay Sands"
        let coordinate = CLLocationCoordinate2D(latitude: 1.290270, longitude: 103.851959)
        let placeType = GooglePlaceType.lodging
        
        var foundPlaces: [GooglePlace]?
        var errorResponse: String?
        
        let promise = expectation(description: "Autocomplete results successfully returned")
        
        // when
        GoogleClient.sharedInstance.searchPlaces(for: searchText, coordinate: coordinate, type: placeType.key, radius: "10") { (error, places) in
            if let error = error {
                errorResponse = error.description
            }
            
            if places.isEmpty {
                errorResponse = "No Google places returned"
            } else {
                foundPlaces = places
            }
            
            promise.fulfill()
        }
        
        waitForExpectations(timeout: 5, handler: nil)
        
        // then
        XCTAssertNil(errorResponse)
        XCTAssertNotNil(foundPlaces)
        
        XCTAssertTrue(foundPlaces!.count > 0)
        XCTAssertTrue(foundPlaces![0].name.contains("Marina Bay Sands"))
    }
    
    func flushData(for entity: String) {
        let fetchRequest: NSFetchRequest<NSFetchRequestResult> = NSFetchRequest<NSFetchRequestResult>(entityName: entity)
        let objs = try! mockPersistantContainer.viewContext.fetch(fetchRequest)
        
        for case let obj as NSManagedObject in objs {
            mockPersistantContainer.viewContext.delete(obj)
        }
        
        try? mockPersistantContainer.viewContext.save()
    }

}
