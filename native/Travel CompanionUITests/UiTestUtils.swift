//
//  UiTestUitls.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 01.11.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import XCTest

class UiTestUtils {
    
    static func loginWithEmailIfNecessary(_ testCase: XCTestCase) {
        let app = XCUIApplication()
        let welcomeButton = app.navigationBars["Welcome"].otherElements["Welcome"]
        
        if welcomeButton.exists { //not signed in
            app/*@START_MENU_TOKEN@*/.buttons["EmailButtonAccessibilityID"]/*[[".buttons[\"Sign in with email\"]",".buttons[\"EmailButtonAccessibilityID\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
            
            let tablesQuery = app.tables
            tablesQuery/*@START_MENU_TOKEN@*/.textFields["Enter your email"]/*[[".cells[\"EmailCellAccessibilityID\"].textFields[\"Enter your email\"]",".textFields[\"Enter your email\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
            app.textFields["Enter your email"].typeText("tester@test.com")
            app.buttons["Next"].tap()
            
            let label = app.staticTexts["Password"]
            var exists = NSPredicate(format: "exists == 1")
            
            testCase.expectation(for: exists, evaluatedWith: label, handler: nil)
            testCase.waitForExpectations(timeout: 10, handler: nil)
            
            tablesQuery/*@START_MENU_TOKEN@*/.secureTextFields["Enter your password"]/*[[".cells.secureTextFields[\"Enter your password\"]",".secureTextFields[\"Enter your password\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.tap()
            tablesQuery/*@START_MENU_TOKEN@*/.secureTextFields["Enter your password"]/*[[".cells.secureTextFields[\"Enter your password\"]",".secureTextFields[\"Enter your password\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/.typeText("test123")
            app.navigationBars["Sign in"].buttons["Sign in"].tap()
            
            let exploreImage = app.images["explore"]
            exists = NSPredicate(format: "exists == 1")
            
            testCase.expectation(for: exists, evaluatedWith: exploreImage, handler: nil)
            testCase.waitForExpectations(timeout: 15, handler: nil)
        }
    }
}
