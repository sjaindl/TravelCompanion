//
//  ExplorePhotosViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 14.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import GoogleMaps
import GooglePlaces
import UIKit

class ExplorePhotosViewController: UIViewController {
    @IBOutlet private var map: GMSMapView!
    @IBOutlet private var collectionView: UICollectionView!
    @IBOutlet private var flowLayout: UICollectionViewFlowLayout!
    @IBOutlet private var countryButton: UIButton!
    @IBOutlet private var placeButton: UIButton!
    @IBOutlet private var latlongButton: UIButton!
    @IBOutlet private var activityIndicator: UIActivityIndicatorView!
    @IBOutlet private var noPhotoLabel: UILabel!
    
    var pin: Pin?
    var dataController: DataController?
    var dataSource: GenericListDataSource<Photos, AlbumCollectionViewCell>?
    var fetchType: Int = FetchType.Country.rawValue
    
    var choosePhoto = false
    var plan: Plan!
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.tabBarController?.navigationItem.title = pin?.name
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        initMap()
        initResultsController()
        fetchData()
        setButtonState()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        dataSource?.fetchedResultsController = nil
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        UiUtils.setupFlowLayout(for: self, view: view, flowLayout: flowLayout)
    }
    
    @IBAction func fetchByCountry(_ sender: Any) {
        fetch(by: FetchType.Country.rawValue)
    }
    
    @IBAction func fetchByPlace(_ sender: Any) {
        fetch(by: FetchType.Place.rawValue)
    }
    
    @IBAction func fetchByLatLong(_ sender: Any) {
        fetch(by: FetchType.LatLong.rawValue)
    }
    
    func fetch(by type: Int) {
        resetFetchedResultsController()
        
        fetchType = type
        initResultsController()
        setButtonState()
        
        fetchData()
    }
    
    func setButtonState() {
        countryButton.isEnabled = fetchType != FetchType.Country.rawValue ? true : false
        placeButton.isEnabled = fetchType != FetchType.Place.rawValue ? true : false
        latlongButton.isEnabled = fetchType != FetchType.LatLong.rawValue ? true : false
    }
    
    func resetFetchedResultsController() {
        if let pin = pin {
            NSFetchedResultsController<NSFetchRequestResult>.deleteCache(withName: "\(Constants.CoreData.cacheNamePhotos)-\(pin.objectID)")
        }
       
        dataSource?.fetchedResultsController = nil
        collectionView.reloadData()
    }
    
    func initResultsController() {
        guard let dataController = dataController else {
            debugPrint("dataController is nil, cannot init results controller")
            return
        }
        
        let fetchRequest: NSFetchRequest<Photos> = Photos.fetchRequest()
        
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.sortKey, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        if let pin = pin {
            let predicate = NSPredicate(format: "pin = %@ AND type = %d", pin, fetchType)
            fetchRequest.predicate = predicate
        }
        
        var cacheName: String? = nil
        if let pin = pin {
            cacheName = "\(Constants.CoreData.cacheNamePhotos)-\(pin.objectID)"
        }
        
        dataSource = GenericListDataSource(
            collectionView: collectionView,
            managedObjectContext: dataController.viewContext,
            fetchRequest: fetchRequest,
            cellReuseId: Constants.ReuseIds.albumCell,
            cacheName: cacheName
        )
    }
    
    func fetchData() {
        enableUi(false)
        
        self.noPhotoLabel.isHidden = true
        
        DispatchQueue.global().async {
            do {
                try self.dataSource?.performFetch()
                if let result = self.dataSource?.fetchedObjects() {
                    if result.count == 0 {
                        self.fetchRemoteData()
                    } else {
                        DispatchQueue.main.async {
                            self.collectionView.reloadData()
                            self.enableUi(true)
                        }
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.enableUi(true)
                    self.noPhotoLabel.isHidden = false
                    UiUtils.showError(error.localizedDescription, controller: self)
                }
            }
        }
    }
    
    func fetchRemoteData() {
        if fetchType == FetchType.Place.rawValue {
            fetchDataFromGooglePlacePhotos()
        } else {
            fetchDataFromFlickr()
        }
    }
    
    func fetchDataFromGooglePlacePhotos() {
        guard let placeId = pin?.placeId else {
            debugPrint("Could not fetchDataFromGooglePlacePhotos")
            return
        }
        
        DispatchQueue.main.async {
            self.loadPhotosOfPlace(placeID: placeId)
        }
    }
    
    func fetchDataFromFlickr() {
        let latitude = pin?.latitude
        let longitude = pin?.longitude
        var country = pin?.countryCode
        if let countryName = pin?.country {
            country = countryName //search primarily by whole country name, if not available by country code
        }
        
        DispatchQueue.global().async {
            var queryItems = FlickrClient.sharedInstance.buildQueryItems()
            if self.fetchType == FetchType.Country.rawValue {
                guard let country = country else {
                    UiUtils.showError("noCountryPhotos".localized(), controller: self)
                    
                    DispatchQueue.main.async {
                        self.noPhotoLabel.isHidden = false
                        self.enableUi(true)
                    }
                    return
                }
                
                queryItems[FlickrConstants.ParameterKeys.text] = country
            } else if self.fetchType == FetchType.LatLong.rawValue {
                guard let latitude = latitude, let longitude = longitude else {
                    debugPrint("Could not fetch photos by latitude/longitude")
                    return
                }
                
                queryItems[FlickrConstants.ParameterKeys.boundingBox] = FlickrClient.sharedInstance.bboxString(latitude: latitude, longitude: longitude)
            }
            
            FlickrClient.sharedInstance.fetchPhotos(with: queryItems) { (error, isEmpty, photos) in
                if let error = error {
                    DispatchQueue.main.async {
                        UiUtils.showError(error, controller: self)
                        self.noPhotoLabel.isHidden = false
                        self.enableUi(true)
                    }
                } else {
                    DispatchQueue.main.async {
                        guard let photos = photos else {
                            self.noPhotoLabel.isHidden = false
                            self.enableUi(true)
                            return
                        }
                        
                        if let dataController = self.dataController, let pin = self.pin {
                            for photo in photos {
                                _ = CoreDataClient.sharedInstance.storePhoto(dataController, photo: photo, pin: pin, fetchType: self.fetchType)
                            }
                        }
                        
                        self.enableUi(true)
                    }
                }
            }
        }
    }
    
    func loadPhotosOfPlace(placeID: String) {
        GMSPlacesClient.shared().lookUpPhotos(forPlaceID: placeID) { (photos, error) -> Void in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
                self.noPhotoLabel.isHidden = false
                self.enableUi(true)
                
                return
            } else {
                if let photos = photos?.results, photos.count > 0 {
                    if let dataController = self.dataController, let pin = self.pin {
                        for photo in photos {
                            CoreDataClient.sharedInstance.storePhoto(dataController, placePhoto: photo, pin: pin, fetchType: self.fetchType) { (error) in
                                if let error = error {
                                    UiUtils.showError(error, controller: self)
                                }
                            }
                        }
                    }
                } else {
                    self.noPhotoLabel.isHidden = false
                }
            }
            self.enableUi(true)
            self.collectionView.reloadData()
        }
    }
    
    func initMap() {
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.zoomLevel)
        
        if let pin = pin {
            let camera = GMSCameraPosition.camera(withLatitude: pin.latitude,
                                                  longitude: pin.longitude,
                                                  zoom: zoom)
            
            map.camera = camera
            
            addPinToMap(with: CLLocationCoordinate2D(latitude: pin.latitude, longitude: pin.longitude))
        }
    }
    
    func enableUi(_ enable: Bool) {
        if enable {
            activityIndicator.stopAnimating()
        } else {
            activityIndicator.startAnimating()
        }
    }
    
    func addPinToMap(with coordinate: CLLocationCoordinate2D) {
        let position = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        let marker = GMSMarker(position: position)
        marker.map = map
    }
}

