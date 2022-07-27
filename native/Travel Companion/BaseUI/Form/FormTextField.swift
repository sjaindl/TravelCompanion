//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public final class FormTextField: UITextField {
    public var validator: (String?) -> Bool = { _ in true }

    public var isValid: Bool { validator(text) }

    override public var isEnabled: Bool {
        didSet {
            textColor = isEnabled ? UIColor.appTextColorDefault() : UIColor.gray
        }
    }
}
