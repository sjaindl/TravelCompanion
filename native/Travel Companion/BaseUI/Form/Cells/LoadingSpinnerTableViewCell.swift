//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import UIKit

public final class LoadingSpinnerTableViewCell: UITableViewCell, XibLoadable {
    public static let xibName = "LoadingSpinnerTableViewCell"

    @IBOutlet public var activityIndicatorView: UIActivityIndicatorView!

    override public func awakeFromNib() {
        super.awakeFromNib()
        activityIndicatorView.startAnimating()
    }
}
