//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public final class ErrorView: UIView, XibLoadable {
    public static let xibName = "ErrorView"
    
    @IBOutlet private var wrapper: UIView!
    @IBOutlet private var label: UILabel!

    public var errorText: String? {
        didSet {
            updateUI()
        }
    }
    
    override public func awakeFromNib() {
        super.awakeFromNib()
        updateUI()
    }
    
    private func updateUI() {
        wrapper.backgroundColor = .red
        
        label.text = errorText
        label.textColor = .white
        label.font = .systemFont(ofSize: 13, weight: .semibold)
    }
}
