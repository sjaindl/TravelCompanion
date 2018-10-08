//
//  Travel_CompanionUITests.swift
//  Travel CompanionUITests
//
//  Created by Stefan Jaindl on 01.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import XCTest

class MainMenuUITests: XCTestCase {
    
    var app: XCUIApplication!
    
    override func setUp() {
        super.setUp()
        
        // Put setup code here. This method is called before the invocation of each test method in the class.
        
        app = XCUIApplication()
        
        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false
        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        app.launch()
        
        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testAuthenticationScreen() {
        let cancelButton = app.navigationBars["Welcome"].buttons["Cancel"]
        let signInEmailButton = app/*@START_MENU_TOKEN@*/.buttons["Sign in with email"]/*[[".buttons[\"Sign in with email\"]",".buttons[\"EmailButtonAccessibilityID\"]"],[[[-1,1],[-1,0]]],[1]]@END_MENU_TOKEN@*/
        let signInWithFacebookButton = app.buttons["Sign in with Facebook"]
        let signInWithGoogleButton = app.buttons["Sign in with Google"]
        
        XCTAssertTrue(cancelButton.exists, "No cancel button in authentication screen")
        XCTAssertTrue(signInEmailButton.exists, "No cancel button in authentication screen")
        XCTAssertTrue(signInWithFacebookButton.exists, "No cancel button in authentication screen")
        XCTAssertTrue(signInWithGoogleButton.exists, "No cancel button in authentication screen")
    }
    
    func testMainMenuElementsPresent() {
        let cancelButton = app.navigationBars["Welcome"].buttons["Cancel"]
        cancelButton.tap()
        
        let exploreLabel = app.staticTexts["EXPLORE"]
        let exploreImage = app.images["explore-1"]
        let exploreDescription = app.staticTexts["Explore the world for new travel opportunities"]
        XCTAssertTrue(exploreLabel.exists, "No explore label in MainMenu screen")
        XCTAssertTrue(exploreImage.exists, "No explore image in MainMenu screen")
        XCTAssertTrue(exploreDescription.exists, "No explore description in MainMenu screen")
        
        let planLabel = app.staticTexts["PLAN"]
        let planImage = app.images["plan-1"]
        let planDescription = app.staticTexts["Plan and share your travel itineraries"]
        XCTAssertTrue(planLabel.exists, "No plan label in MainMenu screen")
        XCTAssertTrue(planImage.exists, "No plan image in MainMenu screen")
        XCTAssertTrue(planDescription.exists, "No plan description in MainMenu screen")
        
        let rememberLabel = app.staticTexts["REMEMBER"]
        let rememberImage = app.images["remember"]
        let rememberDescription = app.staticTexts["Capture your memories in the travel gallery"]
        XCTAssertTrue(rememberLabel.exists, "No remember label in MainMenu screen")
        XCTAssertTrue(rememberImage.exists, "No remember image in MainMenu screen")
        XCTAssertTrue(rememberDescription.exists, "No remember description in MainMenu screen")
        
        let signOutButton = app.toolbars["Toolbar"].buttons["Sign out"]
        XCTAssertTrue(rememberDescription.exists, "No Sign out button")
        
        signOutButton.tap()
        
        let signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertTrue(signInWithGoogleButton.exists, "Sign out did not forward to authentication screen")
    }
    
    func testAuthenticationFlow() {
        let signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertTrue(signInWithGoogleButton.exists, "Authentication screen not initially shown")
        
        let cancelButton = app.navigationBars["Welcome"].buttons["Cancel"]
        cancelButton.tap()
        
        let planLabel = app.staticTexts["PLAN"]
        XCTAssertTrue(planLabel.exists, "MainMenu screen not shown after cancelling authentication")
        
        app.toolbars["Toolbar"].buttons["Sign out"].tap()
        
        XCTAssertTrue(signInWithGoogleButton.exists, "Sign out did not forward to authentication screen")
    }    
}
