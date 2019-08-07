//
//  PlanUiTests.swift
//  Travel CompanionUITests
//
//  Created by Stefan Jaindl on 01.11.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import XCTest

class PlanUiTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false

        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        XCUIApplication().launch()

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
        goToPlanScreen()
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testAddTrip() {
        addTrip()
        
        let app = XCUIApplication()
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        XCTAssertTrue(searchedCell.exists, "Plan was not added")
    }
    
    func testPlanShowDetail() {
        addTrip()
        
        let app = XCUIApplication()
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        
        searchedCell.tap()
        app.alerts["Choose action"].buttons["Show details"].tap()
        
        //check whether all detail elements are displayed correctly
        let scrollViewsQuery = app.scrollViews
        let elementsQuery = scrollViewsQuery.otherElements
        
        //country details:
        let title = elementsQuery.staticTexts["Mountain View"]
        let exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: title, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
        XCTAssertTrue(title.exists)
        XCTAssertTrue(elementsQuery.staticTexts["United States of America"].exists)
        let flag = scrollViewsQuery.otherElements.containing(.staticText, identifier:"Mountain View").children(matching: .other).element(boundBy: 1)
        XCTAssertTrue(flag.exists)
        XCTAssertTrue(elementsQuery.staticTexts["Capital: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Washington, D.C."].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Language: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["English"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Currency: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["United States dollar (USD/$)"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Area: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Population: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Timezones: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Region: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Americas, Northern America"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["ISO code: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["US"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Calling codes: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["+1"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Domains: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts[".us"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Native name: "].exists)
        XCTAssertTrue(elementsQuery.staticTexts["United States"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Regional blocks:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["North American Free Trade Agreement (NAFTA)"].exists)
        
        //photos:
        let tabBarsQuery = app.tabBars
        tabBarsQuery.buttons["Photos"].tap()
        XCTAssertTrue(app.buttons["Country"].exists)
        let placeButton = app.buttons["Place"]
        XCTAssertTrue(placeButton.exists)
        XCTAssertTrue(app.buttons["Location"].exists)
        placeButton.tap()
        app.collectionViews.children(matching: .cell).element(boundBy: 1).children(matching: .other).element.tap()
        
        //photo detail:
        let photoDetailNavigationBar = app.navigationBars["Photo Detail"]
        XCTAssertTrue(photoDetailNavigationBar.otherElements["Photo Detail"].exists)
        photoDetailNavigationBar.buttons["Mountain View"].tap()
        
        //Wikis:
        XCTAssertTrue(tabBarsQuery.buttons["WikiVoyage"].exists)
        XCTAssertTrue(tabBarsQuery.buttons["Wikipedia"].exists)
    }
    
    func testPlanAddPlannableAndNote() {
        let app = XCUIApplication()
        
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        
        searchedCell.tap()
        app.alerts["Choose action"].buttons["Show"].tap()
        
        let toolbar = app.toolbars["Toolbar"]
        
        let flightToolbarItem = toolbar.buttons["Flight"]
        let publicTransportToolbarItem = toolbar.buttons["Public Transport"]
        let hotelToolbarItem = toolbar.buttons["Hotel"]
        let restaurantToolbarItem = toolbar.buttons["Restaurant"]
        let attractionToolbarItem = toolbar.buttons["Attraction"]
        
        let flightHeader = app.tables.children(matching: .other)["FLIGHTS"].children(matching: .other)["FLIGHTS"]
        let publicTransportHeader = app.tables.children(matching: .other)["PUBLIC TRANSPORT"].children(matching: .other)["PUBLIC TRANSPORT"]
        let hotelHeader = app.tables.children(matching: .other)["HOTELS"].children(matching: .other)["HOTELS"]
        let restaurantHeader = app.tables.children(matching: .other)["RESTAURANTS"].children(matching: .other)["RESTAURANTS"]
        let attractionHeader = app.tables.children(matching: .other)["ATTRACTIONS"].children(matching: .other)["ATTRACTIONS"]
        
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: flightToolbarItem, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        XCTAssertTrue(flightToolbarItem.exists)
        XCTAssertTrue(publicTransportToolbarItem.exists)
        XCTAssertTrue(hotelToolbarItem.exists)
        XCTAssertTrue(restaurantToolbarItem.exists)
        XCTAssertTrue(attractionToolbarItem.exists)
        XCTAssertTrue(flightHeader.exists)
        XCTAssertTrue(publicTransportHeader.exists)
        XCTAssertTrue(hotelHeader.exists)
        XCTAssertTrue(restaurantHeader.exists)
        XCTAssertTrue(attractionHeader.exists)
        
        flightHeader.tap()
        addFlight()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        let formattedDate = dateFormatter.string(from: Date())
        
        let tablesQuery = app.tables
        let element = tablesQuery.staticTexts["\(formattedDate), Graz - Zurich"].firstMatch
        XCTAssertTrue(element.exists)
        
        element.tap()
        Thread.sleep(forTimeInterval: 2)
        let addNoteButton = app.alerts["Choose action"].buttons["Add Note"]
        addNoteButton.tap()
        let noteField = app.textFields["Add notes.."]
        UIPasteboard.general.string = "test"
        noteField.doubleTap()
        app.menuItems.element(boundBy: 0).tap()
        Thread.sleep(forTimeInterval: 1)
        
        let okButton = app.buttons["OK"]
        okButton.tap()
        
        Thread.sleep(forTimeInterval: 2)
        element.tap()
        addNoteButton.tap()
        Thread.sleep(forTimeInterval: 2)
        XCTAssertEqual(noteField.firstMatch.value as? String, "test")
    }
    
    func testPlanDelete() {
        let app = XCUIApplication()
        
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        
        searchedCell.tap()
        app.alerts["Choose action"].buttons["Show"].tap()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        let formattedDate = dateFormatter.string(from: Date())
        
        let tablesQuery = app.tables
        var element = tablesQuery.staticTexts["\(formattedDate), Graz - Zurich"].firstMatch
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: element, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        XCTAssertTrue(element.exists)
        
        element.tap()
        Thread.sleep(forTimeInterval: 2)
        let deleteButton = app.alerts["Choose action"].buttons["Delete"]
        deleteButton.tap()
        
        Thread.sleep(forTimeInterval: 5)
        
        element = tablesQuery.staticTexts["\(formattedDate), Graz - Zurich"].firstMatch
        XCTAssertFalse(element.exists)
    }
    
    func addFlight() {
        let app = XCUIApplication()
        
        let element = app.otherElements.containing(.navigationBar, identifier:"Add Flight").children(matching: .other).element.children(matching: .other).element.children(matching: .other).element.children(matching: .other).element
        let originTextField = element.children(matching: .textField).element(boundBy: 0)
        originTextField.tap()
        UIPasteboard.general.string = "Graz, Austria"
        originTextField.doubleTap()
        app.menuItems.element(boundBy: 0).tap()
        
        let destinationTextField = element.children(matching: .textField).element(boundBy: 1)
        destinationTextField.tap()
        UIPasteboard.general.string = "Los Angeles, California, USA"
        destinationTextField.doubleTap()
        app.menuItems.element(boundBy: 0).tap()
        
        Thread.sleep(forTimeInterval: 1)
        
        app.buttons["Search"].tap()
        
        Thread.sleep(forTimeInterval: 1)
        
        let tablesQuery = app.tables
        let searchedCell = tablesQuery.staticTexts["Fly to Los Angeles: Graz - Los Angeles"].firstMatch
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        searchedCell.tap()
        
        let searchedFlight = tablesQuery/*@START_MENU_TOKEN@*/.staticTexts["LX1513: Graz 10:45 - Zurich 12:05"]/*[[".cells.staticTexts[\"LX1513: Graz 10:45 - Zurich 12:05\"]",".staticTexts[\"LX1513: Graz 10:45 - Zurich 12:05\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedFlight, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        searchedFlight.tap()
        
        app.alerts["Add Flight"].buttons["Single flight"].tap()
        Thread.sleep(forTimeInterval: 5)
    }

    func goToPlanScreen() {
        UiTestUtils.loginWithEmailIfNecessary(self)
        
        let app = XCUIApplication()
        app.images["plan"].tap()
    }
    
    func addTrip() {
        let app = XCUIApplication()
        app.toolbars["Toolbar"].buttons["Add"].tap()
        
        let elementsQuery = app.scrollViews.otherElements
        elementsQuery.staticTexts["End Date: "].swipeUp()
        elementsQuery.buttons["Add trip"].tap()
        
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        
        let exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
    }
}
