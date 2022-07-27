//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

open class FormCell: UITableViewCell {
    // MARK: Open

    open func isValid() -> Bool {
        false
    }

    open func validate() {
        accessoryType = isValid() ? .checkmark : .none
        accessoryView?.tintColor = UIColor.appTextColorDefault()
    }
    
    override open func awakeFromNib() {
        super.awakeFromNib()
        separatorInset = UIEdgeInsets(top: 0, left: 56, bottom: 0, right: 0)
        tintColor = UIColor.appTextColorDefault()
    }
    
    func handleError(isValid: Bool) {
        if isValid {
            errorView.removeFromSuperview()
        } else {
            stackView.addArrangedSubview(errorView)
        }
    }
    
    func configureErrorView(text: String) {
        errorView.errorText = text
    }

    @IBOutlet public var iconContainerView: UIView!
    @IBOutlet public var iconView: UIImageView!
    @IBOutlet private var stackView: UIStackView!
    
    public var icon: UIImage? {
        get { iconView?.image }
        set { iconView?.image = newValue }
    }
    
    public var isRequired = false {
        didSet {
            handleError(isValid: errorsEnabled ? isValid() : true)
        }
    }
    
    public var errorText: String? {
        didSet {
            errorView.errorText = errorText
        }
    }
    
    public var errorsEnabled = false
    
    public lazy var errorView = ErrorView.loadXib(bundle: .main)
}
