//
//  PlanViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Firebase
import FirebaseStorage
import UIKit

class PlanViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView: UITableView!
    
    var dataController: DataController!
    
    var firestoreDbReference: CollectionReference!
    var upcomingTrips: [Plan] = []
    var pastTrips: [Plan] = []
    var pins: [Pin] = []
    
    var fetchedResultsController: NSFetchedResultsController<Pin>!
    let imageCache = GlobalCache.imageCache
    
    @IBOutlet weak var add: UIBarButtonItem! {
        didSet {
            UiUtils.setImage("add", for: add)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "plan".localized()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        initResultsController()
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans)
        
        fetchPins()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        fetchPlans()
    }
    
    deinit {
        firestoreDbReference = nil
    }
    
    func initResultsController() {
        let fetchRequest: NSFetchRequest<Pin> = Pin.fetchRequest()
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.sortKey, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: Constants.CoreData.cacheNamePins)
    }
    
    func fetchPins() {
        do {
            try fetchedResultsController.performFetch()
            if let result = fetchedResultsController.fetchedObjects {
                pins = result
            }
        } catch {
            UiUtils.showError(error.localizedDescription, controller: self)
        }
    }
    
    func fetchPlans() {
        firestoreDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                debugPrint("Error getting documents: \(error)")
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                self.upcomingTrips.removeAll()
                self.pastTrips.removeAll()
                
                for document in querySnapshot!.documents {
                    debugPrint("\(document.documentID) => \(document.data())")
                    
                    let name = document.data()[FirestoreConstants.Ids.Plan.name] as? String
                    let pinName = document.data()[FirestoreConstants.Ids.Plan.pinName] as? String
                    let startDate = document.data()[FirestoreConstants.Ids.Plan.startDate] as? Timestamp
                    let endDate = document.data()[FirestoreConstants.Ids.Plan.endDate] as? Timestamp
                    let imageRef = document.data()[FirestoreConstants.Ids.Plan.imageReference] as? String
                    
                    if let name = name, let pinName = pinName, let startDate = startDate, let endDate = endDate {
                        var imagePath = ""
                        if let imageRef = imageRef {
                            imagePath = imageRef
                        }
                        
                        let plan = Plan(name: name, originalName: pinName, startDate: startDate, endDate: endDate, imageRef: imagePath)
                        
                        //load subdocuments of plan:
                        DispatchQueue.main.async {
                            plan.loadPlannables() { (error) in
                                if let error = error {
                                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                                } else {
                                    self.tableView.reloadData()
                                }
                            }
                        }
                        
                        if endDate.compare(Timestamp(date: Date())).rawValue > 0 {
                            self.upcomingTrips.append(plan)
                        } else {
                            self.pastTrips.append(plan)
                        }
                    }
                }
            }
            
            DispatchQueue.main.async {
                self.tableView.reloadData()
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.addPlan {
            let controller = segue.destination as! AddPlanViewController
            controller.pins = pins
        } else if segue.identifier == Constants.Segues.planDetail {
            let indexPath = sender as! IndexPath
            let plan = getSectionArray(for: indexPath.section)[indexPath.row]
            
            let controller = segue.destination as! PlanDetailViewController
            
            controller.plan = plan
            controller.pins = pins
            controller.dataController = dataController
            controller.firestorePlanDbReference = firestoreDbReference
        } else if segue.identifier == Constants.Segues.exploreDetail {
            let indexPath = sender as! IndexPath
            let plan = getSectionArray(for: indexPath.section)[indexPath.row]
            
            let controller = segue.destination as! UITabBarController
            let detailTargetController = controller.viewControllers![0] as! ExploreDetailViewController
            let photosTargetController = controller.viewControllers![1] as! ExplorePhotosViewController
            let infoTargetController = controller.viewControllers![2] as! ExploreInfoViewController
            
            let pin = CoreDataClient.sharedInstance.findPinByName(plan.pinName, pins: pins)
            
            detailTargetController.pin = pin
            detailTargetController.dataController = dataController
            
            photosTargetController.pin = pin
            photosTargetController.dataController = dataController
            
            infoTargetController.pin = pin
        }
    }
}

