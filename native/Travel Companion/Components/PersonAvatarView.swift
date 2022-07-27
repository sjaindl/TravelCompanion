//
//  PersonAvatarView.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class PersonAvatarView: UIView, XibLoadable {
    public static let xibName = "PersonAvatarView"

    @IBOutlet private var imageView: UIImageView!
    @IBOutlet private var initialsView: UIView!
    
    @IBOutlet public var textLabel: UILabel!

    public var initials: String? {
        didSet {
            if let initials = initials {
                initialsView.isHidden = false
                imageView.isHidden = true // TODO: implement image support when needed
                textLabel.isHidden = false
                textLabel.text = initials
            } else {
                imageView.isHidden = true
                textLabel.isHidden = true
                initialsView.isHidden = true
            }

            setNeedsLayout()
        }
    }

    override public func awakeFromNib() {
        super.awakeFromNib()
        updateTintColor()
    }

    override public func layoutSubviews() {
        super.layoutSubviews()

        let mask = CAShapeLayer()
        mask.path = UIBezierPath(ovalIn: bounds).cgPath
        mask.fillColor = UIColor.black.cgColor

        layer.mask = mask
    }

    override public func tintColorDidChange() {
        super.tintColorDidChange()
        updateTintColor()
    }

    // MARK: Private

    private func updateTintColor() {
        initialsView.backgroundColor = tintColor ?? .magenta
    }
}
