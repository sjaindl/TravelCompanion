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
    
    static func loginWithEmailIfNecessary(_ testCase: XCTestCase) -> Bool {
        let app = XCUIApplication()
        let welcomeGreeting = app.navigationBars["Welcome"]
        
        if welcomeGreeting.exists { //not signed in
            let signInEmailButton = app/*@START_MENU_TOKEN@*/.buttons["Sign in with email"]/*[[".buttons[\"Sign in with email\"]",".buttons[\"EmailButtonAccessibilityID\"]"],[[[-1,1],[-1,0]]],[1]]@END_MENU_TOKEN@*/
            signInEmailButton.tap()
            
            let tablesQuery = app.tables
            let emailTextField = tablesQuery/*@START_MENU_TOKEN@*/.textFields["Enter your email"]/*[[".cells[\"EmailCellAccessibilityID\"].textFields[\"Enter your email\"]",".textFields[\"Enter your email\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
            emailTextField.tap()
            emailTextField.typeText("tester@test.com")
            app.buttons["Next"].tap()
            
            let label = app.staticTexts["Password"]
            let exists = NSPredicate(format: "exists == 1")
            
            testCase.expectation(for: exists, evaluatedWith: label, handler: nil)
            testCase.waitForExpectations(timeout: 10, handler: nil)
            
            let passwordTextField = tablesQuery/*@START_MENU_TOKEN@*/.secureTextFields["Enter your password"]/*[[".cells.secureTextFields[\"Enter your password\"]",".secureTextFields[\"Enter your password\"]"],[[[-1,1],[-1,0]]],[0]]@END_MENU_TOKEN@*/
            passwordTextField.tap()
            
            UIPasteboard.general.string = "test123"
            passwordTextField.tap()
            passwordTextField.tap()
            app.menuItems.element(boundBy: 0).tap()
            Thread.sleep(forTimeInterval: 1)
            
            app.navigationBars["Sign in"].buttons["Sign in"].tap()
            
            Thread.sleep(forTimeInterval: 4)
            
            return true
        }
        
        return false
    }
}
