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
        
        var findLocationButton = app.toolbars["Toolbar"].children(matching: .other).element.children(matching: .other).element.children(matching: .button).element
        XCTAssertTrue(findLocationButton.exists, "No find new location button in explore screen")
        findLocationButton.tap()
        
        let searchField = app.navigationBars["Search for Place"].searchFields["Search for Place"]
        
        searchField.tap()
        searchField.tap()
        Thread.sleep(forTimeInterval: 1)
        searchField.typeText("Mountain View, CA, USA")
        
        let tablesQuery = app.tables
        let text = tablesQuery.staticTexts["Mountain View, CA, USA"]
        var exists = NSPredicate(format: "exists == 1")
        
        expectation(for: exists, evaluatedWith: text, handler: nil)
        waitForExpectations(timeout: 15, handler: nil)
        
        text.tap()
        
        findLocationButton = app.toolbars["Toolbar"].children(matching: .other).element.children(matching: .other).element.children(matching: .button).element
        exists = NSPredicate(format: "exists == 1")
        
        expectation(for: exists, evaluatedWith: findLocationButton, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
        
        XCTAssertTrue(findLocationButton.exists, "Not returned to explore screen after search")
    }
    
    //explore detail is tested via "show detail" in plan feature
    
    func goToExploreScreen() {
        let app = XCUIApplication()
        app.images["explore"].tap()
    }
}
