//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public protocol FormRow {
    var shouldHighlight: Bool { get }
    var requiresValidState: Bool { get }
    var required: Bool { get }
    var errorsEnabled: Bool { get set }
}

public extension FormRow {
    var shouldHighlight: Bool { false }
    var requiresValidState: Bool { false }
}

public extension FormRow {
    func isValid() -> Bool {
        if let formOfAddressRow = self as? Form.FormOfAddressRow {
            return formOfAddressRow.validator(formOfAddressRow.formOfAddressName)
        } else {
            return true
        }
    }
}

public struct Form {
    // MARK: Lifecycle

    public init(sections: [Section]) {
        self.sections = sections
    }

    // MARK: Public

    public enum RowConfiguration {
        case title(String)
        case icon(UIImage?)
    }

    public struct Section {
        public init(header: String?, footer: String?, rows: [FormRow]) {
            self.header = header
            self.footer = footer
            self.rows = rows
        }
        
        public let header: String?
        public let footer: String?
        public let rows: [FormRow]
    }
    
    public struct InputRow: FormRow {
        // MARK: Lifecycle

        public init(
            text: String? = nil,
            configuration: RowConfiguration,
            placeHolder: String? = nil,
            keyboardType: UIKeyboardType = .default,
            returnKeyType: UIReturnKeyType = .default,
            isSecureTextEntry: Bool = false,
            required: Bool = false,
            readOnly: Bool = false,
            missing: Bool = false,
            errorsEnabled: Bool = false,
            errorText: String? = nil
        ) {
            self.text = text
            self.configuration = configuration
            self.placeHolder = placeHolder
            self.keyboardType = keyboardType
            self.returnKeyType = returnKeyType
            self.isSecureTextEntry = isSecureTextEntry
            self.required = required
            self.readOnly = readOnly
            self.missing = missing
            self.errorsEnabled = errorsEnabled
            self.errorText = errorText ?? "" // L10n.pleaseFillInThisField
        }

        // MARK: Public

        public let text: String?
        public let configuration: RowConfiguration
        public let placeHolder: String?
        public let keyboardType: UIKeyboardType
        public let returnKeyType: UIReturnKeyType
        public let isSecureTextEntry: Bool
        public let required: Bool
        public let readOnly: Bool
        public let missing: Bool
        public var errorsEnabled: Bool
        public let errorText: String
    }

    public struct FormOfAddressRow: FormRow {
        // MARK: Lifecycle

        public init(
            configuration: RowConfiguration,
            name: String,
            formOfAddressName: String? = nil,
            formOfAddressNames: [String],
            required: Bool = false,
            validator: @escaping (String?) -> Bool = { _ in true },
            errorsEnabled: Bool = false
        ) {
            self.configuration = configuration
            self.name = name
            self.formOfAddressName = formOfAddressName
            self.formOfAddressNames = formOfAddressNames
            self.required = required
            self.validator = validator
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let configuration: RowConfiguration
        public let name: String
        public let formOfAddressName: String?
        public let formOfAddressNames: [String]
        public let required: Bool
        public let validator: (String?) -> Bool
        public var errorsEnabled: Bool
    }

    public struct MessageRow: FormRow {
        // MARK: Lifecycle

        public init(
            message: String,
            messageColor: UIColor,
            backgroundColor: UIColor,
            cornerRadius: CGFloat? = nil,
            required: Bool = false,
            errorsEnabled: Bool = false
        ) {
            self.message = message
            self.messageColor = messageColor
            self.backgroundColor = backgroundColor
            self.cornerRadius = cornerRadius
            self.required = required
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let message: String
        public let messageColor: UIColor
        public let backgroundColor: UIColor
        public let cornerRadius: CGFloat?
        public let required: Bool
        public var errorsEnabled: Bool
    }

    public struct MultiLineTextRow: FormRow {
        // MARK: Lifecycle

        public init(
            text: String?,
            placeholder: String?,
            accessory: UITableViewCell.AccessoryType,
            required: Bool = false,
            errorsEnabled: Bool = false
        ) {
            self.text = text
            self.placeholder = placeholder
            self.accessory = accessory
            self.required = required
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let text: String?
        public let placeholder: String?
        public let accessory: UITableViewCell.AccessoryType
        public let shouldHighlight = true
        public let required: Bool
        public var errorsEnabled: Bool
    }

    public struct RightDetailSubtitleRow: FormRow {
        // MARK: Lifecycle

        public init(
            title: String?,
            titleFont: UIFont? = nil,
            subtitle: String?,
            subtitleFont: UIFont? = nil,
            rightDetail: String?,
            rightDetailFont: UIFont? = nil,
            required: Bool = false,
            shouldHighlight: Bool = false,
            errorsEnabled: Bool = false
        ) {
            self.title = title
            self.titleFont = titleFont
            self.subtitle = subtitle
            self.subtitleFont = subtitleFont
            self.rightDetail = rightDetail
            self.rightDetailFont = rightDetailFont
            self.shouldHighlight = shouldHighlight
            self.required = required
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let title: String?
        public let titleFont: UIFont?
        public let subtitle: String?
        public let subtitleFont: UIFont?
        public let rightDetail: String?
        public let rightDetailFont: UIFont?
        public let shouldHighlight: Bool
        public let required: Bool
        public var errorsEnabled: Bool
    }

    public struct ButtonRow: FormRow {
        // MARK: Lifecycle

