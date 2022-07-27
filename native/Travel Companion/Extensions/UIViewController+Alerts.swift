//  Travel Companion
//
//  Created by Stefan Jaindl on 27.07.22.
//  Copyright © 2022 Stefan Jaindl. All rights reserved.
//

import Foundation
import UIKit

public extension UIViewController {
    static func createLoadingAlert() -> UIAlertController {
        createLoadingAlert(title: "pleaseWait".localized())
    }
    
    static func createLoadingAlert(title: String) -> UIAlertController {
        let alert = UIAlertController(title: title, message: nil, preferredStyle: .alert)

        let indicator = LoadingSpinnerView(
            frame: .zero,
            topColor: UIColor.appTextColorDefault(),
            backColor: UIColor.white
        )
        indicator.translatesAutoresizingMaskIntoConstraints = false
        indicator.isUserInteractionEnabled = false
        indicator.startAnimating()

        let controller = UIViewController()
        controller.view.addSubview(indicator)

        indicator.heightAnchor.constraint(equalToConstant: 72).isActive = true
        indicator.widthAnchor.constraint(equalToConstant: 72).isActive = true

        controller.view.centerXAnchor.constraint(equalTo: indicator.centerXAnchor).isActive = true
        controller.view.centerYAnchor.constraint(equalTo: indicator.centerYAnchor).isActive = true

        alert.setValue(controller, forKey: "contentViewController")

        return alert
    }
    
    static func createAppSettingsAlert(message: String) -> UIAlertController {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "openSystemPreferences".localized(), style: .default) { _ in
            UIApplication.shared.openSystemSettings()
        })
        alert.addAction(UIAlertAction(title: "cancel".localized, style: .cancel))
        return alert
    }
}
