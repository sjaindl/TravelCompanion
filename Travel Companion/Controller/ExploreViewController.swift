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
    var deleteMode = false
    var mapCenter: CLLocationCoordinate2D? = nil
    
    //TODO: watch tut @ https://codelabs.developers.google.com/codelabs/firestore-ios/#0   https://firebase.google.com/docs/firestore/quickstart
    var firestoreDbReference: CollectionReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        map.delegate = self
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLACES)
        
        initCamera()
        initResultsController()
        fetchData()
    }
    
    deinit {
        firestoreDbReference = nil
    }
    
    @IBAction func deletePressed(_ sender: UIBarButtonItem) {
        deleteMode = !deleteMode
        sender.title = deleteMode ? "Tap pin to delete" : "Delete location"
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
//        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "Find new location", style: .plain, target: self, action: #selector(addPlace))
//        navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Delete location", style: .plain, target: self, action: #selector(setDeleteMode))
    }
    
    
//    @objc
//    func setDeleteMode() {
//        deleteMode = !deleteMode
//        navigationItem.rightBarButtonItem?.title = deleteMode ? "Tap pin to delete" : "Delete location"
//    }
    
//    override func didReceiveMemoryWarning() {
//        super.didReceiveMemoryWarning()
//        // Dispose of any resources that can be recreated.
//    }
    
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
                        let pin = CoreDataClient.storePin(self.dataController, placeId: placeId, latitude: latitude, longitude: longitude)
                        let coordinate = CLLocationCoordinate2DMake(pin.latitude, pin.longitude)
                        let marker = self.addPinToMap(with: coordinate)
                        self.store(pin, in: marker)
                    }
                }
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.ALBUM_SEGUE_ID { //TODO: change segue id to detail.. (tab bar controller)
            let controller = segue.destination as! ExploreDetailViewController
            controller.pin = sender as! Pin
            controller.dataController = dataController
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
    
    func persistPin(of place: GMSPlace) -> Pin {
        let pin = CoreDataClient.storePin(dataController, place: place)
        
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: place.placeID, data: [
            FirestoreConstants.Ids.Place.PLACE_ID: place.placeID,
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
    
//    func mapView(_ mapView: GMSMapView, didLongPressAt coordinate: CLLocationCoordinate2D) {
//        let marker = addPinToMap(with: coordinate)
//        let pin = persistPin(with: coordinate)
//        store(pin, in: marker)
//    }
    
    func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
        if deleteMode {
            if let pin = marker.userData as? Pin {
                
                //delete from Firestore
                if let placeId = pin.placeId {
                    firestoreDbReference.document(placeId).delete() { err in
                        if let err = err {
                            print("Error removing document: \(err)")
                        } else {
                            print("Document successfully removed!")
                        }
                    }
                }
                
                if let index = fetchedResultsController.indexPath(forObject: pin) {
                    let object = fetchedResultsController.object(at: index)
                    dataController.viewContext.delete(object)
                    try? dataController.save()
                }
            }
            
            marker.map = nil
        } else {
            performSegue(withIdentifier: Constants.EXPLORE_DETAIL_SEGUE_ID, sender: marker.userData)
        }
        
        return true
    }
}

extension ExploreViewController : GMSPlacePickerViewControllerDelegate {
    func placePicker(_ viewController: GMSPlacePickerViewController, didPick place: GMSPlace) {
        let marker = addPinToMap(with: place.coordinate)
        let pin = persistPin(of: place)
        store(pin, in: marker)
        
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
