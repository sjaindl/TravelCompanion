//
//  SettingsViewModel.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import Foundation
import UIKit

public protocol SettingsViewModelDelegate: AnyObject {
    func didUpdate(state: SettingsViewModel.State)
}

public final class SettingsViewModel {
    // MARK: Lifecycle

    public init() {
        update(user: Auth.auth().currentUser)
    }

    // MARK: Internal

    private(set) var state = State() {
        didSet {
            delegate?.didUpdate(state: state)
        }
    }

    weak var delegate: SettingsViewModelDelegate? {
        didSet {
            delegate?.didUpdate(state: state)
        }
    }

    func section(for section: Int) -> Section? {
        state.sections[section]
    }

    func item(at indexPath: IndexPath) -> Item? {
        guard let section = section(for: indexPath.section) else {
            return nil
        }
        return section.items[indexPath.row]
    }

    func isSectionLoading(for sectionIndex: Int) -> Bool {
        state.isLoading
    }

    func showPoweredByFooter(for sectionIndex: Int) -> Bool {
        state.sections.count - 1 == sectionIndex
    }
    
    func update(user: User?) {
        state.update(user: Auth.auth().currentUser)
    }
}

public extension SettingsViewModel {
    enum Item: Equatable {
        case profile(User?)
        case item(String, UIImage?, Navigation)
    }

    struct Section: Equatable {
        let title: String?
        let items: [Item]
    }

    enum Navigation {
        case permission, notifications
    }

    struct State: Equatable {
        // MARK: Internal

        private(set) var user: User?
        fileprivate(set) var isLoading = false

        private(set) var sections = [Section]()

        mutating func update(user: User?) {
            self.user = user
            updateSections()
        }

        mutating func update() {
            isLoading = false
            updateSections()
        }
        
        // MARK: Fileprivate

        fileprivate mutating func updateSections() {
            var sections = [Section]()
            sections.append(
                Section(title: nil, items: [
                    .profile(user)
                ])
            )

            sections.append(
                Section(title: "settings".localized(), items: [
                    .item(
                        "permissions".localized(),
                        UIImage(named: "permissions")?.withTintColor(UIColor.appTextColorDefault()),
                        .permission
                    ),
                    .item(
                        "notifications".localized(),
                        UIImage(named: "notifications")?.withTintColor(UIColor.appTextColorDefault()),
                        .notifications
                    )
                ])
            )

            self.sections = sections
        }
    }
}
