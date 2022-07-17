//
//  RememberViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 20.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Firebase
import FirebaseStorage
import UIKit

class RememberViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    var firestorePlanDbReference: CollectionReference!
    var rememberTrips: [Plan] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "remember".localized()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        firestorePlanDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        fetchPlans()
    }
    
    deinit {
        firestorePlanDbReference = nil
    }
    
    func fetchPlans() {
        firestorePlanDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                
                self.rememberTrips.removeAll()
                
                for document in querySnapshot!.documents {
                    print("\(document.documentID) => \(document.data())")
                    
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
                        
                        //remember photos should be storable from the beginning of the trip
                        if startDate.compare(Timestamp(date: Date())).rawValue <= 0 {
                            self.rememberTrips.append(plan)
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
        if segue.identifier == Constants.Segues.rememberDetail {
            let controller = segue.destination as! RememberDetailViewController
            let indexPath = sender as! IndexPath
            let plan = rememberTrips[indexPath.row]
            controller.plan = plan
            controller.firestorePlanDbReference = firestorePlanDbReference
        }
    }
}

extension RememberViewController: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1 //past trips. Default is already 1. Just to make it clear.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return rememberTrips.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Constants.ReuseIds.planCell)!
        
        let plan = rememberTrips[indexPath.row]
        
        cell.textLabel?.text = plan.name
        cell.detailTextLabel?.text = FormatUtils.formatTimestampRangeForDisplay(begin: plan.startDate, end: plan.endDate)
        
        if !plan.imageRef.isEmpty { //Is an image available in storage?
            let storageImageRef = Storage.storage().reference(forURL: plan.imageRef)
            storageImageRef.getData(maxSize: 1 * 1024 * 512) { (data, error) in //max 0.5 MB for thumbnail
                if let error = error {
                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                    return
                }
                
                guard let data = data else {
                    UiUtils.showToast(message: "noImageData".localized(), view: self.view)
                    return
                }
                
                cell.imageView?.image = UIImage(data: data)
                UiUtils.resizeImage(cell: cell)
            }
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: Constants.Segues.rememberDetail, sender: indexPath)
    }
    
    func tableView(_ tableView: UITableView, didEndDisplaying cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cell.backgroundColor = UIColor.darkGray
    }
    
}
