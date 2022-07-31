//
//  PersonalInformationViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//
import UIKit

public final class PersonalInformationViewController: UIViewController, FormController {

    // MARK: Internal

    @IBOutlet private var tableView: UITableView!

    public static func create() -> PersonalInformationViewController {
        let storyboard = UIStoryboard(name: "Profile", bundle: nil)
        return storyboard.instantiateViewController(withIdentifier: Constants.ControllerIds.personalInformationViewController) as! PersonalInformationViewController
    }

    override public func viewDidLoad() {
        super.viewDidLoad()

        setupUI()

        viewModel.reloadLocalData()
        viewModel.loadCustomer()
    }

    // MARK: Private

    private static let tag = "PersonalInformationViewController"

    private let activityIndicator = UIActivityIndicatorView(style: .medium)
    private let viewModel = PersonalInformationViewModel()
}

private extension PersonalInformationViewController {
    func setupUI() {
        tableView.delegate = self
        tableView.dataSource = self

        tableView.estimatedRowHeight = 56

        tableView.allowsSelection = true
        tableView.tableFooterView = UIView()

        tableView.delaysContentTouches = false

        registerFormCells(tableView: tableView)

        activityIndicator.hidesWhenStopped = true
        let refreshBarButton = UIBarButtonItem(customView: activityIndicator)
        navigationItem.rightBarButtonItem = refreshBarButton
    }

    func updateUI(state: PersonalInformationViewModel.State) {
        activityIndicator.isLoading = state.isLoading
    }
}

extension PersonalInformationViewController: UITableViewDataSource {
    public func numberOfSections(in _: UITableView) -> Int {
        viewModel.formMapping.form.sections.count
    }

    public func tableView(_: UITableView, titleForHeaderInSection section: Int) -> String? {
        let section = viewModel.formMapping.form.sections[section]

        return section.header
    }

    public func tableView(_: UITableView, numberOfRowsInSection section: Int) -> Int {
        let section = viewModel.formMapping.form.sections[section]

        return section.rows.count
    }

    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let row = assertNotNil(
            viewModel.formMapping.form.sections[indexPath.section].rows[indexPath.row],
            errorMessage: "No row given at indexPath `\(indexPath)`",
            tag: PersonalInformationViewController.tag
        )
        let cell = cellForRow(row: row, tableView: tableView, indexPath: indexPath)

        return assertNotNil(
            cell,
            errorMessage: "No cell given for row `\(row)` at indexPath `\(indexPath)`",
            tag: PersonalInformationViewController.tag
        )
    }
}

extension PersonalInformationViewController: UITableViewDelegate {
    public func tableView(_: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let row = viewModel.formMapping.form.sections[indexPath.section].rows[indexPath.row]

        if row is Form.MultiLineTextRow {
            return UITableView.automaticDimension
        }

        return 56.0
    }

    public func tableView(_: UITableView, shouldHighlightRowAt indexPath: IndexPath) -> Bool {
        let row = viewModel.formMapping.form.sections[indexPath.section].rows[indexPath.row]
        return row.shouldHighlight
    }

    // MARK: - UIScrollViewDelegate

    public func scrollViewWillBeginDragging(_: UIScrollView) {
        view.endEditing(true)
    }
}
