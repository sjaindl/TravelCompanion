//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public protocol FormNavigationDelegate: AnyObject {
    func didTapUrl(_ url: URL)
}

public protocol FormController {}

extension FormController {
    public func registerFormCells(tableView: UITableView) {
        tableView.register(xibLoadable: TextInputCell.self, bundle: .main)
        tableView.register(xibLoadable: ButtonTableViewCell.self, bundle: .main)
        tableView.register(xibLoadable: MessageCell.self, bundle: .main)
        tableView.register(xibLoadable: RightDetailSubtitleCell.self, bundle: .main)
        tableView.register(xibLoadable: RightDetailCell.self, bundle: .main)
        tableView.register(xibLoadable: BasicCell.self, bundle: .main)
        tableView.register(xibLoadable: LoadingSpinnerTableViewCell.self, bundle: .main)
        tableView.register(xibLoadable: SeparatorCell.self, bundle: .main)
    }

    public func cellForRow(row: FormRow, tableView: UITableView, indexPath: IndexPath) -> UITableViewCell? {
        if let row = row as? Form.InputRow {
            return textInputCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.LoadingButton {
            return buttonCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.MultiLineTextRow {
            return multiLineCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.MessageRow {
            return messageCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.RightDetailSubtitleRow {
            return rightDetailSubtitleCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.RightDetailRow {
            return rightDetailCell(row: row, tableView: tableView, indexPath: indexPath)
        } else if let row = row as? Form.BasicRow {
            return basicRow(row: row, tableView: tableView, indexPath: indexPath)
        } else if row is Form.LoadingSpinnerRow {
            let cell: LoadingSpinnerTableViewCell? = tableView.dequeueCell(for: indexPath)
            cell?.activityIndicatorView.startAnimating()
            return cell
        } else if let row = row as? Form.SeparatorRow {
            return separatorCell(row: row, tableView: tableView, indexPath: indexPath)
        } else {
            fatalError("Row type `\(row.self)` not handled")
        }
    }
    
    private func textInputCell(row: Form.InputRow, tableView: UITableView, indexPath: IndexPath) -> TextInputCell? {
        guard let cell: TextInputCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        cell.errorText = row.errorText
        cell.iconContainerView.isHidden = true
        // cell.delegate = self
        cell.configuration = row.configuration
        cell.missing = row.missing
        row.configure(cell.textField, cell: cell)
        cell.errorsEnabled = row.errorsEnabled
        cell.isRequired = row.required
        
        return cell
    }

    private func buttonCell(row: Form.LoadingButton, tableView: UITableView, indexPath: IndexPath) -> ButtonTableViewCell? {
        guard let cell: ButtonTableViewCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        cell.buttonText = row.name
        cell.isLoading = row.loading
        cell.style = row.style
        return cell
    }
    
    private func multiLineCell(row: Form.MultiLineTextRow, tableView: UITableView, indexPath: IndexPath) -> MultiLineCell? {
        guard let cell: MultiLineCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        row.configure(cell.multiLineLabel, placeholderLabel: cell.placeholderLabel, cell: cell)
        return cell
    }

    private func messageCell(row: Form.MessageRow, tableView: UITableView, indexPath: IndexPath) -> MessageCell? {
        guard let cell: MessageCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        cell.message = row.message
        cell.messageColor = row.messageColor
        cell.backgroundColor = row.backgroundColor
        cell.errorsEnabled = row.errorsEnabled
//        if let cornerRadius = row.cornerRadius {
//            cell.round(corners: .allCorners, radius: cornerRadius)
//        }
        return cell
    }

    private func rightDetailSubtitleCell(
        row: Form.RightDetailSubtitleRow,
        tableView: UITableView,
        indexPath: IndexPath
    ) -> RightDetailSubtitleCell? {
        guard let cell: RightDetailSubtitleCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        row.configure(titleLabel: cell.titleLabel, subtitleLabel: cell.subtitleLabel, rightDetailLabel: cell.rightDetailLabel)
        return cell
    }

    private func rightDetailCell(row: Form.RightDetailRow, tableView: UITableView, indexPath: IndexPath) -> RightDetailCell? {
        guard let cell: RightDetailCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        row.configure(titleLabel: cell.titleLabel, detailLabel: cell.detailLabel)
        return cell
    }

    private func separatorCell(row: Form.SeparatorRow, tableView: UITableView, indexPath: IndexPath) -> SeparatorCell? {
        guard let cell: SeparatorCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        return cell
    }

    private func basicRow(row: Form.BasicRow, tableView: UITableView, indexPath: IndexPath) -> BasicCell? {
        guard let cell: BasicCell = tableView.dequeueCell(for: indexPath) else {
            return nil
        }
        row.configure(cell: cell)
        return cell
    }
}
