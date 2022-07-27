//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class RightDetailCell: UITableViewCell, XibLoadable {
    public static let xibName = "RightDetailCell"

    @IBOutlet public var titleLabel: UILabel!
    @IBOutlet public var detailLabel: UILabel!
}
