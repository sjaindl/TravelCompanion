//
//  AppDelegate.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 15.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import UIKit
import CoreData
import Firebase
import FirebaseAuthUI
import GoogleMaps

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
//    let dataController: DataController = DataController(modelName: "VirtualTourist")
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        GMSServices.provideAPIKey(SecretConstants.GOOGLE_MAPS_API_KEY)
        checkIfFirstLaunch()
//        dataController.load()
        
        FirebaseApp.configure()
        
//        let navigationController = window?.rootViewController as! UINavigationController
//        let travelLocationsListViewController = navigationController.topViewController as! TravelLocationsMapViewController
//        travelLocationsListViewController.dataController = dataController
        
        return true
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
        save()
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
        // Saves changes in the application's managed object context before the application terminates.
        save()
    }
    
    func application(_ application: UIApplication, open url: URL, sourceApplication: String?, annotation: Any) -> Bool {
        return FUIAuth.defaultAuthUI()?.handleOpen(url, sourceApplication: sourceApplication ?? "") ?? false
    }
    
    func checkIfFirstLaunch() {
        if !UserDefaults.standard.bool(forKey: Constants.UserDefaults.USER_DEFAULT_LAUNCHED_BEFORE) {
            UserDefaults.standard.set(true, forKey: Constants.UserDefaults.USER_DEFAULT_LAUNCHED_BEFORE)
            UserDefaults.standard.set(Constants.UserDefaults.STANDARD_ZOOM_LEVEL, forKey: Constants.UserDefaults.USER_DEFAULT_ZOOM_LEVEL)
            UserDefaults.standard.set(Constants.UserDefaults.STANDARD_LONGITUDE, forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LONGITUDE)
            UserDefaults.standard.set(Constants.UserDefaults.STANDARD_LATITUDE, forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LATITUDE)
        }
    }
    
    func save() {
//        try? dataController.save()
    }
}
