//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public protocol TextInputCellDelegate: AnyObject {
    func textInputCellDidReturn(_ cell: TextInputCell)
    func textInputCellDidChange(_ cell: TextInputCell)
    func textInputCellDidBeginEditing(_ cell: TextInputCell)
    func textInputCellDidEndEditing(_ cell: TextInputCell)
    func textInputCellShouldChangeCharacters(
        _ cell: TextInputCell,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool
}

public extension TextInputCellDelegate {
    func textInputCellDidBeginEditing(_: TextInputCell) {}
    func textInputCellDidEndEditing(_: TextInputCell) {}
}

public final class TextInputCell: FormCell, XibLoadable {
    public static let xibName = "TextInputCell"

    @IBOutlet public var textField: FormTextField!
    @IBOutlet public var textFieldLabel: UILabel!

    public weak var delegate: TextInputCellDelegate?

    public var missing: Bool = false

    public var configuration: Form.RowConfiguration? {
        didSet {
            guard let design = configuration else {
                return
            }
            switch design {
            case let .title(title):
                iconContainerView.isHidden = true
                textFieldLabel.isHidden = false
                titleLabel = title

            case let .icon(image):
                iconContainerView.isHidden = false
                textFieldLabel.isHidden = true
                icon = image?.withTintColor(UIColor.appTextColorDefault())
                titleLabel = nil
            }
        }
    }

    public var placeHolder: String {
        get { textField?.placeholder ?? "" }
        set { textField?.placeholder = newValue }
    }

    public var formText: String {
        get { textField?.text ?? "" }
        set { textField?.text = newValue }
    }

    override public var icon: UIImage? {
        get { iconView?.image }
        set {
            super.icon = newValue?.withTintColor(UIColor.appTextColorDefault())
            iconView.isHidden = icon == nil
            iconView.tintColor = UIColor.appTextColorDefault()
        }
    }

    public private(set) var titleLabel: String? {
        get { textFieldLabel?.text ?? "" }
        set { textFieldLabel?.text = newValue }
    }

    override public func becomeFirstResponder() -> Bool {
        textField.becomeFirstResponder()
    }

    override public func resignFirstResponder() -> Bool {
        textField.resignFirstResponder()
    }

    override public func endEditing(_ force: Bool) -> Bool {
        textField.endEditing(force)
    }

    override public func awakeFromNib() {
        super.awakeFromNib()
        textField.delegate = self
        textField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
        textField.addTarget(self, action: #selector(textFieldDidBeginEditing(_:)), for: .editingDidBegin)
        textField.addTarget(self, action: #selector(textFieldDidEndEditing(_:)), for: .editingDidEnd)
        textFieldLabel.isHidden = true

        iconContainerView.layer.roundCorners(7)        
    }

    override public func prepareForReuse() {
        super.prepareForReuse()
        accessoryType = .none
        textField.text = nil
        textField.placeholder = nil
        textFieldLabel.text = nil
    }

    override public func isValid() -> Bool {
        textField.isValid && validateIfRequired()
    }
    
    private func validateIfRequired() -> Bool {
        guard isRequired else {
            return true
        }
        
        return textField.text.nilIfEmpty != nil
    }

    override public func validate() {
        super.validate()
        
        handleError(isValid: errorsEnabled ? isValid() : true)
        
        if textField.text?.isEmpty == true {
            accessoryType = .none
        }
    }

    @objc
    func textFieldDidChange(_: FormTextField) {
        validate()
        delegate?.textInputCellDidChange(self)
    }
}

extension TextInputCell: UITextFieldDelegate {
    public func textFieldShouldReturn(_: UITextField) -> Bool {
        if isValid() {
            defer { delegate?.textInputCellDidReturn(self) }
            return true
        }
        return false
    }

    public func textFieldDidBeginEditing(_: UITextField) {
        delegate?.textInputCellDidBeginEditing(self)
    }

    public func textFieldDidEndEditing(_: UITextField) {
        validate()
        delegate?.textInputCellDidChange(self)
        delegate?.textInputCellDidEndEditing(self)
    }

    public func textField(_: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        delegate?.textInputCellShouldChangeCharacters(self, shouldChangeCharactersIn: range, replacementString: string) ?? true
    }
}
