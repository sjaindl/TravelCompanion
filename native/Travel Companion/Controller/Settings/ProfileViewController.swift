//
//  ProfileViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import FirebaseStorage
import UIKit

public final class ProfileViewController: UITableViewController, StretchingTableViewFooterProvider {
    // MARK: Public

    public static func create() -> ProfileViewController {
        let storyboard = UIStoryboard(name: "Profile", bundle: nil)
        return storyboard.instantiateViewController(withIdentifier: Constants.ControllerIds.profileViewController) as! ProfileViewController
    }

    public var footerView: UIView {
        tableViewFooterView
    }

    public var tableViewForFooter: UITableView {
        tableView
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        setupUI()
        localizeUI()
    }

    override public func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateFooterViewHeight()
    }

    // MARK: - UITableViewDelegate & Datasource

    override public func numberOfSections(in _: UITableView) -> Int {
        viewModel.state.items.count
    }

    override public func tableView(_: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let item = viewModel.item(for: section) else {
            return 0
        }

        switch item {
        case let .section(_, items):
            return items.count
        default:
            return 1
        }
    }

    override public func tableView(_: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        guard let item = viewModel.item(for: section) else {
            return 0.0
        }

        switch item {
        case let .section(user, _):
            return (user != nil) ? 240.0 : 0.0
        default:
            return 0
        }
    }

    override public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = assertNotNil(
            viewModel.item(for: indexPath.section),
            errorMessage: "No item given at indexPath `\(indexPath)`",
            tag: ProfileViewController.tag
        )
        var cell: UITableViewCell?

        switch item {
        case .item:
            break

        case let .section(_, items):
            guard items.indices.contains(indexPath.row) else {
                break
            }
            let sectionItem = items[indexPath.row]

            var itemCell: UITableViewCell?
            if case let .item(type, title, subTitle, image) = sectionItem {
                switch type {
                case .personalInformations:
                    itemCell = tableView.dequeueReusableCell(withIdentifier: itemWithSubtitleReuseIdentifier, for: indexPath)

                default:
                    itemCell = tableView.dequeueReusableCell(withIdentifier: itemReuseIdentifier, for: indexPath)
                }

                itemCell?.textLabel?.text = title
                itemCell?.textLabel?.textColor = UIColor.appTextColorDefault()
                itemCell?.textLabel?.numberOfLines = 0
                itemCell?.detailTextLabel?.text = subTitle
                itemCell?.detailTextLabel?.textColor = UIColor.gray
                itemCell?.detailTextLabel?.numberOfLines = 0
                    
                itemCell?.imageView?.image = image?.withTintColor(UIColor.appTextColorDefault())
            }
            cell = itemCell
        }

        return assertNotNil(
            cell,
            errorMessage: "No cell given for item `\(item)` at indexPath `\(indexPath)`",
            tag: ProfileViewController.tag
        )
    }

    override public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        guard let item = viewModel.item(for: indexPath.section) else {
            return
        }

        switch item {
        case let .section(_, items):
            guard case let .item(rowType, _, _, _) = items[indexPath.row] else {
                return
            }
            
            switch rowType {
            case .personalInformations:
                navigationController?.pushViewController(PersonalInformationViewController.create(), animated: true)

            case .requestAccountDeletion:
                presentDeleteAccountAlert()
            }

        default:
            break
        }
    }

    override public func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        guard case let .section(user, _)? = viewModel.item(for: section) else {
            return nil
        }

        let cell = UserHeaderTableViewCell.loadXib(bundle: .main)
        cell.frame = CGRect(x: 0.0, y: 0.0, width: tableView.frame.width, height: 240.0)
        cell.backgroundColor = tableView.backgroundColor
        cell.user = user

        return cell
    }

    @objc
    private func didTapLogout() {
        viewModel.logout(controller: self)
        navigationController?.popToRootViewController(animated: true)
    }

    // MARK: Private

    private static let tag = "ProfileViewController"

    private let itemReuseIdentifier = "profileItemReuseIdentifier"
    private let itemWithSubtitleReuseIdentifier = "profileItemWithSubtitleReuseIdentifier"
    private let logoutReuseIdentifier = "profileLogoutReuseIdentifier"
    private let viewModel = ProfileViewModel()

    private var tableViewFooterView = TableViewFooterView(frame: .zero)
}

