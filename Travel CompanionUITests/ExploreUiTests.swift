//
//  testExplore.swift
//  Travel CompanionTests
//
//  Created by Stefan Jaindl on 21.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import XCTest

class ExploreUiTests: XCTestCase {
    
    
    override func setUp() {
        super.setUp()
        continueAfterFailure = false
        XCUIApplication().launch()
        
        goToExploreScreen()
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testFindAndViewLocation() {
        let app = XCUIApplication()
        
        var findLocationButton = app.toolbars["Toolbar"].buttons["Find new location"]
        XCTAssertTrue(findLocationButton.exists, "No find new location button in explore screen")
        findLocationButton.tap()
        
        app.navigationBars["Select a location"].buttons["Search"].tap()
        app.navigationBars["searchBar"].searchFields["Search"].tap()
        
        let searchSearchField = app.navigationBars["searchBar"].searchFields["Search"]
        
        searchSearchField.tap()
        UIPasteboard.general.string = "Mountain View, CA, USA"
        searchSearchField.doubleTap()
        app.menuItems.element(boundBy: 0).tap()
    
        let tablesQuery = app.tables
        tablesQuery/*@START_MENU_TOKEN@*/.staticTexts["CA, USA"]/*[[".cells.staticTexts[\"CA, USA\"]",".staticTexts[\"CA, USA\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
        tablesQuery/*@START_MENU_TOKEN@*/.staticTexts["Mountain View, CA, USA"]/*[[".cells.staticTexts[\"Mountain View, CA, USA\"]",".staticTexts[\"Mountain View, CA, USA\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
        
        findLocationButton = app.toolbars["Toolbar"].buttons["Find new location"]
        XCTAssertTrue(findLocationButton.exists, "Not returned to explore screen after search")
    }
    
    func testDelete() {
        let app = XCUIApplication()
        var deleteLocation = app.toolbars["Toolbar"].buttons["Delete location"]
        var tapPinToDeleteButton = app.toolbars["Toolbar"].buttons["Tap pin to delete"]
        XCTAssertTrue(deleteLocation.exists, "Delete button has wrong title or doesn't exist")
        XCTAssertFalse(tapPinToDeleteButton.exists, "Delete button should be hidden")
        
        deleteLocation.tap()
        deleteLocation = app.toolbars["Toolbar"].buttons["Delete location"]
        tapPinToDeleteButton = app.toolbars["Toolbar"].buttons["Tap pin to delete"]
        
        XCTAssertTrue(tapPinToDeleteButton.exists, "Delete button should be hidden")
        XCTAssertFalse(deleteLocation.exists, "Delete button has wrong title or doesn't exist")
    }
    
    func testExploreDetail() {
        // Use recording to get started writing UI tests.
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let controller = storyboard.instantiateViewController(withIdentifier: "ExploreDetail")
//        let controller = ExploreDetailViewController()
        UIApplication.shared.keyWindow?.rootViewController = controller
        
        let app = XCUIApplication()
        let scrollViewsQuery = app.scrollViews
        let elementsQuery = scrollViewsQuery.otherElements
        XCTAssertTrue(elementsQuery.staticTexts["Mountain View"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["political, locality"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["United States of America"].exists)
        XCTAssertTrue(elementsQuery.staticTexts["Washington, D.C."].exists)
        
        let tabBarsQuery = app.tabBars
        XCTAssertTrue(tabBarsQuery.buttons["Detail"].exists)
        XCTAssertTrue(tabBarsQuery.buttons["Photos"].exists)
        tabBarsQuery.buttons["Photos"].tap()
            
        XCTAssertTrue(app.buttons["Country"].exists)
        XCTAssertTrue(app.buttons["Place"].exists)
        XCTAssertTrue(app.buttons["Location"].exists)
    }
   
    func goToExploreScreen() {
        let app = XCUIApplication()
        let cancelButton = app.navigationBars["Welcome"].buttons["Cancel"]
        if cancelButton.exists {
            cancelButton.tap()
        }
        
        app.images["explore-1"].tap()
    }
}
