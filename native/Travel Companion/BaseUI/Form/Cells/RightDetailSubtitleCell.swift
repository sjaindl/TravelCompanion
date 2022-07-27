//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class RightDetailSubtitleCell: UITableViewCell, XibLoadable {
    public static let xibName = "RightDetailSubtitleCell"

    @IBOutlet public var titleLabel: UILabel!
    @IBOutlet public var subtitleLabel: UILabel!
    @IBOutlet public var rightDetailLabel: UILabel!
    @IBOutlet public var stackView: UIStackView!

    public var additionalContentInset: UIEdgeInsets = .zero {
        didSet {
            stackView.layoutMargins = additionalContentInset
        }
    }
}
