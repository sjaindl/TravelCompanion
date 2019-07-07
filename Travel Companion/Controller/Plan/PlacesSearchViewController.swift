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

class PlacesSearchViewController: UISearchController, UISearchBarDelegate {
    
    convenience public init(apiKey: String, placeType: GooglePlaceType, coordinate: CLLocationCoordinate2D, firestoreDbReference: CollectionReference, plan: Plan, radius: CLLocationDistance = 0, strictBounds: Bool = false, searchBarPlaceholder: String = "enterPlace".localized()) {
        
        let gpaViewController = GooglePlacesAutocompleteContainer(
            apiKey: apiKey,
            firestoreDbReference: firestoreDbReference,
            plan: plan,
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


class GooglePlacesAutocompleteContainer: UITableViewController {
    
    private var apiKey: String = ""
    private var firestoreDbReference: CollectionReference!
    private var plan: Plan!
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
    
    convenience init(apiKey: String, firestoreDbReference: CollectionReference, plan: Plan, placeType: GooglePlaceType, coordinate: CLLocationCoordinate2D, radius: Double, strictBounds: Bool) {
        self.init()
        
        self.apiKey = apiKey
        self.firestoreDbReference = firestoreDbReference
        self.plan = plan
        self.placeType = placeType
        self.coordinate = coordinate
        self.radius = radius
        self.strictBounds = strictBounds
        
        tableView.backgroundColor = UIColor.darkGray
    }
}

extension GooglePlacesAutocompleteContainer {
    
    override open func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return places.count
    }
    
    override open func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier) ?? UITableViewCell(style: .subtitle, reuseIdentifier: cellIdentifier)
        
        guard indexPath.row < places.count else {
            return cell
        }
        
        let place = places[indexPath.row]
        
        cell.textLabel?.text = place.description()
        let detailText = place.details()
        if let attributions = place.htmlAttributions, attributions.count > 0, let attribution = FormatUtils.getLinkAttributedText(attributions[0]) {
            detailText.append(NSAttributedString(string: "\n"))
            detailText.append(attribution)
        }
        cell.detailTextLabel?.attributedText = detailText
        cell.accessoryType = .disclosureIndicator
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.backgroundColor = UIColor.darkGray
        cell.textLabel?.textColor = CustomColors.appTextColorDefault()
        cell.detailTextLabel?.textColor = CustomColors.appTextColorDefault()
    }
    
    override open func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let place = places[indexPath.row]
        
        let docData = try! FirestoreEncoder().encode(place)
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: place.getId(), data: docData) { (error) in
            if let error = error {
                UiUtils.showToast(message: error.localizedDescription, view: self.view)
            } else {
                debugPrint("Document added")
                
                self.addPlaceToPlan(place)
                
                UiUtils.showToast(message: "addedPlace".localized(), view: self.view)
                
                //show toast for 1 second
                DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(1), execute: {
                    self.dismiss(animated: true, completion: nil)
                })
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

extension GooglePlacesAutocompleteContainer: UISearchResultsUpdating {
    
    public func updateSearchResults(for searchController: UISearchController) {
        guard let searchText = searchController.searchBar.text, !searchText.isEmpty, currentSearchText != searchText else {
            places = [];
            return
        }
        
        currentSearchText = searchText
        
        GoogleClient.sharedInstance.searchPlaces(for: searchText, coordinate: CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude), type: placeType.key) { (error, places) in
            self.places = places
        }
    }
}
