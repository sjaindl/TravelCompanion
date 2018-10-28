//
//  ExploreViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 08.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import CoreLocation
import Firebase
import GoogleMaps
import GooglePlacePicker
import UIKit

class ExploreViewController: UIViewController {

    @IBOutlet weak var map: GMSMapView!
    
    var dataController: DataController!
    var fetchedResultsController: NSFetchedResultsController<Pin>!
    var mapCenter: CLLocationCoordinate2D? = nil
    
    var firestoreDbReference: CollectionReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "explore".localized()
        
        map.delegate = self
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.places)
        
        initCamera()
        initResultsController()
        fetchData()
    }
    
    deinit {
        firestoreDbReference = nil
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
            } else {
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
            let wikivoyagetargetController = controller.viewControllers![2] as! WikiViewController
            let wikitargetController = controller.viewControllers![3] as! WikiViewController
            detailTargetController.pin = sender as? Pin
            detailTargetController.dataController = dataController
            photosTargetController.pin = sender as? Pin
            photosTargetController.dataController = dataController
            wikitargetController.pin = sender as? Pin
            wikitargetController.domain = WikiConstants.UrlComponents.domainWikipedia
            wikivoyagetargetController.pin = sender as? Pin
            wikivoyagetargetController.domain = WikiConstants.UrlComponents.domainWikiVoyage
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
        let position = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        let marker = GMSMarker(position: position)
        marker.map = map
        
        return marker
    }
    
    func persistPin(of place: GMSPlace, countryCode: String?) -> Pin {
        let pin = CoreDataClient.sharedInstance.storePin(dataController, place: place, countryCode: countryCode)
        
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: place.placeID, data: [
            FirestoreConstants.Ids.Place.placeId: place.placeID,
            FirestoreConstants.Ids.Place.name: place.name,
            FirestoreConstants.Ids.Place.latitude: place.coordinate.latitude,
            FirestoreConstants.Ids.Place.longitude: place.coordinate.longitude
        ]) { (error) in
            if let error = error {
                debugPrint("Error adding document: \(error.localizedDescription)")
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                debugPrint("Document added")
            }
        }
        
        return pin
    }
    
    func store(_ pin: Pin, in marker: GMSMarker) {
        marker.userData = pin
    }
    
    @IBAction func addPlace(_ sender: Any) {
        var viewport: GMSCoordinateBounds?
        
        if let center = mapCenter {
            let northEast = CLLocationCoordinate2D(latitude: center.latitude + 0.001,
                                                   longitude: center.longitude + 0.001)
            let southWest = CLLocationCoordinate2D(latitude: center.latitude - 0.001,
                                                   longitude: center.longitude - 0.001)
            viewport = GMSCoordinateBounds(coordinate: northEast, coordinate: southWest)
        }
        
        let config = GMSPlacePickerConfig(viewport: viewport)
        let placePicker = GMSPlacePickerViewController(config: config)
        placePicker.delegate = self
        
        present(placePicker, animated: true, completion: nil)
    }
}

extension ExploreViewController: GMSMapViewDelegate {
    
    func mapView(_ mapView: GMSMapView, idleAt position: GMSCameraPosition) {
        UserDefaults.standard.set(position.target.latitude, forKey: Constants.UserDefaults.mapLatitude)
        UserDefaults.standard.set(position.target.longitude, forKey: Constants.UserDefaults.mapLongitude)
        UserDefaults.standard.set(position.zoom, forKey: Constants.UserDefaults.zoomLevel)
        
        mapCenter = position.target
    }
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        
        let alert = UIAlertController(title: "chooseAction".localized(), message: nil, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "showDetails".localized(), style: .default, handler: { _ in
            self.performSegue(withIdentifier: Constants.Segues.exploreDetail, sender: marker.userData)
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "delete".localized(), style: .default, handler: { _ in
            if let pin = marker.userData as? Pin, let pinName = pin.name {
                
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
                        
                        if let index = self.fetchedResultsController.indexPath(forObject: pin) {
                            let object = self.fetchedResultsController.object(at: index)
                            self.dataController.viewContext.delete(object)
                            try? self.dataController.save()
                        }
                        
                        marker.map = nil
                    }
                }
            }
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "cancel".localized(), style: .default, handler: { _ in
            self.dismiss(animated: true, completion: nil)
        }))
        
        present(alert, animated: true, completion: nil)
        
        return true
    }
}

extension ExploreViewController : GMSPlacePickerViewControllerDelegate {
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        let marker = addPinToMap(with: place.coordinate)
        
        //center map at newly added pin
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.zoomLevel)
        setCamera(with: place.coordinate.latitude, longitude: place.coordinate.longitude, zoom: zoom)
        
        GeoNamesClient.sharedInstance.fetchCountryCode(latitude: place.coordinate.latitude, longitude: place.coordinate.longitude) { (error, code) in
            var countryCode: String?
            if let code = code as? String {
                countryCode = code
            }
            let pin = self.persistPin(of: place, countryCode: countryCode)
            self.store(pin, in: marker)
        }
        
        // Dismiss the place picker.
        viewController.dismiss(animated: true, completion: nil)
    }
    
    func placePicker(_ viewController: GMSPlacePickerViewController, didFailWithError error: Error) {
        UiUtils.showError(error.localizedDescription, controller: self)
    }
    
    func placePickerDidCancel(_ viewController: GMSPlacePickerViewController) {
        debugPrint("The place picker was canceled by the user")
        
        // Dismiss the place picker.
        viewController.dismiss(animated: true, completion: nil)
    }
}
