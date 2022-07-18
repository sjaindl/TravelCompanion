//
//  UiUtils.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 24.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation
import UIKit

class UiUtils {
    
    static func showToast(message : String, view: UIView) {
        let toastLabel = UILabel(frame: CGRect(x: view.frame.size.width/2 - 200, y: view.frame.size.height - 200, width: 500, height: 70))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 11.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        
        view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: {
            toastLabel.alpha = 0.0
        }, completion: {(isCompleted) in
            toastLabel.removeFromSuperview()
        })
    }
    
    static func setImage(_ imageName: String, for item: UIBarButtonItem) {
        let imageSetting = UIImageView(image: UIImage(named: imageName))
        imageSetting.image = imageSetting.image!.withRenderingMode(.alwaysOriginal)
        imageSetting.tintColor = UIColor.clear
        item.image = imageSetting.image
    }
    
    static func showError(_ error: String, controller: UIViewController) {
        //show alertview with error message
        let alert = UIAlertController(title: "Error", message: error, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        controller.present(alert, animated: true)
    }
    
    static func showHint(_ message: String, title: String, controller: UIViewController) {
        //show alertview with error message
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        controller.present(alert, animated: true)
    }
    
    static func layoutDatePicker(_ picker: UIDatePicker) {
        if #available(iOS 13.4, *) {
            picker.preferredDatePickerStyle = .wheels
            picker.setValue(false, forKeyPath: "highlightsToday")
        }
        
        layoutView(picker)
    }
    
    static func layoutView(_ view: UIView) {
        view.setValue(UIColor.cyan, forKey: "textColor")
    }
    
    static func setupFlowLayout(for controller: UIViewController, view: UIView, flowLayout: UICollectionViewFlowLayout) {
        let width = view.frame.size.width
        let height = view.frame.size.height
        let min = width > height ? height : width
        let max = width > height ? width : height
        
        let space:CGFloat = 3
        let dimension = controller.isPortrait ? (min - (2 * space)) / 2 : (max - (2 * space)) / 3
        flowLayout.minimumInteritemSpacing = space
        flowLayout.minimumLineSpacing = space
        
        flowLayout.itemSize = CGSize(width: dimension, height: dimension)
        
        debugPrint("space: \(space), dimension: \(dimension), portraitmode: \(controller.isPortrait)")
    }
    
    static func resizeImage(cell: UITableViewCell) {
        let itemSize = CGSize.init(width: 75, height: 75)
        UIGraphicsBeginImageContextWithOptions(itemSize, false, UIScreen.main.scale)
        let imageRect = CGRect.init(origin: CGPoint.zero, size: itemSize)
        cell.imageView?.image!.draw(in: imageRect)
        cell.imageView?.image! = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
    }
}
