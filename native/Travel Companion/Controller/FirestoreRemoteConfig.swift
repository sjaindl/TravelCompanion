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
    
    private(set) var numberOfPhotosToDownload: Int = Constants.CoreData.photoLimit
    private(set) var photoResizingHeight: Int = Constants.RemoteConfig.LocalDefaultValues.photoResizingHeight
    private(set) var photoResizingWidth: Int = Constants.RemoteConfig.LocalDefaultValues.photoResizingWidth
    private(set) var transportSearchAutocomplete: Bool = Constants.RemoteConfig.LocalDefaultValues.transportSearchAutocomplete
    
    private var inAppDefaults: [String: NSObject] = [
        Constants.RemoteConfig.Keys.numberOfPhotosToDownload: Constants.CoreData.photoLimit as NSObject,
        Constants.RemoteConfig.Keys.photoResizingHeight: Constants.RemoteConfig.LocalDefaultValues.photoResizingHeight as NSObject,
        Constants.RemoteConfig.Keys.photoResizingWidth: Constants.RemoteConfig.LocalDefaultValues.photoResizingWidth as NSObject,
        Constants.RemoteConfig.Keys.transportSearchAutocomplete: Constants.RemoteConfig.LocalDefaultValues.transportSearchAutocomplete as NSObject
    ]
    
    private init() {
        //Set in-app default values
        RemoteConfig.remoteConfig().setDefaults(inAppDefaults)
    }
    
    func activateFetched() {
        RemoteConfig.remoteConfig().fetch { (remoteConfigFetchStatus, error) in
            guard error == nil else {
                debugPrint("Couldn't fetch RemoteConfig. Applying default values.")
                return
            }
            
            self.fetchRemoteConfigValues()
            RemoteConfig.remoteConfig().activate()
        }
    }
    
    func fetchRemoteConfigValues() {
        if let numberOfPhotosToDownload = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.numberOfPhotosToDownload).numberValue as? Int {
            self.numberOfPhotosToDownload = numberOfPhotosToDownload
        }
        
        if let photoResizingHeight = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.photoResizingHeight).numberValue as? Int {
            self.photoResizingHeight = photoResizingHeight
        }
        
        if let photoResizingWidth = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.photoResizingWidth).numberValue as? Int {
            self.photoResizingWidth = photoResizingWidth
        }
        
        self.transportSearchAutocomplete = RemoteConfig.remoteConfig().configValue(forKey: Constants.RemoteConfig.Keys.transportSearchAutocomplete).boolValue
    }
}
