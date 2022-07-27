//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension UIApplication {
    var appWindow: UIWindow? {
        UIApplication.shared.windows.first { $0.isKeyWindow }
    }
    
    var statusBarHeight: CGFloat {
        appWindow?.windowScene?.statusBarManager?.statusBarFrame.height ?? 0
    }
    
    var interfaceOrientation: UIInterfaceOrientation? {
        appWindow?.windowScene?.interfaceOrientation
    }
    
    func openSystemSettings() {
        guard let url = URL(string: Self.openSettingsURLString) else {
            return
        }
        open(url, options: [:], completionHandler: nil)
    }
}
