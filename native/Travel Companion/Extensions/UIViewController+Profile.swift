//
//  UIViewController+Profile.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import Foundation
import UIKit

public extension UIViewController {
    func addUserProfileNavigationItem() {
        var barButtonItems = [UIBarButtonItem]()
        
        let userView = createUserProfileView()
        userView.buttonTrailingConstraint.constant = 4
        
        userView.updateUI(user: Auth.auth().currentUser)
        
        let menuBarItem = UIBarButtonItem(customView: userView)

        // constraints needed for UIButtons action in UserView to trigger
        menuBarItem.customView?.heightAnchor.constraint(equalToConstant: userView.frame.height).isActive = true
        menuBarItem.customView?.widthAnchor.constraint(equalToConstant: userView.frame.width).isActive = true
        
        barButtonItems.append(menuBarItem)
        
        navigationItem.rightBarButtonItems = barButtonItems
    }

    func createUserProfileView() -> UserProfileView {
        let userView = UserProfileView.loadXib() {
            $0.buttonAction = { [weak self] in
                self?.present(
                    UINavigationController(rootViewController: SettingsViewController.create(
                        showDismissButton: true
                    )),
                    animated: true,
                    completion: nil
                )
            }
        }

        userView.updateUI(user: Auth.auth().currentUser)

        return userView
    }
}
