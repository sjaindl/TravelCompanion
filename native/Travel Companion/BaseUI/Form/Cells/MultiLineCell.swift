//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class MultiLineCell: UITableViewCell, XibLoadable {
    public static let xibName = "MultiLineCell"

    @IBOutlet public var multiLineLabel: UILabel!
    @IBOutlet public var placeholderLabel: UILabel!
}