extension ExplorePhotosViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return dataSource?.numberOfSections(in: collectionView) ?? 1
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource?.collectionView(collectionView, numberOfItemsInSection: section) ?? 0
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Constants.ReuseIds.albumCell, for: indexPath) as! AlbumCollectionViewCell
        
        cell.locationImage.image = UIImage(named: Constants.CoreData.placeholderImage)
        
        if let imageData = dataSource?.object(at: indexPath)?.imageData {
            cell.locationImage.image = UIImage(data: imageData)
        } else {
            
            if let imagePath = dataSource?.object(at: indexPath)?.imageUrl {
                
                WebClient.sharedInstance.downloadImage(imagePath: imagePath) { (imageData, error) in
                    if let error = error {
                        UiUtils.showError(error, controller: self)
                    } else {
                        guard let imageData = imageData else {
                            UiUtils.showError("errorDownloadImage".localized(), controller: self)
                            return
                        }
                        
                        DispatchQueue.main.async {
                            cell.locationImage.image = UIImage(data: imageData)
                        }
                    }
                }
            }
        }
        
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let photo = dataSource?.fetchedResultsController?.object(at: indexPath)
        
        guard photo?.imageData != nil else {
            debugPrint("No image data to display")
            UiUtils.showToast(message: "waitForPhotos".localized(), view: self.view)
            return
        }
        
        if choosePhoto {
            plan.imageData = photo?.imageData
            self.navigationController?.popViewController(animated: true)
        } else {
            performSegue(withIdentifier: Constants.Segues.photoDetail, sender: photo)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.photoDetail {
            let destinationViewController = segue.destination as! PhotosDetailViewController
            let photo = sender as! Photos
            destinationViewController.data = photo.imageData
            destinationViewController.text = photo.title
        }
    }
}
