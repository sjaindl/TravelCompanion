//
//  AppDelegate.swift
//  VirtualTourist
//
//  Created by Stefan Jaindl on 15.06.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import FacebookCore
import FacebookLogin
import Firebase
import FirebaseAuthUI
import GoogleMaps
import GooglePlaces
import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    let dataController: DataController = DataController(persistentContainer: NSPersistentContainer(name: "TravelCompanion"))
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        initApis()
        checkIfFirstLaunch()
        initDataController()
        FirestoreRemoteConfig.sharedInstance.activateFetched()
        startNetworkReachabilityListener()
        
        let font = UIFont.systemFont(ofSize: 16)
        UITabBarItem.appearance().setTitleTextAttributes([NSAttributedString.Key.font: font], for: .normal)
        
        // Facebook
        ApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        
        return true
    }
    
    func initApis() {
        GMSServices.provideAPIKey(SecretConstants.apiKeyGoogleMaps) //Google Maps
        GMSPlacesClient.provideAPIKey(SecretConstants.apiKeyGooglePlaces) //Google Places
        //GMSServices.provideAPIKey(SecretConstants.apiKeyGooglePlaces) //Google PlacePicker
        FirebaseApp.configure() //Firebase
    }
    
    func initDataController() {
        dataController.load()
        
        let navigationController = window?.rootViewController as! UINavigationController
        let mainMenuViewController = navigationController.topMostViewController() as! MainMenuViewController
        mainMenuViewController.dataController = dataController
    }
    
    func startNetworkReachabilityListener() {
        do {
            Network.reachability = try Reachability(hostname: "www.google.com")
            do {
                try Network.reachability?.start()
            } catch let error as Network.Error {
                debugPrint(error)
            } catch {
                debugPrint(error)
            }
        } catch {
            debugPrint(error)
        }
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
        if !UserDefaults.standard.bool(forKey: Constants.UserDefaults.launchedBefore) {
            UserDefaults.standard.set(true, forKey: Constants.UserDefaults.launchedBefore)
            UserDefaults.standard.set(Constants.UserDefaults.zoomLevelStandard, forKey: Constants.UserDefaults.zoomLevel)
            UserDefaults.standard.set(Constants.UserDefaults.mapLongitudeStandard, forKey: Constants.UserDefaults.mapLongitude)
            UserDefaults.standard.set(Constants.UserDefaults.mapLatitudeStandard, forKey: Constants.UserDefaults.mapLatitude)
        }
    }
    
    func save() {
        try? dataController.save()
    }
}

extension UIViewController {
    func topMostViewController() -> UIViewController {
        if let navigation = self as? UINavigationController {
            return navigation.visibleViewController!.topMostViewController()
        }
        
        if let tab = self as? UITabBarController {
            if let selectedTab = tab.selectedViewController {
                return selectedTab.topMostViewController()
            }
            return tab.topMostViewController()
        }
        
        if self.presentedViewController == nil {
            return self
        }
        
        if let navigation = self.presentedViewController as? UINavigationController, let visibleController = navigation.visibleViewController {
            return visibleController.topMostViewController()
        }
        
        if let tab = self.presentedViewController as? UITabBarController {
            if let selectedTab = tab.selectedViewController {
                return selectedTab.topMostViewController()
            }
            return tab.topMostViewController()
        }
        
        return self.presentedViewController!.topMostViewController()
    }
}

extension UIApplication {
    func topMostViewController() -> UIViewController? {
        self.keyWindow?.rootViewController?.topMostViewController()
    }
}
