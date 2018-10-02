//
//  PlacesSearchViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import UIKit
import CoreLocation

public enum GooglePlaceType: String {
    case lodging           //hotels
    case restaurant        //restaurants
    
    //attractions:
    case point_of_interest
    case amusement_park
    case aquarium
    case art_gallery
    case atm
    case bank
    case bar
    case beauty_salon
    case bowling_alley
    case cafe
    case casino
    case church
    case city_hall
    case embassy
    case gym
    case hindu_temple
    case library
    case mosque
    case movie_theater
    case museum
    case night_club
    case post_office
    case rv_park
    case shopping_mall
    case spa
    case stadium
    case synagogue
    case travel_agency
    case zoo
}

open class PlacesSearchViewController: UISearchController, UISearchBarDelegate {
    
    convenience public init(apiKey: String, placeType: GooglePlaceType, coordinate: CLLocationCoordinate2D, firestoreDbReference: CollectionReference, radius: CLLocationDistance = 0, strictBounds: Bool = false, searchBarPlaceholder: String = "Enter Place") {
        
        let gpaViewController = GooglePlacesAutocompleteContainer(
            apiKey: apiKey,
            firestoreDbReference: firestoreDbReference,
            placeType: placeType,
            coordinate: coordinate,
            radius: radius,
            strictBounds: strictBounds
        )
        
        self.init(searchResultsController: gpaViewController)
        
        self.searchResultsUpdater = gpaViewController
        self.hidesNavigationBarDuringPresentation = true
        self.definesPresentationContext = true
        self.searchBar.placeholder = searchBarPlaceholder
        self.searchBar.text = " " //necessary to expand tableview for initial search
    }
}


open class GooglePlacesAutocompleteContainer: UITableViewController {
    
    private var apiKey: String = ""
    var firestoreDbReference: CollectionReference!
    private var placeType: GooglePlaceType = .lodging
    private var coordinate: CLLocationCoordinate2D = kCLLocationCoordinate2DInvalid
    private var radius: Double = 0.0
    private var strictBounds: Bool = false
    private let cellIdentifier = "Cell"
    private var currentSearchText = ""
    
    private var places = [GooglePlace]() {
        didSet {
            DispatchQueue.main.async {
                self.tableView.reloadData()
            }
        }
    }
    
    convenience init(apiKey: String, firestoreDbReference: CollectionReference, placeType: GooglePlaceType, coordinate: CLLocationCoordinate2D, radius: Double, strictBounds: Bool) {
        self.init()
        
        self.apiKey = apiKey
        self.firestoreDbReference = firestoreDbReference
        self.placeType = placeType
        self.coordinate = coordinate
        self.radius = radius
        self.strictBounds = strictBounds
    }
}

extension GooglePlacesAutocompleteContainer {
    
    override open func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return places.count
    }
    
    override open func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier) ?? UITableViewCell(style: .subtitle, reuseIdentifier: cellIdentifier)
        
        let place = places[indexPath.row]
        
        cell.textLabel?.text = place.description()
        let detailText = place.details()
        if let attributions = place.html_attributions, attributions.count > 0, let attribution = UiUtils.getLinkAttributedText(attributions[0]) {
            detailText.append(NSAttributedString(string: "\n"))
            detailText.append(attribution)
        }
        cell.detailTextLabel?.attributedText = detailText
        cell.accessoryType = .disclosureIndicator
        
        return cell
    }
    
    override open func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let place = places[indexPath.row]
        
        let docData = try! FirestoreEncoder().encode(place)
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: place.getId(), data: docData) { (error) in
            if let error = error {
                UiUtils.showToast(message: "Error adding document: \(error)", view: self.view)
            } else {
                print("Document added")
                self.dismiss(animated: true, completion: nil)
            }
        }
    }
}

extension GooglePlacesAutocompleteContainer: UISearchResultsUpdating {
    
    public func updateSearchResults(for searchController: UISearchController) {
        guard let searchText = searchController.searchBar.text, !searchText.isEmpty, currentSearchText != searchText else {
            places = [];
            return
        }
        
        currentSearchText = searchText
        
        GoogleClient.sharedInstance.searchPlaces(for: searchText, coordinate: CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude), type: placeType.rawValue) { (error, places) in
            self.places = places
        }
    }
}
