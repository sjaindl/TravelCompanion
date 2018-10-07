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
    
    @IBOutlet weak var map: GMSMapView!
    @IBOutlet weak var collectionView: UICollectionView!
    @IBOutlet weak var flowLayout: UICollectionViewFlowLayout!
    @IBOutlet weak var countryButton: UIButton!
    @IBOutlet weak var placeButton: UIButton!
    @IBOutlet weak var latlongButton: UIButton!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var noPhotoLabel: UILabel!
    
    var pin: Pin!
    var dataController: DataController!
    var dataSource: GenericListDataSource<Photos, AlbumCollectionViewCell>!
    var fetchType: Int = FetchType.Country.rawValue
    
    var choosePhoto: Bool = false //TODO: enum refactor
    var plan: Plan!
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        dataSource.fetchedResultsController = nil
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        initMap()
        initResultsController()
        fetchData()
        setButtonState()
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.tabBarController?.navigationItem.title = pin.name
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
        NSFetchedResultsController<NSFetchRequestResult>.deleteCache(withName: "\(Constants.CoreData.CACHE_NAME_PHOTOS)-\(pin.objectID)")
        dataSource.fetchedResultsController = nil
        collectionView.reloadData()
    }
    
    func initResultsController() {
        let fetchRequest: NSFetchRequest<Photos> = Photos.fetchRequest()
        
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.SORT_KEY, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        let predicate = NSPredicate(format: "pin = %@ AND type = %d", pin, fetchType)
        
        fetchRequest.predicate = predicate
        
        dataSource = GenericListDataSource(collectionView: collectionView, managedObjectContext: dataController.viewContext, fetchRequest: fetchRequest, cellReuseId: Constants.REUSE_IDS.ALBUM_CELL_REUSE_ID, cacheName: "\(Constants.CoreData.CACHE_NAME_PHOTOS)-\(pin.objectID)")
    }
    
    func fetchData() {
        enableUi(false)
        
        self.noPhotoLabel.isHidden = true
        
        DispatchQueue.global().async {
            do {
                try self.dataSource.performFetch()
                if let result = self.dataSource.fetchedObjects() {
                    if result.count == 0 {
                        self.fetchRemoteData()
                    }
                    else {
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
                    UiUtils.showError("Could not fetch data \(error.localizedDescription)", controller: self)
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
        DispatchQueue.main.async {
            self.loadPhotosOfPlace(placeID: self.pin.placeId!)
        }
    }
    
    func fetchDataFromFlickr() {
        let latitude = pin.latitude
        let longitude = pin.longitude
        var country = pin.countryCode
        if let countryName = pin.country {
            country = countryName //search primarily by whole country name, if not available by country code
        }
        
        DispatchQueue.global().async {
            var queryItems = FlickrClient.sharedInstance.buildQueryItems()
            if self.fetchType == FetchType.Country.rawValue {
                guard let country = country else {
                    UiUtils.showError("This location is not in a country. Can't fetch country photos.", controller: self)
                    
                    DispatchQueue.main.async {
                        self.noPhotoLabel.isHidden = false
                        self.enableUi(true)
                    }
                    return
                }
                
                queryItems[FlickrConstants.FlickrParameterKeys.Text] = country
            } else if self.fetchType == FetchType.LatLong.rawValue {
                queryItems[FlickrConstants.FlickrParameterKeys.BoundingBox] = FlickrClient.sharedInstance.bboxString(latitude: latitude, longitude: longitude)
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
                        
                        for photo in photos {
                            _ = CoreDataClient.sharedInstance.storePhoto(self.dataController, photo: photo, pin: self.pin, fetchType: self.fetchType)
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
                    for photo in photos {
                        CoreDataClient.sharedInstance.storePhoto(self.dataController, placePhoto: photo, pin: self.pin, fetchType: self.fetchType)
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
        let zoom = UserDefaults.standard.float(forKey: Constants.UserDefaults.USER_DEFAULT_ZOOM_LEVEL)
        
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

extension ExplorePhotosViewController : UICollectionViewDelegate, UICollectionViewDataSource {
    
    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return dataSource.numberOfSections(in: collectionView)
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return dataSource.collectionView(collectionView, numberOfItemsInSection: section)
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Constants.REUSE_IDS.ALBUM_CELL_REUSE_ID, for: indexPath) as! AlbumCollectionViewCell
        
        cell.locationImage.image = UIImage(named: Constants.CoreData.PLACEHOLDER_IMAGE)
        
        if let imageData = dataSource.object(at: indexPath).imageData {
            cell.locationImage.image = UIImage(data: imageData)
        } else {
            
            if let imagePath = dataSource.object(at: indexPath).imageUrl {
                
                WebClient.sharedInstance.downloadImage(imagePath: imagePath) { (imageData, error) in
                    if let error = error {
                        UiUtils.showError(error, controller: self)
                    } else {
                        guard let imageData = imageData else {
                            UiUtils.showError("Could not download image", controller: self)
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
        let photo = dataSource.fetchedResultsController.object(at: indexPath)
        
        guard photo.imageData != nil else {
            debugPrint("No image data to display")
            UiUtils.showToast(message: "Please wait for photo downloads to finish", view: self.view)
            return
        }
        
        if choosePhoto {
            plan.imageData = photo.imageData
            self.navigationController?.popViewController(animated: true)
        } else {
            performSegue(withIdentifier: Constants.SEGUES.PHOTO_DETAIL_SEGUE_ID, sender: photo)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.SEGUES.PHOTO_DETAIL_SEGUE_ID {
            let destinationViewController = segue.destination as! PhotosDetailViewController
            let photo = sender as! Photos
            destinationViewController.data = photo.imageData
            destinationViewController.text = photo.title
        }
    }
}
