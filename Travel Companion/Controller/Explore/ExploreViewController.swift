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
        
        // Do any additional setup after loading the view.
        self.navigationItem.title = "Explore"
        
        map.delegate = self
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLACES)
        
        initCamera()
        initResultsController()
        fetchData()
    }
    
    deinit {
        firestoreDbReference = nil
    }
    
    func initResultsController() {
        let fetchRequest: NSFetchRequest<Pin> = Pin.fetchRequest()
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.SORT_KEY, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: Constants.CoreData.CACHE_NAME_PINS)
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
            fatalError("The fetch could not be performed: \(error.localizedDescription)")
        }
    }
    
    func fetchFromFirestore() {
        firestoreDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                print("Error getting documents: \(error)")
            } else {
                for document in querySnapshot!.documents {
                    print("\(document.documentID) => \(document.data())")
                    
                    let placeId = document.data()[FirestoreConstants.Ids.Place.PLACE_ID] as? String
                    let latitude = document.data()[FirestoreConstants.Ids.Place.LATITUDE] as? Double
                    let longitude = document.data()[FirestoreConstants.Ids.Place.LONGITUDE] as? Double
                    
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
        if segue.identifier == Constants.SEGUES.EXPLORE_DETAIL_SEGUE_ID || segue.identifier == Constants.SEGUES.EXPLORE_PHOTOS_SEGUE_ID || segue.identifier == Constants.SEGUES.EXPLORE_WIKI_SEGUE_ID {
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
            wikitargetController.domain = WikiConstants.UrlComponents.DOMAIN_WIKIPEDIA
            wikivoyagetargetController.pin = sender as? Pin
            wikivoyagetargetController.domain = WikiConstants.UrlComponents.DOMAIN_WIKIVOYAGE
        }
    }
    
    func initCamera() {
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.USER_DEFAULT_ZOOM_LEVEL)
        let latitude = UserDefaults.standard.double(forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LATITUDE)
        let longitude = UserDefaults.standard.double(forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LONGITUDE)
        
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
            FirestoreConstants.Ids.Place.PLACE_ID: place.placeID,
            FirestoreConstants.Ids.Place.NAME: place.name,
            FirestoreConstants.Ids.Place.LATITUDE: place.coordinate.latitude,
            FirestoreConstants.Ids.Place.LONGITUDE: place.coordinate.longitude
        ]) { (error) in
            if let error = error {
                print("Error adding document: \(error)")
            } else {
                print("Document added")
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
        UserDefaults.standard.set(position.target.latitude, forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LATITUDE)
        UserDefaults.standard.set(position.target.longitude, forKey: Constants.UserDefaults.USER_DEFAULT_MAP_LONGITUDE)
        UserDefaults.standard.set(position.zoom, forKey: Constants.UserDefaults.USER_DEFAULT_ZOOM_LEVEL)
        
        mapCenter = position.target
    }
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        
        let alert = UIAlertController(title: "Choose Action", message: nil, preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("Show details", comment: "Show details"), style: .default, handler: { _ in
            self.performSegue(withIdentifier: Constants.SEGUES.EXPLORE_DETAIL_SEGUE_ID, sender: marker.userData)
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("Delete", comment: "Delete"), style: .default, handler: { _ in
            if let pin = marker.userData as? Pin {
                //delete from Firestore
                if let placeId = pin.placeId {
                    self.firestoreDbReference.document(placeId).delete() { err in
                        if let err = err {
                            print("Error removing document: \(err)")
                        } else {
                            print("Document successfully removed!")
                        }
                    }
                }
                
                if let index = self.fetchedResultsController.indexPath(forObject: pin) {
                    let object = self.fetchedResultsController.object(at: index)
                    self.dataController.viewContext.delete(object)
                    try? self.dataController.save()
                }
            }
            
            marker.map = nil
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "Cancel"), style: .default, handler: { _ in
            self.dismiss(animated: true, completion: nil)
        }))
        
        present(alert, animated: true, completion: nil)
        
        return true
    }
}

extension ExploreViewController : GMSPlacePickerViewControllerDelegate {
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        let marker = addPinToMap(with: place.coordinate)
        
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
        // In your own app you should handle this better, but for the demo we are just going to log
        // a message.
        NSLog("An error occurred while picking a place: \(error)")
    }
    
    func placePickerDidCancel(_ viewController: GMSPlacePickerViewController) {
        NSLog("The place picker was canceled by the user")
        
        // Dismiss the place picker.
        viewController.dismiss(animated: true, completion: nil)
    }
}