        public init(
            title: String?,
            required: Bool = false,
            image: UIImage? = nil,
            textColor: UIColor? = nil,
            disabledTextColor: UIColor? = nil,
            font: UIFont? = nil,
            validationDependent: Bool = false,
            errorsEnabled: Bool = false
        ) {
            self.title = title
            self.image = image
            self.textColor = textColor
            self.disabledTextColor = disabledTextColor
            self.font = font
            requiresValidState = validationDependent
            self.required = required
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let title: String?
        public let image: UIImage?
        public let textColor: UIColor?
        public let disabledTextColor: UIColor?
        public let font: UIFont?
        public let requiresValidState: Bool
        public let required: Bool
        public var errorsEnabled: Bool
    }

    public struct RightDetailRow: FormRow {
        // MARK: Lifecycle

        public init(
            title: String?,
            detail: String?,
            required: Bool = false,
            titleFont: UIFont? = nil,
            detailFont: UIFont? = nil,
            errorsEnabled: Bool = false
        ) {
            self.title = title
            self.detail = detail
            self.titleFont = titleFont
            self.detailFont = detailFont
            self.required = required
            self.errorsEnabled = errorsEnabled
        }

        // MARK: Public

        public let title: String?
        public let detail: String?
        public let titleFont: UIFont?
        public let detailFont: UIFont?
        public let required: Bool
        public var errorsEnabled: Bool
    }

    public struct BasicRow: FormRow {
        // MARK: Lifecycle

        public init(
            image: UIImage? = nil,
            text: String? = nil,
            tintColor: UIColor? = nil,
            font: UIFont? = nil,
            textAlignment: NSTextAlignment = .natural,
            separatorInset: UIEdgeInsets? = nil,
            accessoryType: UITableViewCell.AccessoryType = .none,
            required: Bool = false
        ) {
            self.image = image
            self.text = text
            self.tintColor = tintColor
            self.font = font
            self.textAlignment = textAlignment
            self.separatorInset = separatorInset
            self.accessoryType = accessoryType
            self.required = required
        }

        // MARK: Public

        public let image: UIImage?
        public let text: String?
        public let tintColor: UIColor?
        public let font: UIFont?
        public let separatorInset: UIEdgeInsets?
        public let shouldHighlight = true
        public let textAlignment: NSTextAlignment
        public let accessoryType: UITableViewCell.AccessoryType
        public let required: Bool
        public var errorsEnabled = false
    }

    public struct LoadingSpinnerRow: FormRow {
        public init(errorsEnabled: Bool = false) {
            self.errorsEnabled = errorsEnabled
        }
        
        public let required = false
        public var errorsEnabled = false
    }

    public struct SeparatorRow: FormRow {
        public init(required: Bool = false, errorsEnabled: Bool = false) {
            self.required = required
            self.errorsEnabled = errorsEnabled
        }
        
        public var required = false
        public var errorsEnabled = false
    }
    
    public let sections: [Section]
}

public extension Form.InputRow {
    func configure(_ inputField: FormTextField, cell _: FormCell) {
        inputField.text = text

        inputField.placeholder = placeHolder?.addAsterisk(required: required)
        inputField.keyboardType = keyboardType
        inputField.isSecureTextEntry = isSecureTextEntry
        inputField.returnKeyType = returnKeyType
        inputField.isEnabled = !readOnly
    }
}

public extension Form.MultiLineTextRow {
    func configure(_ label: UILabel, placeholderLabel: UILabel, cell: UITableViewCell) {
        label.text = text
        placeholderLabel.text = placeholder
        placeholderLabel.isHidden = !(text?.isEmpty ?? true)
        cell.accessoryType = accessory
    }
}

public extension Form.RightDetailSubtitleRow {
    func configure(titleLabel: UILabel, subtitleLabel: UILabel, rightDetailLabel: UILabel) {
        titleLabel.text = title
        titleFont.map { titleLabel.font = $0 }
        subtitleLabel.text = subtitle
        subtitleFont.map { subtitleLabel.font = $0 }
        rightDetailLabel.text = rightDetail
        rightDetailFont.map { rightDetailLabel.font = $0 }
    }
}

public extension Form.RightDetailRow {
    func configure(titleLabel: UILabel, detailLabel: UILabel) {
        titleLabel.text = title
        titleFont.map { titleLabel.font = $0 }
        detailLabel.text = detail
        detailFont.map { detailLabel.font = $0 }
    }
}

public extension Form.BasicRow {
    func configure(cell: BasicCell) {
        cell.iconImageView.image = image
        if image == nil {
            cell.imageWidthConstraint.constant = 0
            cell.imageLabelMarginConstraint.constant = 0
        } else {
            cell.imageWidthConstraint.constant = 44
            cell.imageLabelMarginConstraint.constant = 16
        }
        cell.separatorInset = UIEdgeInsets(
            top: 0,
            left: cell.layoutMargins.left + cell.imageWidthConstraint.constant + cell.imageLabelMarginConstraint.constant,
            bottom: 0,
            right: 0
        )
        cell.titleLabel.text = text
        cell.titleLabel.textAlignment = textAlignment
        font.map { cell.titleLabel.font = $0 }
        tintColor.map {
            cell.titleLabel.textColor = $0
            cell.iconImageView.tintColor = $0
        }
        cell.accessoryType = accessoryType
        cell.layoutIfNeeded()
    }
}

public extension Form.ButtonRow {
    func configure(_ button: UIButton) {
        button.setTitle(title, for: .normal)
        button.setImage(image, for: .normal)
        textColor.map { button.setTitleColor($0, for: .normal) }
        disabledTextColor.map { button.setTitleColor($0, for: .disabled) }
        font.map { button.titleLabel?.font = $0 }

        if title != nil, image != nil {
            button.titleEdgeInsets.left = 6
            button.imageEdgeInsets.right = 6
        }
    }
}

public extension String {
    func addAsterisk(required: Bool) -> String {
        required ? self + "*" : self
    }
}
