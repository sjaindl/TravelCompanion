//
//  ProfileTableViewCell.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import FirebaseAuth
import UIKit

public final class ProfileTableViewCell: UITableViewCell, XibLoadable {
    public static let xibName = "ProfileTableViewCell"

    @IBOutlet private var titleLabel: UILabel!
    @IBOutlet private var subtitleLabel: UILabel!
    @IBOutlet private var profileImageView: UIImageView!
    @IBOutlet private var profileImageViewBackgroundView: UIView!

    public var user: User? {
        didSet {
            updateUI()
        }
    }

    override public func awakeFromNib() {
        super.awakeFromNib()
        setupUI()

        profileImageViewBackgroundView.layer.roundCorners(profileImageView.bounds.width / 2)

        profileImageView.layer.roundCorners(profileImageView.bounds.width / 2)
        profileImageView.clipsToBounds = true
    }

    override public func prepareForReuse() {
        super.prepareForReuse()
        titleLabel.text = ""
        profileImageView.image = nil
        subtitleLabel.isHidden = true
    }

    // MARK: Private

    private func setupUI() {
        accessoryType = .disclosureIndicator
    }

    private func updateUI() {
        if let user = user {
            subtitleLabel.isHidden = false

            titleLabel.text = user.displayName
            subtitleLabel.text = "account".localized()
            profileImageView.image = user.initialsAvatarImage(size: profileImageView.frame.size)
        } else {
            subtitleLabel.isHidden = true

            titleLabel.text = "account".localized()
            profileImageView.image = UIImage(named: "navigation-user-placeholder")
        }
    }
}
