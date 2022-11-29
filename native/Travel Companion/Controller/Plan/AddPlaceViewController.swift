//
//  AddPlaceViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import GoogleMaps
import shared
import UIKit

class AddPlaceViewController: UIViewController {
    struct SelectedPlace {
        var marker: GMSMarker
        var coordinate: CLLocationCoordinate2D
    }
    
    @IBOutlet weak var map: GMSMapView!
    @IBOutlet weak var radiusSlider: UISlider!
    @IBOutlet weak var radiusLabel: UILabel!
    
    var selectedPlace: SelectedPlace?
    var searchView: UISearchBar?
    var placeType: GooglePlaceType!
    var firestoreDbReference: CollectionReference!
    var pin: Pin?
    var plan: Plan!
    
    func initPlacesSearchViewController() -> PlacesSearchViewController {
        let controller = PlacesSearchViewController(
            apiKey: SecretConstants.apiKeyGooglePlaces,
            placeType: placeType,
            coordinate: (selectedPlace?.coordinate)!,
            firestoreDbReference: firestoreDbReference,
            plan: plan,
            radius: Double(radiusSlider!.value) * 1000 //convert from km to m
            // Optional: strictBounds: true,
            // Optional: searchBarPlaceholder: "Start typing..."
        )
        //Optional: controller.searchBar.isTranslucent = false
        //Optional: controller.searchBar.barStyle = .black
        //Optional: controller.searchBar.tintColor = .white
        //Optional: controller.searchBar.barTintColor = .black
        return controller
    }
    
    @IBAction func radiusChanged(_ sender: UISlider) {
        let currentValue = round(sender.value * 10) / 10 // round to 100 metres
        radiusSlider.setValue(currentValue, animated: false)
        radiusLabel.text = "searchRadius".localized() + "\(currentValue) km"
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = String(format: "addPlace".localized(), placeType.description) // TODO: rename var
        
        map.delegate = self
        initCamera()
        
        radiusLabel.text = "searchRadius".localized() + "\(Int(radiusSlider.value)) km"
    }
    
    func initCamera() {
        let zoom = Float(Constants.zoomLevelDetail)
        let latitude = pin?.latitude ?? Constants.UserDefaults.mapLatitudeStandard
        let longitude = pin?.longitude ?? Constants.UserDefaults.mapLongitudeStandard
        
        let camera = GMSCameraPosition.camera(withLatitude: latitude,
                                              longitude: longitude,
                                              zoom: zoom)
        
        map.camera = camera
    }

}

extension AddPlaceViewController: GMSMapViewDelegate {
    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        let marker = addPinToMap(with: coordinate)
        selectedPlace = SelectedPlace(marker: marker, coordinate: coordinate)
        
        guard let reachability = Network.reachability, reachability.isReachable else {
            UiUtils.showError("offline".localized(), controller: self)
            selectedPlace?.marker.map = nil
            selectedPlace = nil
            return
        }
        
        search()
    }
    
    func addPinToMap(with coordinate: CLLocationCoordinate2D) -> GMSMarker {
        let position = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        let marker = GMSMarker(position: position)
        marker.map = map
        
        return marker
    }
    
    func search() {
        searchView?.removeFromSuperview()
        
        let placesSearchController = initPlacesSearchViewController()
        searchView = placesSearchController.searchBar
        
        self.view.addSubview(placesSearchController.searchBar)
        
        present(placesSearchController, animated: true, completion: nil)
        
        selectedPlace?.marker.map = nil
        selectedPlace = nil
    }
}
