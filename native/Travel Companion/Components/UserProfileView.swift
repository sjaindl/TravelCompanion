//
//  UserProfileView.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import UIKit

public final class UserProfileView: UIView, XibLoadable {
    public static let xibName = "UserProfileView"

    public var buttonAction: (() -> Void)?

    @IBOutlet public var button: UIButton!
    @IBOutlet public var buttonTrailingConstraint: NSLayoutConstraint!
    
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!

    override public func awakeFromNib() {
        super.awakeFromNib()

        setupUI()
        updateUI(user: nil)
    }

    public func updateUI(initials: String?, size: CGSize) {
        if let initials = initials {
            button.setImage(UIImage.initialsImage(initials: initials, size: size), for: .normal)
        } else {
            button.setImage(UIImage(named: "navigation-user-placeholder"), for: .normal)
        }
    }

    public func updateUI(user: User?) {
        if let user = user {
            if user.photoURL != nil {
                button.setImage(UIImage(named: "image-placeholder"), for: .normal)
            } else {
                button.setImage(user.initialsAvatarImage(size: button.frame.size), for: .normal)
            }
        } else {
            button.setImage(UIImage(named: "navigation-user-placeholder"), for: .normal)
        }
    }

    @IBAction func didTapButton(_: Any) {
        buttonAction?()
    }

    // MARK: Private

    private func setupUI() {
        button.layer.roundCorners(button.bounds.width / 2)
        button.clipsToBounds = true
    }
}
