//
//  Travel_CompanionUITests.swift
//  Travel CompanionUITests
//
//  Created by Stefan Jaindl on 01.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import XCTest

class MainMenuUiTests: XCTestCase {
    
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
        
        let signOut = app.buttons["Sign out"]
        if signOut.exists {
            signOut.tap()
        }
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testAuthenticationScreen() {
        let signInButton = app.toolbars["Toolbar"].buttons["Sign in"]
        XCTAssertTrue(signInButton.exists, "No sign-in button in main screen, but should be")
        signInButton/*@START_MENU_TOKEN@*/.tap()/*[[".tap()",".press(forDuration: 0.5);"],[[[-1,1],[-1,0]]],[1]]@END_MENU_TOKEN@*/
        
        let cancelButton = app.navigationBars["Welcome"].buttons["Cancel"]
        let signInEmailButton = app/*@START_MENU_TOKEN@*/.buttons["Sign in with email"]/*[[".buttons[\"Sign in with email\"]",".buttons[\"EmailButtonAccessibilityID\"]"],[[[-1,1],[-1,0]]],[1]]@END_MENU_TOKEN@*/
        let signInWithFacebookButton = app.buttons["Sign in with Facebook"]
        let signInWithGoogleButton = app.buttons["Sign in with Google"]
        let signInWithAppleButton = app.buttons["Sign in with Apple"]
        XCTAssertTrue(cancelButton.exists, "No cancel button in authentication screen, but should be")
        XCTAssertTrue(signInEmailButton.exists, "No sign in with email button in authentication screen")
        XCTAssertTrue(signInWithFacebookButton.exists, "No sign in with Facebook button in authentication screen")
        XCTAssertTrue(signInWithGoogleButton.exists, "No sign in with Google button in authentication screen")
        XCTAssertTrue(signInWithAppleButton.exists, "No sign in with Apple button in authentication screen")
    }
    
    func testMainMenuElementsPresent() {
        let exploreLabel = app.buttons["Explore"]
        let exploreImage = app.images["explore"]
        let exploreDescription = app.buttons["Explore the world for new travel opportunities"]
        XCTAssertTrue(exploreLabel.exists, "No explore label in MainMenu screen")
        XCTAssertTrue(exploreImage.exists, "No explore image in MainMenu screen")
        XCTAssertTrue(exploreDescription.exists, "No explore description in MainMenu screen")
        
        let planLabel = app.buttons["Plan"]
        let planImage = app.images["plan"]
        let planDescription = app.buttons["Plan your travel itineraries"]
        XCTAssertTrue(planLabel.exists, "No plan label in MainMenu screen")
        XCTAssertTrue(planImage.exists, "No plan image in MainMenu screen")
        XCTAssertTrue(planDescription.exists, "No plan description in MainMenu screen")
        
        let rememberLabel = app.buttons["Remember"]
        let rememberImage = app.images["remember"]
        let rememberDescription = app.buttons["Capture your memories in the travel gallery"]
        XCTAssertTrue(rememberLabel.exists, "No remember label in MainMenu screen")
        XCTAssertTrue(rememberImage.exists, "No remember image in MainMenu screen")
        XCTAssertTrue(rememberDescription.exists, "No remember description in MainMenu screen")
        
        let signInButton = app.toolbars["Toolbar"].buttons["Sign in"]
        XCTAssertTrue(signInButton.exists, "No Sign in button")
        
        signInButton.tap()
        
        let signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertTrue(signInWithGoogleButton.exists, "Sign out did not forward to authentication screen")
    }
    
    func testLoginRequired() {
        app.images["plan"].tap()
        
        var signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertTrue(signInWithGoogleButton.exists, "Plan did not forward to authentication screen")
        app.navigationBars["Welcome"].buttons["Cancel"].tap()
           
        app.images["remember"].tap()
        signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertTrue(signInWithGoogleButton.exists, "Remember did not forward to authentication screen")
        app.navigationBars["Welcome"].buttons["Cancel"].tap()
        
        app.images["explore"].tap()
        signInWithGoogleButton = app.buttons["Sign in with Google"]
        XCTAssertFalse(signInWithGoogleButton.exists, "Explore feature shouldn't require authentication.")
    }
}
