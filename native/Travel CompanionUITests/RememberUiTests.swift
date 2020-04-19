//
//  RememberUiTests.swift
//  Travel CompanionUITests
//
//  Created by Stefan Jaindl on 01.11.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import XCTest

class RememberUiTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false

        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        XCUIApplication().launch()

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
        goToRememberScreen()
    }

    func testRememberUi() {
        let app = XCUIApplication()
        let tripToRemember = app.tables/*@START_MENU_TOKEN@*/.staticTexts["Mountain View"]/*[[".cells.staticTexts[\"Mountain View\"]",".staticTexts[\"Mountain View\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
        
        var exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: tripToRemember, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
            
        tripToRemember.tap()
        XCTAssertTrue(app.toolbars["Toolbar"].buttons["addFromGallery"].exists)
        XCTAssertTrue(app.toolbars["Toolbar"].buttons["addFromCam"].exists)
        
        app.collectionViews.cells.children(matching: .other).element.tap() //tap photo
        let chooseActionAlert = app.alerts["Choose action"]
        let showPhotoAlertButton = chooseActionAlert.buttons["Show photo"]
        let deleteAlertButton = chooseActionAlert.buttons["Delete"]
        let cancelAlertButton = chooseActionAlert.buttons["Cancel"]
        XCTAssertTrue(showPhotoAlertButton.exists)
        XCTAssertTrue(deleteAlertButton.exists)
        XCTAssertTrue(cancelAlertButton.exists)
        showPhotoAlertButton.tap()
        
        let photoDetailNavigationBar = app.navigationBars["Photo Detail"]
        exists = NSPredicate(format: "exists == 1")
        expectation(for: exists, evaluatedWith: photoDetailNavigationBar, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
        XCTAssertTrue(photoDetailNavigationBar.exists)
    }
    
    func goToRememberScreen() {
        let app = XCUIApplication()
        app.images["remember"].tap()
        _ = UiTestUtils.loginWithEmailIfNecessary(self)
    }
}
