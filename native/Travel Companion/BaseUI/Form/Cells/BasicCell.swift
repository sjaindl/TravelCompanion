//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class BasicCell: UITableViewCell, XibLoadable {
    // MARK: Public

    public static let xibName = "BasicCell"

    @IBOutlet var iconImageView: UIImageView!
    @IBOutlet var titleLabel: UILabel!

    @IBOutlet var imageWidthConstraint: NSLayoutConstraint!

    // MARK: Internal

    @IBOutlet var imageLabelMarginConstraint: NSLayoutConstraint!
}
