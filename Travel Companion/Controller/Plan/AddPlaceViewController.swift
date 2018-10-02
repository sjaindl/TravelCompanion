//
//  AddPlaceViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import GoogleMaps
import UIKit

class AddPlaceViewController: UIViewController {
    
    struct SelectedPlace {
        var marker: GMSMarker
        var coordinate: CLLocationCoordinate2D
    }
    
    @IBOutlet weak var map: GMSMapView!
    
    var selectedPlace: SelectedPlace?
    var searchView: UISearchBar?
    var placeType: GooglePlaceType!
    var firestoreDbReference: CollectionReference!
    var pin: Pin?
    
    func initPlacesSearchViewController() -> PlacesSearchViewController {
        let controller = PlacesSearchViewController(
            apiKey: SecretConstants.GOOGLE_PLACES_API_KEY,
            placeType: placeType,
            coordinate: (selectedPlace?.coordinate)!,
            firestoreDbReference: firestoreDbReference
            // Optional: radius: 10,
            // Optional: strictBounds: true,
            // Optional: searchBarPlaceholder: "Start typing..."
        )
        //Optional: controller.searchBar.isTranslucent = false
        //Optional: controller.searchBar.barStyle = .black
        //Optional: controller.searchBar.tintColor = .white
        //Optional: controller.searchBar.barTintColor = .black
        return controller
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "Add \(placeType.rawValue) Place"
        
        map.delegate = self
        initCamera()
    }
    
    func initCamera() {
        let zoom = Float(Constants.ZOOM_LEVEL_DETAIL)
        let latitude = pin?.latitude ?? Constants.UserDefaults.STANDARD_LATITUDE
        let longitude = pin?.longitude ?? Constants.UserDefaults.STANDARD_LONGITUDE
        
        let camera = GMSCameraPosition.camera(withLatitude: latitude,
                                              longitude: longitude,
                                              zoom: zoom)
        
        map.camera = camera
    }

}

extension AddPlaceViewController: GMSMapViewDelegate {
    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        let isFirstSearch = (selectedPlace == nil)
        
        if !isFirstSearch {
            selectedPlace!.marker.map = nil
            selectedPlace = nil
        }
        
        let marker = addPinToMap(with: coordinate)
        selectedPlace = SelectedPlace(marker: marker, coordinate: coordinate)
        
        searchView?.removeFromSuperview()
        
        let placesSearchController = initPlacesSearchViewController()
        searchView = placesSearchController.searchBar
//        searchView?.translatesAutoresizingMaskIntoConstraints = false
        
        self.view.addSubview(placesSearchController.searchBar)

//        searchView?.topAnchor.constraint(equalTo: self.view.topAnchor, constant: 10).isActive = true
//        searchView?.leadingAnchor.constraint(equalTo: self.view.leadingAnchor, constant: 32).isActive = true
//        searchView?.trailingAnchor.constraint(equalTo: self.view.trailingAnchor, constant: -32).isActive = true
//        searchView?.heightAnchor.constraint(equalToConstant: 40.0).isActive = true
        
        present(placesSearchController, animated: true, completion: nil)
    }
    
    func addPinToMap(with coordinate: CLLocationCoordinate2D) -> GMSMarker {
        let position = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        let marker = GMSMarker(position: position)
        marker.map = map
        
        return marker
    }
}
