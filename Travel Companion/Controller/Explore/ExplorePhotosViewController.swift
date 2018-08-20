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
    var fetchType: Int = Constants.FetchType.Country.rawValue
    
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
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        setupFlowLayout()
    }
    
    @IBAction func fetchByCountry(_ sender: Any) {
        resetFetchedResultsController()
        
        fetchType = Constants.FetchType.Country.rawValue
        initResultsController()
        setButtonState()
        
        fetchData()
    }
    
    @IBAction func fetchByPlace(_ sender: Any) {
        resetFetchedResultsController()
        
        fetchType = Constants.FetchType.Place.rawValue
        initResultsController()
        setButtonState()
        
        fetchData()
    }
    
    @IBAction func fetchByLatLong(_ sender: Any) {
        resetFetchedResultsController()
        
        fetchType = Constants.FetchType.LatLong.rawValue
        initResultsController()
        setButtonState()
        
        fetchData()
    }
    
    func setButtonState() {
        countryButton.isEnabled = fetchType != Constants.FetchType.Country.rawValue ? true : false
        placeButton.isEnabled = fetchType != Constants.FetchType.Place.rawValue ? true : false
        latlongButton.isEnabled = fetchType != Constants.FetchType.LatLong.rawValue ? true : false
    }
    
    func resetFetchedResultsController() {
        NSFetchedResultsController<NSFetchRequestResult>.deleteCache(withName: "\(Constants.CoreData.CACHE_NAME_PHOTOS)-\(pin.objectID)")
        dataSource.fetchedResultsController = nil
        collectionView.reloadData()
    }
    
    func setupFlowLayout() {
        let width = view.frame.size.width
        let height = view.frame.size.height
        let min = width > height ? height : width
        let max = width > height ? width : height
        
        let space:CGFloat = 3
        let isPortraitMode = UIDevice.current.orientation == .portrait || UIDevice.current.orientation == .portraitUpsideDown
        let dimension = isPortraitMode ? (min - (2 * space)) / 2 : (max - (2 * space)) / 3
        
        flowLayout.minimumInteritemSpacing = space
        flowLayout.minimumLineSpacing = space
        flowLayout.itemSize = CGSize(width: dimension, height: dimension)
    }
    
    func initResultsController() {
        let fetchRequest: NSFetchRequest<Photos> = Photos.fetchRequest()
        
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.SORT_KEY, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        let predicate = NSPredicate(format: "pin = %@ AND type = %d", pin, fetchType)
        
        fetchRequest.predicate = predicate
        
        dataSource = GenericListDataSource(collectionView: collectionView, managedObjectContext: dataController.viewContext, fetchRequest: fetchRequest, cellReuseId: Constants.ALBUM_CELL_REUSE_ID, cacheName: "\(Constants.CoreData.CACHE_NAME_PHOTOS)-\(pin.objectID)")
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
                    self.showError("Could not fetch data \(error.localizedDescription)")
                }
            }
        }
    }
    
    func showError(_ error: String) {
        //show alertview with error message
        let alert = UIAlertController(title: "Error", message: error, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
        self.present(alert, animated: true)
    }
    
    func fetchRemoteData() {
        if fetchType == Constants.FetchType.Place.rawValue {
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
        let country = pin.country
        
        DispatchQueue.global().async {
            var queryItems = FlickrClient.sharedInstance.buildQueryItems()
            if self.fetchType == Constants.FetchType.Country.rawValue {
                guard let country = country else {
                    debugPrint("This location is not in a country. Can't fetch country photos.")
                    
                    DispatchQueue.main.async {
                        self.noPhotoLabel.isHidden = false
                        self.enableUi(true)
                    }
                    return
                }
                
                queryItems[FlickrConstants.FlickrParameterKeys.Text] = country
            } else if self.fetchType == Constants.FetchType.LatLong.rawValue {
                queryItems[FlickrConstants.FlickrParameterKeys.BoundingBox] = FlickrClient.sharedInstance.bboxString(latitude: latitude, longitude: longitude)
            }
            
            FlickrClient.sharedInstance.fetchPhotos(with: queryItems) { (error, isEmpty, photos) in
                if let error = error {
                    self.showError(error)
                    self.noPhotoLabel.isHidden = false
                    self.enableUi(true)
                } else {
                    DispatchQueue.main.async {
                        guard let photos = photos else {
                            self.noPhotoLabel.isHidden = false
                            self.enableUi(true)
                            return
                        }
                        
                        for photo in photos {
                            self.persistPhoto(photo: photo)
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
                self.showError(error.localizedDescription)
                self.noPhotoLabel.isHidden = false
                self.enableUi(true)
            } else {
                if let photos = photos?.results, photos.count > 0 {
                    for photo in photos {
                        self.persistPhoto(placePhoto: photo)
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
        let camera = GMSCameraPosition.camera(withLatitude: pin.latitude,
                                              longitude: pin.longitude,
                                              zoom: zoom)
        
        map.camera = camera
        
        addPinToMap(with: CLLocationCoordinate2D(latitude: pin.latitude, longitude: pin.longitude))
    }
    
    func persistPhoto(placePhoto: GMSPlacePhotoMetadata) {
        GMSPlacesClient.shared().loadPlacePhoto(placePhoto, callback: {
            (placePhoto, error) -> Void in
            if let error = error {
                // TODO: handle the error.
                print("Error: \(error.localizedDescription)")
            } else {
                let photo = Photos(context: self.dataController.viewContext)
                
                photo.pin = self.pin
                photo.type = Int16(self.fetchType)
                photo.title = self.pin.name
                photo.imageData = UIImagePNGRepresentation(placePhoto!)
                try? self.dataController.save()
            }
        })
    }
    
    func persistPhoto(photo: [String: AnyObject]) {
        if let title = photo[FlickrConstants.FlickrResponseKeys.Title] as? String,
            /* Does the photo have a key for 'url_l'? */
            let imageUrlString = photo[FlickrConstants.FlickrResponseKeys.ImageSize] as? String {
            
            let photo = Photos(context: dataController.viewContext)
            
            photo.pin = self.pin
            photo.type = Int16(fetchType)
            photo.title = title
            photo.imageUrl = imageUrlString
            
            try? dataController.save()
            
            let photoId = photo.objectID
            
            if let url = URL(string: imageUrlString), let backgroundContext:NSManagedObjectContext = dataController?.backgroundContext {
                backgroundContext.perform {
                    let backgroundPhoto = backgroundContext.object(with: photoId) as! Photos
                    try? backgroundPhoto.imageData = Data(contentsOf: url)
                    try? backgroundContext.save()
                }
            }
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
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: Constants.ALBUM_CELL_REUSE_ID, for: indexPath) as! AlbumCollectionViewCell
        
        cell.locationImage.image = UIImage(named: Constants.CoreData.PLACEHOLDER_IMAGE)
        
        if let imageData = dataSource.object(at: indexPath).imageData {
            cell.locationImage.image = UIImage(data: imageData)
        } else {
            
            if let imagePath = dataSource.object(at: indexPath).imageUrl {
                
                WebClient.sharedInstance.downloadImage(imagePath: imagePath) { (imageData, error) in
                    if let error = error {
                        self.showError(error)
                    } else {
                        guard let imageData = imageData else {
                            self.showError("Could not download image")
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
            showToast(message: "Please wait for photo downloads to finish")
            return
        }
        
        performSegue(withIdentifier: Constants.PHOTO_DETAIL_SEGUE_ID, sender: photo)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.PHOTO_DETAIL_SEGUE_ID {
            let destinationViewController = segue.destination as! ExplorePhotosDetailViewController
            destinationViewController.photo = sender as! Photos
        }
    }
}

extension ExplorePhotosViewController {
    
    func showToast(message : String) {
        let toastLabel = UILabel(frame: CGRect(x: self.view.frame.size.width/2 - 200, y: self.view.frame.size.height-100, width: 400, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.textAlignment = .center;
        toastLabel.font = UIFont(name: "Montserrat-Light", size: 12.0)
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: {
            toastLabel.alpha = 0.0
        }, completion: {(isCompleted) in
            toastLabel.removeFromSuperview()
        })
    } }
