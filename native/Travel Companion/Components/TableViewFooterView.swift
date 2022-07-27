//
//  TableViewFooterView.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class TableViewFooterView: UIView, XibLoadable {
    public static let xibName = "TableViewFooterView"

    @IBOutlet private var stackView: UIStackView!
    @IBOutlet private var textView: UITextView!
    @IBOutlet private var textViewHeightConstraint: NSLayoutConstraint!

    @IBOutlet public var button: UIButton!

    override public func awakeFromNib() {
        super.awakeFromNib()
        button.isHidden = true
        textView.isHidden = true

        textView.attributedText = NSAttributedString(string: "not empty")
        textView.font = UIFont.systemFont(ofSize: UIFont.smallSystemFontSize)
        textView.isOpaque = false
        textView.isEditable = false
    }
}

public extension TableViewFooterView {
    struct Builder {
        let footerView = TableViewFooterView.loadXib(bundle: .main)

        public init() { }

        public func withButtonTitle(title: String, color: UIColor) -> Builder {
            footerView.button.isHidden = false
            footerView.button.setTitle(title, for: .normal)
            footerView.button.setTitleColor(color, for: .normal)
            return self
        }

        public func withText(attributed: NSAttributedString, delegate: UITextViewDelegate?) -> Builder {
            footerView.textView.isHidden = false
            footerView.textView.attributedText = attributed
            footerView.textView.delegate = delegate

            let size = footerView.textView.sizeThatFits(footerView.frame.size)
            footerView.textViewHeightConstraint.constant = size.height

            return self
        }

        public func build() -> TableViewFooterView {
            footerView
        }
    }
}
