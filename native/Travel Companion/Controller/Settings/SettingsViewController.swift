//
//  SettingsViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import FirebaseAuthUI
import UIKit

public final class SettingsViewController: UITableViewController {
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!
    
    // MARK: Public

    public static func create(showDismissButton: Bool = false) -> SettingsViewController {
        let vc = UIStoryboard(name: "Settings", bundle: nil).instantiateViewController(withIdentifier: "SettingsViewController") as! SettingsViewController
        vc.showDismissButton = showDismissButton
        return vc
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        setupUI()
        viewModel.delegate = self
        handleAuthStateChanges()
    }

    override public func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateUI()
    }
    
    @objc
    private func didTapDone() {
        Auth.auth().removeStateDidChangeListener(_authHandle)
        dismiss(animated: true, completion: nil)
    }

    // TableView

    override public func numberOfSections(in _: UITableView) -> Int {
        viewModel.state.sections.count
    }

    override public func tableView(_: UITableView, titleForHeaderInSection section: Int) -> String? {
        guard let section = viewModel.section(for: section) else {
            return nil
        }
        return section.title
    }

    override public func tableView(_: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        viewModel.section(for: section) != nil ? 40.0 : 0.0
    }

    override public func tableView(_: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        var height: CGFloat = 0.0
        if viewModel.isSectionLoading(for: section) {
            height += 54.0
        }

        if viewModel.showPoweredByFooter(for: section) {
            height += 80.0
        }

        return height
    }

    override public func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let stackView = UIStackView()
        stackView.alignment = .center
        stackView.axis = .vertical
        stackView.distribution = .fill
        stackView.spacing = 8.0

        if viewModel.isSectionLoading(for: section) {
            let activityIndicator = UIActivityIndicatorView(style: .medium)
            activityIndicator.startAnimating()
            activityIndicator.frame = CGRect(x: 0.0, y: 0.0, width: tableView.bounds.width, height: 44.0)

            stackView.addArrangedSubview(activityIndicator)
        }

        guard !stackView.arrangedSubviews.isEmpty else {
            return nil
        }

        let spacer = UIView()
        spacer.backgroundColor = .clear
        spacer.translatesAutoresizingMaskIntoConstraints = false
        spacer.heightAnchor.constraint(equalToConstant: 20).isActive = true

        stackView.insertArrangedSubview(spacer, at: 0)
        return stackView
    }

    override public func tableView(_: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel.section(for: section)?.items.count ?? 0
    }

    override public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = assertNotNil(
            viewModel.item(at: indexPath),
            errorMessage: "No item given at indexPath `\(indexPath)`",
            tag: SettingsViewController.tag
        )
        var cell: UITableViewCell?

        switch item {
        case let .profile(user):
            let profileCell: ProfileTableViewCell? = tableView.dequeueCell(for: indexPath)
            profileCell?.user = user
            cell = profileCell

        case let .item(title, image, _):
            let itemCell = tableView.dequeueReusableCell(withIdentifier: itemReuseIdentifier, for: indexPath)
            itemCell.textLabel?.text = title
            itemCell.imageView?.image = image?.withTintColor(UIColor.appTextColorDefault())
            itemCell.accessoryType = .disclosureIndicator
            cell = itemCell
        }

        return assertNotNil(
            cell,
            errorMessage: "No cell given for item `\(itemReuseIdentifier)` at indexPath `\(indexPath)`",
            tag: SettingsViewController.tag
        )
    }

    override public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)

        guard let item = viewModel.item(at: indexPath) else {
            return
        }

        switch item {
        case .profile:
            if Auth.auth().currentUser != nil {
                show(ProfileViewController.create(), sender: nil)
            } else {
                loginSession()
            }

        case let .item(_, _, navigation):
            switch navigation {
            case .notifications, .permission:
                UIApplication.shared.openSystemSettings()
            }
        }
    }
    
    func loginSession() {
        let authViewController = FUIAuth.defaultAuthUI()!.authViewController()
        present(authViewController, animated: true, completion: nil)
    }
    
    func handleAuthStateChanges() {
        // listen for changes in the authorization state
        _authHandle = Auth.auth().addStateDidChangeListener { (auth: Auth, user: User?) in
            self.viewModel.update(user: user)
            self.tableView.reloadData()
        }
    }

    // MARK: Private

    private static let tag = "SettingsViewController"

    private let itemReuseIdentifier = "settingsItemReuseIdentifier"

    private let viewModel = SettingsViewModel()

    private var showDismissButton = false
}

private extension SettingsViewController {
    func setupUI() {
        title = "profile".localized()

        navigationController?.view.backgroundColor = UIColor.systemGroupedBackground

        if showDismissButton {
            let item = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(didTapDone))
            item.tintColor = UIColor.appTextColorDefault()
            navigationItem.rightBarButtonItem = item
        }

        tableView.register(xibLoadable: ProfileTableViewCell.self, bundle: .main)
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 80.0
    }

    func updateUI() {
        tableView.reloadData()
    }
}

extension SettingsViewController: SettingsViewModelDelegate {
    public func didUpdate(state _: SettingsViewModel.State) {
        guard viewIfLoaded != nil else {
            return
        }
        updateUI()
    }
}
