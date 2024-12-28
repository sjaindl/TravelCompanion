//
//  ProfileViewModel.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import FirebaseStorage
import Foundation
import UIKit

public protocol ProfileViewModelDelegate: AnyObject {
    func didUpdate(state: ProfileViewModel.State)
}

public final class ProfileViewModel {
    // MARK: Lifecycle

    public init() {
        state = State(
            user: Auth.auth().currentUser
        )
    }

    // MARK: Internal

    private(set) var state: State {
        didSet {
            delegate?.didUpdate(state: state)
        }
    }

    weak var delegate: ProfileViewModelDelegate? {
        didSet {
            delegate?.didUpdate(state: state)
        }
    }

    func logout(controller: UIViewController) {
        do {
            try Auth.auth().signOut()
        } catch {
            UiUtils.showError("signOutError".localized(), controller: controller)
        }
    }

    func item(for section: Int) -> Item? {
        guard state.items.indices.contains(section) else {
            return nil
        }
        return state.items[section]
    }
    
    func deletePhotos(controller: UIViewController) {
        let userRef = FirestoreClient.userReference()
        let plans = userRef.collection(FirestoreConstants.Collections.plans)
        
        plans.getDocuments() { query, error in
            query?.documents.forEach { planDocument in
                let photoCollection = planDocument.reference.collection(FirestoreConstants.Collections.photos)
                
                photoCollection.getDocuments() { photoQuery, photoError in
                    photoQuery?.documents.forEach { photoDoc in
                        if let fileName = photoDoc.get("path") as? String {
                            let storageImageRef = Storage.storage().reference(forURL: fileName)
                            
                            storageImageRef.delete() { error in
                                if let error = error {
                                    UiUtils.showError(error.localizedDescription, controller: controller)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

public extension ProfileViewModel {
    enum RowType {
        case personalInformations
        case requestAccountDeletion
    }

    enum Item: Equatable {
        case section(User?, [Item])
        case item(rowType: RowType, title: String, subTitle: String?, image: UIImage?)
    }

    struct State {
        // MARK: Lifecycle

        init(user: User?) {
            guard let user = user else {
                return
            }

            self.user = user

            items = [
                .section(user, [
                    .item(
                        rowType: .personalInformations,
                        title: "personalInformation".localized(),
                        subTitle: "nameAddressBirthday".localized(),
                        image: UIImage(named: "personal-info")
                    ),
                    .item(
                        rowType: .requestAccountDeletion,
                        title: "requestAccountDeletion".localized(),
                        subTitle: nil,
                        image: UIImage(named: "userDeleteIcon")
                    )
                ]),
            ]
        }

        // MARK: Internal

        private(set) var user: User?
        private(set) var items: [Item] = []
    }
}
