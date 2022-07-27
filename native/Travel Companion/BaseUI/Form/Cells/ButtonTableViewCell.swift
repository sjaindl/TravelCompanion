//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class ButtonTableViewCell: UITableViewCell, XibLoadable {
    public static let xibName = "ButtonTableViewCell"

    @IBOutlet private var label: UILabel!
    @IBOutlet private var activityIndicator: UIActivityIndicatorView!

    public var buttonText: String? {
        didSet {
            label.text = buttonText
        }
    }

    public var isLoading: Bool = false {
        didSet {
            label.isHidden = isLoading
            activityIndicator.isLoading = isLoading
        }
    }

    public var style: Form.LoadingButton.Style = .normal {
        didSet {
            updateUI()
        }
    }

    override public func awakeFromNib() {
        super.awakeFromNib()

        activityIndicator.isHidden = true
        updateUI()
    }

    override public func prepareForReuse() {
        super.prepareForReuse()

        activityIndicator.isHidden = true
        isLoading = false
        style = .normal
    }

    override public func setSelected(_: Bool, animated: Bool) {
        super.setSelected(false, animated: animated)
    }
}

private extension ButtonTableViewCell {
    func updateUI() {
        switch style {
        case .normal:
            label.textColor = UIColor.appTextColorDefault() // themeColors.primary.fillColor
        case .destructive:
            label.textColor = UIColor.red // themeColors.danger.fillColor
        case .disabled:
            label.textColor = UIColor.gray // themeColors.inactiveColor
        }
    }
}
