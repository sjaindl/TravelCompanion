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
        let destinationImage = app.scrollViews.otherElements.images["placeholder"]
        let exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: destinationImage, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
    }
    
    func testPlanShowDetail() {
        addTrip()
        
        let app = XCUIApplication()
        app.navigationBars["Mountain View"].buttons["Add Plan"].tap()
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        searchedCell.tap()
        Thread.sleep(forTimeInterval: 1)
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
        XCTAssertTrue(elementsQuery.staticTexts["Capital:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Washington, D.C."].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Language:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["English"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Currency:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["United States dollar (USD/$)"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Area:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Population:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Timezones:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Regions:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Americas, Northern America"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["ISO code:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["US"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Calling codes:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["+1"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Domains:"].exists)
        XCTAssertTrue(elementsQuery.staticTexts[".us"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Native name:"].exists)
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
        Thread.sleep(forTimeInterval: 5)
        app.collectionViews.children(matching: .cell).element(boundBy: 1).children(matching: .other).element.tap()
        Thread.sleep(forTimeInterval: 1)
        
        //photo detail:
        let photoDetailNavigationBar = app.navigationBars["Photo Detail"]
        XCTAssertTrue(photoDetailNavigationBar.exists)
        photoDetailNavigationBar.buttons["Mountain View"].tap()
        
        //Wikis:
        tabBarsQuery.buttons["Info"].tap()
        XCTAssertTrue(app.buttons["WikiVoyage"].exists)
        XCTAssertTrue(app.buttons["Wikipedia"].exists)
        XCTAssertTrue(app.buttons["Google"].exists)
        XCTAssertTrue(app.buttons["Lonelyplanet"].exists)
    }
    
    func testPlanAddPlannableAndNote() {
        let app = XCUIApplication()
        
        let searchedCell = app.filterCells(containing: ["Mountain View"]).element
        
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        
        searchedCell.tap()
        Thread.sleep(forTimeInterval: 2)
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
        let element = tablesQuery.staticTexts["\(formattedDate), Graz - Frankfurt"].firstMatch
        XCTAssertTrue(element.exists)
        
        element.tap()
        Thread.sleep(forTimeInterval: 2)
        let addNoteButton = app.alerts["Choose action"].buttons["Add Note"]
        addNoteButton.tap()
        Thread.sleep(forTimeInterval: 2)
        let noteField = app.textFields["Add notes.."]
          
        noteField.tap()
        Thread.sleep(forTimeInterval: 2)
        UIPasteboard.general.string = "test"
        noteField.tap()
        app.menuItems.element(boundBy: 0).tap()
        Thread.sleep(forTimeInterval: 2)
        
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
        Thread.sleep(forTimeInterval: 2)
        app.alerts["Choose action"].buttons["Show"].tap()
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.MM.yyyy"
        let formattedDate = dateFormatter.string(from: Date())
        
        let tablesQuery = app.tables
        var element = tablesQuery.staticTexts["\(formattedDate), Graz - Frankfurt"].firstMatch
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: element, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        XCTAssertTrue(element.exists)
        
        element.tap()
        Thread.sleep(forTimeInterval: 2)
        let deleteButton = app.alerts["Choose action"].buttons["Delete"]
        deleteButton.tap()
        
        Thread.sleep(forTimeInterval: 5)
        
        element = tablesQuery.staticTexts["\(formattedDate), Graz - Frankfurt"].firstMatch
        XCTAssertFalse(element.exists)
    }
    
    func addFlight() {
        let app = XCUIApplication()
        
        let addFlightNavigationBar = app.navigationBars["Add Flight"]
        
        let searchForOriginSearchField = addFlightNavigationBar.searchFields["Search for Origin"]
        searchForOriginSearchField.tap()
        searchForOriginSearchField.typeText("Graz")
        let tablesQuery = app.tables
        let grazAustriaStaticText = tablesQuery/*@START_MENU_TOKEN@*/.staticTexts["Graz, Austria"]/*[[".cells.staticTexts[\"Graz, Austria\"]",".staticTexts[\"Graz, Austria\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
        grazAustriaStaticText.tap()
        
        let searchForDestinationSearchField = addFlightNavigationBar.searchFields["Search for Destination"]
        searchForDestinationSearchField.tap()
        searchForDestinationSearchField.tap()
        Thread.sleep(forTimeInterval: 1)
        UIPasteboard.general.string = "Frankfurt"
        searchForDestinationSearchField.tap()
        app.menuItems.element(boundBy: 0).tap()
        let destinationStaticText = tablesQuery.staticTexts["Frankfurt am Main, Germany"]
        destinationStaticText.tap()
        
        XCTAssertTrue(app.datePickers.pickerWheels["April"].exists, "Date picker not shown")
        app/*@START_MENU_TOKEN@*/.staticTexts["Search"]/*[[".buttons[\"Search\"].staticTexts[\"Search\"]",".staticTexts[\"Search\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
        
        Thread.sleep(forTimeInterval: 1)
        
        let searchedCell = tablesQuery.staticTexts["Fly: Graz - Frankfurt am Main"].firstMatch
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedCell, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        searchedCell.tap()
        
        let searchedFlight = tablesQuery.staticTexts["Lufthansa, Airbus A319"]
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: searchedFlight, handler: nil)
        waitForExpectations(timeout: 20, handler: nil)
        searchedFlight.tap()
        
        app.alerts["Add Flight"].buttons["Single flight"].tap()
        Thread.sleep(forTimeInterval: 5)
    }

    func goToPlanScreen() {
        let app = XCUIApplication()
        app.images["plan"].tap()
        _ = UiTestUtils.loginWithEmailIfNecessary(self)
    }
    
    func addTrip() {
        let app = XCUIApplication()
        app.toolbars["Toolbar"].buttons["Add"].tap()
        
        let scrollViews = app.scrollViews
        
        XCTAssertTrue(scrollViews.otherElements/*@START_MENU_TOKEN@*/.pickerWheels["Mountain View"]/*[[".pickers.pickerWheels[\"Mountain View\"]",".pickerWheels[\"Mountain View\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.exists, "Destination not shown")
        let nextStaticText = scrollViews/*@START_MENU_TOKEN@*/.staticTexts["Next"]/*[[".buttons[\"Next\"].staticTexts[\"Next\"]",".staticTexts[\"Next\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
        nextStaticText.tap()
        
        XCTAssertTrue(scrollViews.textFields["destination name"].exists, "Destination display name not shown")
        nextStaticText.tap()
        
        XCTAssertTrue(scrollViews.datePickers/*@START_MENU_TOKEN@*/.pickerWheels["April"]/*[[".pickers.pickerWheels[\"April\"]",".pickerWheels[\"April\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.exists, "Start date not shown")
        nextStaticText.tap()
        
        XCTAssertTrue(scrollViews.datePickers/*@START_MENU_TOKEN@*/.pickerWheels["April"]/*[[".pickers.pickerWheels[\"April\"]",".pickerWheels[\"April\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.exists, "End date not shown")
        scrollViews/*@START_MENU_TOKEN@*/.staticTexts["Add Plan"]/*[[".buttons[\"Add Plan\"].staticTexts[\"Add Plan\"]",".staticTexts[\"Add Plan\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
    }
}
