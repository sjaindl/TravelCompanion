//
//  FirestoreRemoteConfig.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 04.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import FirebaseRemoteConfig
import Foundation

class FirestoreRemoteConfig {
    
    static let sharedInstance = FirestoreRemoteConfig()
    
    private(set) var numberOfPhotosToDownload: Int = Constants.CoreData.PHOTO_LIMIT
    private(set) var photoResizingHeight: Int = Constants.RemoteConfig.LocalDefaultValues.PHOTO_RESIZING_HEIGHT
    private(set) var photoResizingWidth: Int = Constants.RemoteConfig.LocalDefaultValues.PHOTO_RESIZING_WIDTH
    private(set) var transportSearchAutocomplete: Bool = Constants.RemoteConfig.LocalDefaultValues.TRANSPORT_SEARCH_AUTOCOMPLETE
    
    private var inAppDefaults: [String: NSObject] = [
        Constants.RemoteConfig.Keys.PHOTOS_TO_DOWNLOAD: Constants.CoreData.PHOTO_LIMIT as NSObject,
        Constants.RemoteConfig.Keys.PHOTO_RESIZING_HEIGHT: Constants.RemoteConfig.LocalDefaultValues.PHOTO_RESIZING_HEIGHT as NSObject,
        Constants.RemoteConfig.Keys.PHOTO_RESIZING_WIDTH: Constants.RemoteConfig.LocalDefaultValues.PHOTO_RESIZING_WIDTH as NSObject,
        Constants.RemoteConfig.Keys.TRANSPORT_SEARCH_AUTOCOMPLETE: Constants.RemoteConfig.LocalDefaultValues.TRANSPORT_SEARCH_AUTOCOMPLETE as NSObject
    ]
    
    private init() {
        //Set in-app default values
        RemoteConfig.remoteConfig().setDefaults(inAppDefaults)
        
        //Fetch remote config values
        if let numberOfPhotosToDownload = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.PHOTOS_TO_DOWNLOAD).numberValue as? Int {
            self.numberOfPhotosToDownload = numberOfPhotosToDownload
        }
        
        if let photoResizingHeight = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.PHOTO_RESIZING_HEIGHT).numberValue as? Int {
            self.photoResizingHeight = photoResizingHeight
        }
        
        if let photoResizingWidth = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.PHOTO_RESIZING_WIDTH).numberValue as? Int {
            self.photoResizingWidth = photoResizingWidth
        }
        
        self.transportSearchAutocomplete = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.TRANSPORT_SEARCH_AUTOCOMPLETE).boolValue
    }
}
