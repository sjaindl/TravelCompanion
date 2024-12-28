//
//  PersonalInformationViewModel.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import Foundation
import UIKit

public protocol PersonalInformationDelegate: AnyObject {
    func didUpdate(state: PersonalInformationViewModel.State)
    func didFail(error: Error)
    func reloadData()
    func didSaveData()
}

public final class PersonalInformationViewModel {
    // MARK: Lifecycle

    public init() {
        formMapping = PersonalInformationViewModel.formMapping(for: state)
    }

    // MARK: Internal

    enum Section {
        case personalInformation

        // MARK: Internal

        var rows: [Row] {
            switch self {
            case .personalInformation:
                return [.email, .fullName]
            }
        }
    }

    enum Row: Int, CaseIterable {
        case email, fullName
    }

    private(set) var formMapping: FormWithMapping

    weak var delegate: PersonalInformationDelegate? {
        didSet {
            delegate?.didUpdate(state: state)
        }
    }

    private(set) var state = State() {
        didSet {
            formMapping = PersonalInformationViewModel.formMapping(for: state)
            delegate?.didUpdate(state: state)
        }
    }

    func reloadLocalData() {
        if let mainUser = Auth.auth().currentUser {
            state.updateWithUser(mainUser, loading: false)
            delegate?.reloadData()
        }
    }

    func loadCustomer() {
        state.isLoading = true
        reloadForm()
    }

    // MARK: Private

    private func reloadForm() {
        formMapping = PersonalInformationViewModel.formMapping(for: state)
        delegate?.reloadData()
    }
}

public extension PersonalInformationViewModel {
    struct State {
        fileprivate(set) var isLoading = false
        fileprivate(set) var isSaving = false

        private(set) var emailAddress: String?
        fileprivate(set) var fullName: String?

        private(set) var initialUser: User?

        var isValid: Bool {
            emailAddress?.isValidEmail ?? false &&
                fullName?.isValidName ?? false
        }

        var customerChanged: Bool {
            return emailAddress != initialUser?.email ||
                fullName != initialUser?.displayName
        }

        mutating func updateWithUser(_ user: User, loading: Bool) {
            emailAddress = user.email
            fullName = user.displayName
            
            initialUser = user

            isLoading = loading
        }
    }
}

extension PersonalInformationViewModel {
    struct FormWithMapping {
        // MARK: Lifecycle

        init(state: PersonalInformationViewModel.State) {
            var formSections = [Form.Section]()
            var sections = [PersonalInformationViewModel.Section]()

            let personalInformation = Form.Section(
                header: "personalInformation".localized(),
                footer: nil,
                rows: rows(for: state, rows: Section.personalInformation.rows)
            )
            formSections.append(personalInformation)
            sections.append(.personalInformation)

            form = Form(sections: formSections)
            self.sections = sections
        }

        // MARK: Internal

        var form = Form(sections: [])
        var sections = [PersonalInformationViewModel.Section]()

        func index(for section: PersonalInformationViewModel.Section) -> Int? {
            sections.firstIndex(of: section)
        }
    }

    private static func formMapping(for state: PersonalInformationViewModel.State) -> FormWithMapping {
        FormWithMapping(state: state)
    }

    private static func rows(for state: PersonalInformationViewModel.State, rows: [Row]) -> [FormRow] {
        rows.map {
            switch $0 {
            case .email:
                return Form.InputRow(
                    text: state.emailAddress,
                    configuration: .icon(UIImage(named: "icon-form-mail-blank")),
                    placeHolder: "email".localized(),
                    returnKeyType: .next,
                    readOnly: true
                )
            case .fullName:
                return Form.InputRow(
                    text: state.fullName,
                    configuration: .icon(UIImage(named: "icon-form-traveller-blank")),
                    placeHolder: "name".localized(),
                    returnKeyType: .done,
                    readOnly: true
                )
            }
        }
    }
}
