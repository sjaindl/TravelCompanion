//
//  ExplorePhotosDetailViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 18.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import UIKit

class ExplorePhotosDetailViewController: UIViewController {

    @IBOutlet weak var photoImage: UIImageView!
    @IBOutlet weak var photoTitle: UILabel!
    
    var photo: Photos!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let data = photo.imageData {
            photoImage.image = UIImage(data: data)
        }
        
        photoTitle.text = photo.title
    }
}
