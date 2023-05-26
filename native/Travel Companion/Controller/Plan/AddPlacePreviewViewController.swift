//
//  AddPlacePreviewViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import GoogleMaps
import shared
import UIKit

class AddPlacePreviewViewController: UIViewController {

    @IBOutlet weak var placeName: UILabel!
    @IBOutlet weak var address: UILabel!
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var map: GMSMapView!
    @IBOutlet weak var distance: UILabel!
    @IBOutlet weak var rating: UILabel!
    
    var searchedLocation: CLLocation!
    var googlePlace: GooglePlace!
    
    var firestoreDbReference: CollectionReference!
    var plan: Plan!
    var placeType: GooglePlaceType!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        placeName.text = googlePlace.name
        
        if let imageUrl = googlePlace.imageUrl(), let url = URL(string: imageUrl) {
            ((try? image?.image = UIImage(data: Data(contentsOf: url))) as ()??)
        }
        
        distance.text = ""
        
        var ratingText = "noRating".localized()
        if let googleRating = googlePlace.rating {
            ratingText = "rating".localized() + String(googleRating) + "/5 *"
            if let numberOfRatings = googlePlace.userRatingsTotal {
                ratingText += " (" + String(numberOfRatings) + " " + "ratings".localized() + ")"
            }
        }
        rating.text = ratingText
        
        address.text = googlePlace.vicinity
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if let latitude = googlePlace.geometry?.location.lat, let longitude = googlePlace.geometry?.location.lng {
            let marker = GMSMarker(position: CLLocationCoordinate2D(latitude: latitude, longitude: longitude))
            marker.map = map
            marker.appearAnimation = .pop
            
            //center map at newly added pin
            let zoomLevel = Float(Constants.UserDefaults.zoomLevelStandardGooglePlaceAdd)
            let target = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
            map.camera = GMSCameraPosition.camera(withTarget: target, zoom: zoomLevel)
            
            let placeLocation = CLLocation(latitude: latitude, longitude: longitude)
            let locationDistance = searchedLocation.distance(from: placeLocation) / 1000
            distance.text = "distance".localized() + String(format: " %.01fkm", locationDistance) //Display the result in km
        }
    }
    
    @IBAction func selectPlace(_ sender: Any) {
        persistPlace(googlePlace)
    }
    
    @IBAction func cancel(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    func persistPlace(_ place: GooglePlace) {
        let docData = try! FirestoreEncoder().encode(place)
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: place.getId(), data: docData) { (error) in
            if let error = error {
                UiUtils.showToast(message: error.localizedDescription, view: self.view)
            } else {
                debugPrint("Document added")
                
                self.addPlaceToPlan(place)
                
                DispatchQueue.main.async {
                    UiUtils.showToast(message: "addedPlace".localized(), view: self.view)
                    
                    //show toast for 1 second
                    DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(1), execute: {
                        self.dismiss(animated: true, completion: nil)
                    })
                }
            }
        }
         
    }
    
    func addPlaceToPlan(_ place: GooglePlace) {
        if placeType == GooglePlaceType.lodging {
            plan.hotels.append(place)
        } else if placeType == GooglePlaceType.restaurant {
            plan.restaurants.append(place)
        } else {
            //some attraction
            plan.attractions.append(place)
        }
    }
}