extension PlanViewController {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 2 //upcoming & past trips
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return getSectionArray(for: section).count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Constants.ReuseIds.planCell)!
        
        cell.imageView?.image = nil
        
        let plan = getSectionArray(for: indexPath.section)[indexPath.row]
        
        cell.textLabel?.text = plan.name
        cell.detailTextLabel?.text = FormatUtils.formatTimestampRangeForDisplay(begin: plan.startDate, end: plan.endDate)
        cell.imageView?.image = defaultImage()
        UiUtils.resizeImage(cell: cell)
        
        if !plan.imageRef.isEmpty { //Is an image available in storage?
            
            if let cachedImage = imageCache.object(forKey: plan.imageRef as NSString) {
                cell.imageView?.image = cachedImage
                UiUtils.resizeImage(cell: cell)
            } else {
                let storageImageRef = Storage.storage().reference(forURL: plan.imageRef)
                
                storageImageRef.getData(maxSize: 2 * 1024 * 1024) { (data, error) in //max 2MB
                    if let error = error {
                        UiUtils.showToast(message: error.localizedDescription, view: self.view)
                        return
                    }
                    
                    guard let data = data, let image = UIImage(data: data) else {
                        UiUtils.showToast(message: "noImageData".localized(), view: self.view)
                        return
                    }
                    
                    cell.imageView?.image = image
                    self.imageCache.setObject(cell.imageView!.image!, forKey: plan.imageRef + "-originalsize" as NSString)
                    
                    UiUtils.resizeImage(cell: cell)
                    
                    self.imageCache.setObject(cell.imageView!.image!, forKey: plan.imageRef as NSString)
                }
            }
        }
        
        return cell
    }
    
    func defaultImage() -> UIImage {
        return UIImage(named: "placeholder")!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let alert = UIAlertController(title: "chooseAction".localized(), message: nil, preferredStyle: .alert)
        
        let plan = self.getSectionArray(for: indexPath.section)[indexPath.row]
        let imageRef = plan.imageRef
        
        alert.addAction(UIAlertAction(title: "show".localized(), style: .default, handler: { _ in
            DispatchQueue.main.async { //need to dispatch async because of swift bug (otherwise segue takes some seconds)
                self.performSegue(withIdentifier: Constants.Segues.planDetail, sender: indexPath)
            }
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "showDetails".localized(), style: .default, handler: { _ in
            DispatchQueue.main.async { //need to dispatch async because of swift bug (otherwise segue takes some seconds)
                self.performSegue(withIdentifier: Constants.Segues.exploreDetail, sender: indexPath)
            }
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "delete".localized(), style: .default, handler: { _ in
            self.firestoreDbReference.document(plan.pinName).delete() { error in
                if let error = error {
                    UiUtils.showError(error.localizedDescription, controller: self)
                } else {
                    debugPrint("Document successfully removed!")
                    self.remove(at: indexPath)
                    self.tableView.reloadData()
                    
                    //delete plan photo in firebase storage
                    if !imageRef.isEmpty {
                        let storageImageRef = Storage.storage().reference(forURL: imageRef)
                        storageImageRef.delete(completion: { (error) in
                            if let error = error {
                                UiUtils.showToast(message: error.localizedDescription, view: self.view)
                            }
                        })
                    }
                    
                    //delete from Firestore
                    //subdocuments are not delete automatically, so we have to do that too in case of success
                    plan.deleteSubDocuments() { (error) in
                        if let error = error {
                            UiUtils.showToast(message: error.localizedDescription, view: self.view)
                        }
                    }
                }
            }
            
            self.dismiss(animated: true, completion: nil)
        }))
        
        alert.addAction(UIAlertAction(title: "cancel".localized(), style: .default, handler: { _ in
            self.dismiss(animated: true, completion: nil)
        }))
        
        present(alert, animated: true, completion: nil)
    }
    
    func remove(at indexPath: IndexPath) {
        if indexPath.section == 0 {
            upcomingTrips.remove(at: indexPath.row)
        } else {
            pastTrips.remove(at: indexPath.row)
        }
    }
    
    func getSectionArray(for section: Int) -> [Plan] {
        if section == 0 {
            return upcomingTrips
        } else {
            return pastTrips
        }
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return PlanConstants.Trips.TripTitles.upcoming.rawValue.localized()
        } else {
            return PlanConstants.Trips.TripTitles.past.rawValue.localized()
        }
    }
    
    func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        return String(format: "numberOfTrips".localized(), getSectionArray(for: section).count)
    }
}
