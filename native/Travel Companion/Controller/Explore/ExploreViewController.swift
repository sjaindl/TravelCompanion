//
//  ExploreViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 08.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import CoreLocation
import FirebaseAuth
import FirebaseAuthUI
import FirebaseFirestore
import GoogleMaps
import UIKit
import shared

class ExploreViewController: UIViewController, PlacePicker {
    
    @IBOutlet weak var map: GMSMapView!
    
    fileprivate var _authHandle: AuthStateDidChangeListenerHandle!
    var dataController: DataController!
    var fetchedResultsController: NSFetchedResultsController<Pin>!
    var mapCenter: CLLocationCoordinate2D? = nil
    
    var redirectAfterLogin = false
    var cachedPin: Pin?
    
    var firestoreDbReference: CollectionReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "explore".localized()
        
        map.delegate = self
        
        if Auth.auth().currentUser?.uid != nil {
            firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.places)
        }
        
        initCamera()
        initResultsController()
        fetchData()
    }
    
    deinit {
        firestoreDbReference = nil
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        addAuthListener()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if let _authHandle = _authHandle {
            Auth.auth().removeStateDidChangeListener(_authHandle)
        }
    }
    
    func addAuthListener() {
        // listen for changes in the authorization state
        _authHandle = Auth.auth().addStateDidChangeListener { (auth: Auth, user: User?) in
            
            // check if there is a current user
            if user != nil, self.redirectAfterLogin, let cachedPin = self.cachedPin {
                //redirect to plan detail after successful login
                self.performSegue(withIdentifier: Constants.Segues.addPlan, sender: cachedPin)
                self.redirectAfterLogin = false
                self.cachedPin = nil
            }
        }
    }
    
    func initResultsController() {
        let fetchRequest: NSFetchRequest<Pin> = Pin.fetchRequest()
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.sortKey, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: Constants.CoreData.cacheNamePins)
    }
    
    func fetchData() {
        do {
            try fetchedResultsController.performFetch()
            if let result = fetchedResultsController.fetchedObjects, result.count > 0 {
                for pin in result {
                    let coordinate = CLLocationCoordinate2DMake(pin.latitude, pin.longitude)
                    let marker = addPinToMap(with: coordinate)
                    store(pin, in: marker)
                }
            } else if firestoreDbReference != nil {
                fetchFromFirestore()
            }
        } catch {
            UiUtils.showError(error.localizedDescription, controller: self)
        }
    }
    
    func fetchFromFirestore() {
        firestoreDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                for document in querySnapshot!.documents {
                    debugPrint("\(document.documentID) => \(document.data())")
                    
                    let placeId = document.data()[FirestoreConstants.Ids.Place.placeId] as? String
                    let latitude = document.data()[FirestoreConstants.Ids.Place.latitude] as? Double
                    let longitude = document.data()[FirestoreConstants.Ids.Place.longitude] as? Double
                    
                    if let placeId = placeId, let latitude = latitude, let longitude = longitude {
                        let pin = CoreDataClient.sharedInstance.storePin(self.dataController, placeId: placeId, latitude: latitude, longitude: longitude)
                        let coordinate = CLLocationCoordinate2DMake(pin.latitude, pin.longitude)
                        let marker = self.addPinToMap(with: coordinate)
                        self.store(pin, in: marker)
                        
                        try? self.dataController.save()
                    }
                }
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.exploreDetail || segue.identifier == Constants.Segues.explorePhotos || segue.identifier == Constants.Segues.exploreWiki {
            let controller = segue.destination as! UITabBarController
            let detailTargetController = controller.viewControllers![0] as! ExploreDetailViewController
            let photosTargetController = controller.viewControllers![1] as! ExplorePhotosViewController
            let infoTargetController = controller.viewControllers![2] as! ExploreInfoViewController
            
            detailTargetController.pin = sender as? Pin
            detailTargetController.dataController = dataController
            
            photosTargetController.pin = sender as? Pin
            photosTargetController.dataController = dataController
            
            infoTargetController.pin = sender as? Pin
        } else if segue.identifier == Constants.Segues.searchPlaces {
            let controller = segue.destination as! ExplorePlacesSearchViewController
            controller.callback = self
        } else if segue.identifier == Constants.Segues.addPlan {
            let controller = segue.destination as! AddPlanViewController
            let pin = sender as! Pin
            controller.pins = [pin]
            controller.dataController = dataController
        }
    }
    
    func initCamera() {
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.zoomLevel)
        let latitude = UserDefaults.standard.double(forKey: Constants.UserDefaults.mapLatitude)
        let longitude = UserDefaults.standard.double(forKey: Constants.UserDefaults.mapLongitude)
        
        setCamera(with: latitude, longitude: longitude, zoom: zoom)
    }
    
    func setCamera(with latitude: Double, longitude: Double, zoom: Float) {
        let camera = GMSCameraPosition.camera(withLatitude: latitude,
                                              longitude: longitude,
                                              zoom: zoom)
        
        map.camera = camera
    }
    
    func addPinToMap(with coordinate: CLLocationCoordinate2D) -> GMSMarker {
        let marker = GMSMarker(position: coordinate)
        marker.map = map
        marker.appearAnimation = .pop
        
        return marker
    }
    
    func persistPin(of place: PlacesDetailsResponse, placeId: String, countryCode: String?) -> Pin {
        let pin = CoreDataClient.sharedInstance.storePin(dataController, place: place, placeId: placeId, countryCode: countryCode)
        
        if firestoreDbReference != nil, let name = place.result.name {
            FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: placeId, data: [
                FirestoreConstants.Ids.Place.placeId: placeId,
                FirestoreConstants.Ids.Place.name: name,
                FirestoreConstants.Ids.Place.latitude: place.result.geometry.location.lat,
                FirestoreConstants.Ids.Place.longitude: place.result.geometry.location.lng
            ]) { (error) in
                if let error = error {
                    debugPrint("Error adding document: \(error.localizedDescription)")
                    DispatchQueue.main.async {
                        UiUtils.showError(error.localizedDescription, controller: self)
                    }
                } else {
                    debugPrint("Document added")
                }
            }
        }
        
        return pin
    }
    
    func store(_ pin: Pin, in marker: GMSMarker) {
        marker.userData = pin
    }
    
    func didPickPlace(_ place: PlacesDetailsResponse, for placeId: String) {
        let location = place.result.geometry.location
        let latitude = location.lat
        let longitude = location.lng
        
        let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        let marker = addPinToMap(with: coordinate)
        
        //center map at newly added pin
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.zoomLevel)
        setCamera(with: latitude, longitude: longitude, zoom: zoom)
        
        TCInjector.shared.geoNamesClient.fetchCountryCode(latitude: latitude, longitude: longitude) { countryCode, error in
            DispatchQueue.main.async {
                if let error {
                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                } else if let countryCode {
                    let pin = self.persistPin(of: place, placeId: placeId, countryCode: countryCode)
                    self.store(pin, in: marker)
                    _ = self.showPlaceDialog(marker: marker)
                }
            }
        }
    }
    
    func removeFromCoreData(_ pin: Pin, marker: GMSMarker) {
        if let index = self.fetchedResultsController.indexPath(forObject: pin) {
            let object = self.fetchedResultsController.object(at: index)
            self.dataController.viewContext.delete(object)
            try? self.dataController.save()
        }
        
        marker.map = nil
    }
    
    func loginSession() {
        let authViewController = FUIAuth.defaultAuthUI()!.authViewController()
        present(authViewController, animated: true, completion: nil)
    }
    
    func showPlaceDialog(marker: GMSMarker) -> Bool {
        guard let pin = marker.userData as? Pin else {
            debugPrint("no valid pin")
            return false
        }
        
        let alert = UIAlertController(title: pin.name, message: nil, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "showDetails".localized(), style: .default, handler: { _ in
            self.performSegue(withIdentifier: Constants.Segues.exploreDetail, sender: marker.userData)
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        if let pin = marker.userData as? Pin, let pinName = pin.name {
            alert.addAction(UIAlertAction(title: "delete".localized(), style: .default, handler: { _ in
                
                if self.firestoreDbReference != nil {
                    //checks whether there is a plan. if so, shows alert message and doesn't delete (plan must be deleted first by user).
                    let firestorePlanDbReference: CollectionReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans)
                    
                    let documentReference = firestorePlanDbReference.document(pinName)
                    
                    documentReference.getDocument { (document, error) in
                        if let document = document, document.exists {
                            UiUtils.showError(String(format: "planExists".localized(), pinName), controller: self)
                        } else {
                            //delete from Firestore
                            if let placeId = pin.placeId {
                                self.firestoreDbReference.document(placeId).delete() { error in
                                    if let error = error {
                                        debugPrint("Error removing document: \(error.localizedDescription)")
                                        UiUtils.showError(error.localizedDescription, controller: self)
                                    } else {
                                        debugPrint("Document successfully removed!")
                                    }
                                }
                            }
                            
                            self.removeFromCoreData(pin, marker: marker)
                        }
                    }
                } else {
                    self.removeFromCoreData(pin, marker: marker)
                }
                
                self.dismiss(animated: true, completion: nil)
            }))
            
            let action = UIAlertAction(title: "planTrip".localized(), style: .default, handler: { _ in
                
                if self.firestoreDbReference == nil {
                    //Login required to use this feature, redirect to login!
                    self.redirectAfterLogin = true
                    self.cachedPin = pin
                    self.loginSession()
                } else {
                    self.performSegue(withIdentifier: Constants.Segues.addPlan, sender: pin)
                }
            })
            
            let image = UIImage(named: "lock")
            action.setValue(image?.withRenderingMode(.alwaysOriginal), forKey: "image")
            alert.addAction(action)
        }
        
        alert.addAction(UIAlertAction(title: "cancel".localized(), style: .default, handler: { _ in
            self.dismiss(animated: true, completion: nil)
        }))
        
        present(alert, animated: true, completion: nil)
        
        return true
    }
}

extension ExploreViewController: GMSMapViewDelegate {
    
    func mapView(_ mapView: GMSMapView, idleAt position: GMSCameraPosition) {
        if position.target.latitude != 0 && position.target.longitude != 0 {
            UserDefaults.standard.set(position.target.latitude, forKey: Constants.UserDefaults.mapLatitude)
            UserDefaults.standard.set(position.target.longitude, forKey: Constants.UserDefaults.mapLongitude)
            UserDefaults.standard.set(position.zoom, forKey: Constants.UserDefaults.zoomLevel)
            
            mapCenter = position.target
        }
    }
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        return showPlaceDialog(marker: marker)
    }
    
    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        performSegue(withIdentifier: Constants.Segues.searchPlaces, sender: nil)
    }
}

protocol PlacePicker {
    func didPickPlace(_ place: PlacesDetailsResponse, for placeId: String)
}
