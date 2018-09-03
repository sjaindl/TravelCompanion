//
//  PlanDetailViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class PlanDetailViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tripName: UILabel!
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var date: UILabel!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var imageWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var imageHeightConstraint: NSLayoutConstraint!
    
    var plan: Plan!
    var pins: [Pin]!
    var dataController: DataController!
    
    var firestorePlanDbReference: CollectionReference!
    
    var firestoreFligthDbReference: CollectionReference!
    var firestorePublicTransportDbReference: CollectionReference!
    var firestoreHotelDbReference: CollectionReference!
    var firestoreRestaurantDbReference: CollectionReference!
    var firestoreAttractionDbReference: CollectionReference!
    
    var storageRef: StorageReference!
    
    var fligths: [Plan] = []
    var publicTransport: [Plan] = []
    var hotels: [Plan] = []
    var restaurants: [Plan] = []
    var attractions: [Plan] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tripName.text = plan.name
        date.text = UiUtils.formatTimestampRangeForDisplay(begin: plan.startDate, end: plan.endDate)
        
        //test:
        fligths.append(plan)
        publicTransport.append(plan)
        restaurants.append(plan)
        
        tableView.delegate = self
        tableView.dataSource = self
        
        // Do any additional setup after loading the view.
        addGestureRecognizer(selector: #selector(chooseImage), view: image)
        
        configureDatabase()
        configureStorage()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        loadImageIfAvailable()
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        checkDeviceSize()
    }
    
    func checkDeviceSize() {
        if UIDevice.current.orientation.isLandscape {
            setImageSize(100)
        } else {
            setImageSize(200)
        }
    }
    
    func setImageSize(_ size: CGFloat) {
        imageWidthConstraint.constant = size
        imageHeightConstraint.constant = size
    }
    
    func loadImageIfAvailable() {
        if let data = plan.imageData { //Has an image been chosen?
            image.image = UIImage(data: data)
            persistPhoto(photoData: data)
            checkDeviceSize()
        } else if !plan.imageRef.isEmpty { //Is an image available in storage?
            let storageImageRef = Storage.storage().reference(forURL: plan.imageRef)
            storageImageRef.getData(maxSize: 1 * 1024 * 1024) { (data, error) in
                if let error = error {
                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                    return
                }
                
                guard let data = data else {
                    UiUtils.showToast(message: "No image data available", view: self.view)
                    return
                }
                
                self.image.image = UIImage(data: data)
                self.checkDeviceSize()
            }
        }
    }
    
    func configureStorage() { 
        storageRef = Storage.storage().reference()
    }
    
    func addGestureRecognizer(selector: Selector?, view: UIView) {
        let gestureRecognizer = UITapGestureRecognizer(target: self, action: selector)
        view.isUserInteractionEnabled = true
        view.addGestureRecognizer(gestureRecognizer)
    }
    
    deinit {
        firestoreFligthDbReference = nil
        firestorePublicTransportDbReference = nil
        firestoreHotelDbReference = nil
        firestoreRestaurantDbReference = nil
        firestoreAttractionDbReference = nil
        
        firestorePlanDbReference = nil
    }
    
    @objc
    func chooseImage() {
        performSegue(withIdentifier: Constants.SEGUES.PLAN_CHOOSE_PHOTO_SEGUE_ID, sender: nil)
    }
    
    func configureDatabase() {
        firestoreFligthDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.FLIGTHS)
        
        firestorePublicTransportDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.PUBLIC_TRANSPORT)
        
        firestoreHotelDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.HOTELS)
        
        firestoreRestaurantDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.RESTAURANTS)
        
        firestoreAttractionDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.ATTRACTIONS)
    }

        override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
            if segue.identifier == Constants.SEGUES.PLAN_CHOOSE_PHOTO_SEGUE_ID {
                let controller = segue.destination as! ExplorePhotosViewController
                let pin = CoreDataClient.sharedInstance.findPinByName(plan.pinName, pins: pins)
                controller.pin = pin
                controller.dataController = dataController
                controller.choosePhoto = true
                controller.plan = plan
            }
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }

    func persistPhoto(photoData: Data) {
        let path = FirestoreClient.storageByPath(path: FirestoreConstants.Collections.PLANS, fileName: plan.pinName)
        FirestoreClient.storePhoto(storageRef: storageRef, path: path, photoData: photoData) { (metadata, error) in
            if let error = error {
                UiUtils.showToast(message: error.localizedDescription, view: self.view)
                return
            }
            
            guard let storagePath = metadata?.path else {
                UiUtils.showToast(message: "Could not save image", view: self.view)
                return
            }
            
            self.plan.imageRef = self.storageRef.child(storagePath).description
            self.updatePlan()
        }
    }
    
    func updatePlan() {
        FirestoreClient.addData(collectionReference: firestorePlanDbReference, documentName: plan.name, data: [
            FirestoreConstants.Ids.Plan.NAME: plan.name,
            FirestoreConstants.Ids.Plan.PIN_NAME: plan.pinName,
            FirestoreConstants.Ids.Plan.START_DATE: plan.startDate,
            FirestoreConstants.Ids.Plan.END_DATE: plan.endDate,
            FirestoreConstants.Ids.Plan.IMAGE_REFERENCE: plan.imageRef
        ]) { (error) in
            if let error = error {
                UiUtils.showToast(message: "Error adding document: \(error)", view: self.view)
                return
            }
        }
    }
}

extension PlanDetailViewController {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 5 //fligths, public transport, hotels, restaurants, attractions
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return getSectionArray(for: section).count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Constants.REUSE_IDS.PLAN_DETAIL_CELL_REUSE_ID)!
        
        let plan = getSectionArray(for: indexPath.section)[indexPath.row]
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.mm.yyyy"
        
        cell.textLabel?.text = plan.name
        cell.detailTextLabel?.text = dateFormatter.string(from: plan.startDate.dateValue()) + " - " + dateFormatter.string(from: plan.endDate.dateValue())
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //TODO segue
    }
    
    func getSectionArray(for section: Int) -> [Plan] {
        //TODO: Interface mit func title/subtitle
        if section == 0 {
            return fligths
        } else if section == 1 {
            return publicTransport
        } else if section == 2 {
            return hotels
        } else if section == 3 {
            return restaurants
        } else {
            return attractions
        } 
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return PlanConstants.TripDetails.TripTitles.FLIGHTS.rawValue
        } else if section == 1 {
            return PlanConstants.TripDetails.TripTitles.PUBLIC_TRANSPORT.rawValue
        } else if section == 2 {
            return PlanConstants.TripDetails.TripTitles.HOTELS.rawValue
        } else if section == 3 {
            return PlanConstants.TripDetails.TripTitles.RESTAURANTS.rawValue
        } else {
            return PlanConstants.TripDetails.TripTitles.ATTRACTIONS.rawValue
        }
    }
}
