//
//  UserHeaderTableViewCell.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import UIKit

public final class UserHeaderTableViewCell: UITableViewCell, XibLoadable {
    public static let xibName = "UserHeaderTableViewCell"

    @IBOutlet private var profileImageView: UIImageView!
    @IBOutlet private var nameLabel: UILabel!

    override public var backgroundColor: UIColor? {
        didSet {
            guard profileImageView != nil, nameLabel != nil else {
                return
            }
            profileImageView.backgroundColor = backgroundColor
            nameLabel.backgroundColor = backgroundColor
        }
    }

    public var user: User? {
        didSet {
            updateUI()
        }
    }

    override public func awakeFromNib() {
        super.awakeFromNib()

        setupUI()
    }

    // MARK: Private

    private func setupUI() {
        profileImageView.layer.roundCorners(profileImageView.frame.width / 2)
        profileImageView.clipsToBounds = true
    }

    private func updateUI() {
        profileImageView.image = user?.initialsAvatarImage(size: profileImageView.frame.size)
        nameLabel.text = user?.displayName
    }
}
