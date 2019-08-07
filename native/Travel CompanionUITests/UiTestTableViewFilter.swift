//
//  UiTestTableViewFilter.swift
//  Travel CompanionUITests
//
//  Created by Stefan Jaindl on 04.11.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation
import XCTest

extension XCUIApplication {
    
    func filterCells(containing labels: [String]) -> XCUIElementQuery {
        var cells = self.cells
        
        for label in labels {
            cells = cells.containing(NSPredicate(format: "label CONTAINS %@", label))
        }
        return cells
    }
    
    func cell(containing labels: String...) -> XCUIElement {
        return filterCells(containing: labels).element
    }
    
    func cell(containing labels: [String]) -> XCUIElement {
        return filterCells(containing: labels).element
    }
    
    func tapCell(containing labels: String...) {
        cell(containing: labels).tap()
    }
    
    func swipeCellLeft(containing labels: String...) {
        cell(containing: labels).swipeLeft()
    }
    
    func swipeCellRight(containing labels: String...) {
        cell(containing: labels).swipeRight()
    }
    
    func existsCell(containing labels: String...) -> Bool {
        return cell(containing: labels).exists
    }
}