private extension ProfileViewController {
    func setupUI() {
        title = "myProfile".localized()

        navigationItem.largeTitleDisplayMode = .never
        navigationController?.view.backgroundColor = .systemGroupedBackground
        navigationController?.navigationBar.tintColor = UIColor.appTextColorDefault()

        tableView.register(xibLoadable: ProfileTableViewCell.self, bundle: .main)
        tableView.register(xibLoadable: UserHeaderTableViewCell.self, bundle: .main)
        tableView.rowHeight = UITableView.automaticDimension

        let textView = UITextView(frame: tableView.frame)
        textView.font = UIFont.systemFont(ofSize: UIFont.smallSystemFontSize)
        textView.attributedText = NSAttributedString(string: "not empty")
        let footerStyle = ThemeTextStyle(like: textView, alignment: .center)
        
        let privacyPolicyLink = "https://sjaindl.github.io/TravelCompanion"
        let localizedString = "yourPrivacyMatters".localized()
        let localizedStringWithParam = String(format: localizedString, privacyPolicyLink)
        
        let text = NSAttributedString(
            markdown: localizedStringWithParam,
            textStyle: footerStyle
        )

        tableViewFooterView = TableViewFooterView.Builder()
            .withButtonTitle(title: "logout".localized(), color: UIColor.appTextColorDefault())
            .withText(attributed: text, delegate: nil)
            .build()
        tableViewFooterView.frame = .zero

        tableViewFooterView.button.addTarget(self, action: #selector(didTapLogout), for: .touchUpInside)
    }

    func localizeUI() {}

    func updateUI(state _: ProfileViewModel.State) {
        tableView.reloadData()
    }

    func presentDeleteAccountAlert() {
        let alert = UIAlertController(title: "deleteAccount".localized(), message: "doYouReallyWantToDeleteYourAccount".localized(), preferredStyle: .alert)
        let delteAction = UIAlertAction(title: "yesDelete".localized(), style: .destructive) { _ in
            self.deleteAccount()
        }
        let cancelAction = UIAlertAction(title: "cancel".localized(), style: .cancel, handler: nil)
        alert.addAction(delteAction)
        alert.addAction(cancelAction)
        present(alert, animated: true, completion: nil)
    }

    func deleteAccount() {
        guard let user = Auth.auth().currentUser else {
            return
        }
        
        present(
            UIViewController.createLoadingAlert(title: "pleaseWait".localized()),
            animated: true,
            completion: nil
        )
        
        let userRef = FirestoreClient.userReference()
        
        viewModel.deletePhotos(controller: self)
        
        userRef.delete() { error in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            }
            user.delete() { userError in
                if let userError = userError {
                    UiUtils.showError(userError.localizedDescription, controller: self)
                }
                
                DispatchQueue.main.async {
                    let handleResult = {
                        self.presentDeleteAccountInfo()
                    }
                    
                    if let presentedViewController = self.presentedViewController {
                        presentedViewController.dismiss(animated: true, completion: handleResult)
                    } else {
                        handleResult()
                    }
                }
            }
        }
    }

    func presentDeleteAccountInfo() {
        let alert = UIAlertController(title: "accountDeleted".localized(), message: "yourAccountHasBeenDeleted".localized(), preferredStyle: .alert)
        let okAction = UIAlertAction(title: "ok".localized(), style: .default) { _ in
            self.viewModel.logout(controller: self)
            self.dismiss(animated: true)
        }
        alert.addAction(okAction)
        present(alert, animated: true, completion: nil)
    }
}
