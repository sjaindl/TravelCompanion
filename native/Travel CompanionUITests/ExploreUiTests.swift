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
        
        let searchField = app.navigationBars["searchBar"].searchFields["Search"]
        
        searchField.tap()
        UIPasteboard.general.string = "Mountain View, CA, USA"
        searchField.doubleTap()
        app.menuItems.element(boundBy: 0).tap()
    
        let tablesQuery = app.tables
        let text = tablesQuery.staticTexts["CA, USA"]
        var exists = NSPredicate(format: "exists == 1")
        
        expectation(for: exists, evaluatedWith: text, handler: nil)
        waitForExpectations(timeout: 15, handler: nil)
        
        tablesQuery.staticTexts["CA, USA"].tap()
        tablesQuery/*@START_MENU_TOKEN@*/.staticTexts["Mountain View, CA, USA"]/*[[".cells.staticTexts[\"Mountain View, CA, USA\"]",".staticTexts[\"Mountain View, CA, USA\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
        
        findLocationButton = app.toolbars["Toolbar"].buttons["Find new location"]
        exists = NSPredicate(format: "exists == 1")
        
        expectation(for: exists, evaluatedWith: findLocationButton, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
        
        XCTAssertTrue(findLocationButton.exists, "Not returned to explore screen after search")
    }
    
    //explore detail is tested via "show detail" in plan feature
    
    func goToExploreScreen() {
        UiTestUtils.loginWithEmailIfNecessary(self)
        
        let app = XCUIApplication()
        app.images["explore"].tap()
    }
}
