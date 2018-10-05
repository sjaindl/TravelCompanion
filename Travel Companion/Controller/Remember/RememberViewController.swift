//
//  RememberViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 20.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Firebase
import UIKit

class RememberViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var tableView: UITableView!
    
    var firestorePlanDbReference: CollectionReference!
    var pastTrips: [Plan] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "Remember"
        
        tableView.delegate = self
        tableView.dataSource = self
        
        // Do any additional setup after loading the view.
        firestorePlanDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS)
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
                
                self.pastTrips.removeAll()
                
                for document in querySnapshot!.documents {
                    print("\(document.documentID) => \(document.data())")
                    
                    let name = document.data()[FirestoreConstants.Ids.Plan.NAME] as? String
                    let pinName = document.data()[FirestoreConstants.Ids.Plan.PIN_NAME] as? String
                    let startDate = document.data()[FirestoreConstants.Ids.Plan.START_DATE] as? Timestamp
                    let endDate = document.data()[FirestoreConstants.Ids.Plan.END_DATE] as? Timestamp
                    let imageRef = document.data()[FirestoreConstants.Ids.Plan.IMAGE_REFERENCE] as? String
                    
                    if let name = name, let pinName = pinName, let startDate = startDate, let endDate = endDate {
                        var imagePath = ""
                        if let imageRef = imageRef {
                            imagePath = imageRef
                        }
                        
                        let plan = Plan(name: name, originalName: pinName, startDate: startDate, endDate: endDate, imageRef: imagePath)
                        
                        if endDate.compare(Timestamp(date: Date())).rawValue <= 0 {
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
        if segue.identifier == Constants.SEGUES.REMEMBER_DETAIL_SEGUE_ID {
            let controller = segue.destination as! RememberDetailViewController
            let indexPath = sender as! IndexPath
            let plan = pastTrips[indexPath.row]
            controller.plan = plan
            controller.firestorePlanDbReference = firestorePlanDbReference
        }
    }
}

extension RememberViewController {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1 //past trips
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return pastTrips.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: Constants.REUSE_IDS.PLAN_CELL_REUSE_ID)!
        
        let plan = pastTrips[indexPath.row]
        
        cell.textLabel?.text = plan.name
        cell.detailTextLabel?.text = UiUtils.formatTimestampRangeForDisplay(begin: plan.startDate, end: plan.endDate)
        
        if !plan.imageRef.isEmpty { //Is an image available in storage?
            let storageImageRef = Storage.storage().reference(forURL: plan.imageRef)
            storageImageRef.getData(maxSize: 1 * 1024 * 512) { (data, error) in //max 0.5 MB for thumbnail
                if let error = error {
                    UiUtils.showToast(message: error.localizedDescription, view: self.view)
                    return
                }
                
                guard let data = data else {
                    UiUtils.showToast(message: "No image data available", view: self.view)
                    return
                }
                
                cell.imageView?.image = UIImage(data: data)
            }
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: Constants.SEGUES.REMEMBER_DETAIL_SEGUE_ID, sender: indexPath)
    }
}
