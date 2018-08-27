//
//  PlanViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CoreData
import Firebase
import UIKit

class PlanViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var tableView: UITableView!
    
    var firestoreDbReference: CollectionReference!
    var upcomingTrips: [Plan] = []
    var pastTrips: [Plan] = []
    var pins: [Pin] = []
    
    var dataController: DataController!
    var fetchedResultsController: NSFetchedResultsController<Pin>!
    
    @IBOutlet weak var add: UIBarButtonItem! {
        didSet {
            UiUtils.setImage("add", for: add)
        }
    }
    
    @IBOutlet weak var delete: UIBarButtonItem! {
        didSet {
            UiUtils.setImage("trash", for: delete)
        }
    }
    
    @IBAction func deletePlan(_ sender: Any) {
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        // Do any additional setup after loading the view.
        initResultsController()
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS)
        
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
        let sortDescriptor = NSSortDescriptor(key: Constants.CoreData.SORT_KEY, ascending: false)
        fetchRequest.sortDescriptors = [sortDescriptor]
        fetchedResultsController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: dataController.viewContext, sectionNameKeyPath: nil, cacheName: Constants.CoreData.CACHE_NAME_PINS)
    }
    
    func fetchPins() {
        do {
            try fetchedResultsController.performFetch()
            if let result = fetchedResultsController.fetchedObjects {
                pins = result
            }
        } catch {
            fatalError("The fetch could not be performed: \(error.localizedDescription)")
        }
    }
    
    func fetchPlans() {
        firestoreDbReference.getDocuments() { (querySnapshot, error) in
            if let error = error {
                print("Error getting documents: \(error)")
            } else {
                
                self.upcomingTrips.removeAll()
                self.pastTrips.removeAll()
                
                for document in querySnapshot!.documents {
                    print("\(document.documentID) => \(document.data())")
                    
                    let name = document.data()[FirestoreConstants.Ids.Plan.NAME] as? String
                    let startDate = document.data()[FirestoreConstants.Ids.Plan.START_DATE] as? Timestamp
                    let endDate = document.data()[FirestoreConstants.Ids.Plan.END_DATE] as? Timestamp
                    
                    if let name = name, let startDate = startDate, let endDate = endDate {
                        let plan = Plan(name: name, startDate: startDate, endDate: endDate)
                        
                        if endDate.dateValue() > Date() {
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
        if segue.identifier == Constants.SEGUES.ADD_PLAN_SEGUE_ID {
            let controller = segue.destination as! AddPlanViewController
            controller.pins = pins
        } else if segue.identifier == Constants.SEGUES.PLAN_DETAIL_SEGUE_ID {
            let controller = segue.destination as! PlanDetailViewController
            let indexPath = sender as! IndexPath
            let plan = getSectionArray(for: indexPath.section)[indexPath.row]
            controller.plan = plan
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
        let cell = tableView.dequeueReusableCell(withIdentifier: Constants.REUSE_IDS.PLAN_CELL_REUSE_ID)!
        
        let plan = getSectionArray(for: indexPath.section)[indexPath.row]
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd.mm.yyyy"
        
        cell.textLabel?.text = plan.name
        cell.detailTextLabel?.text = dateFormatter.string(from: plan.startDate.dateValue()) + " - " + dateFormatter.string(from: plan.endDate.dateValue())
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        performSegue(withIdentifier: Constants.SEGUES.PLAN_DETAIL_SEGUE_ID, sender: indexPath)
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
            return PlanConstants.Trips.TripTitles.UPCOMING.rawValue
        } else {
            return PlanConstants.Trips.TripTitles.PAST.rawValue
        }
    }
    
    func tableView(_ tableView: UITableView, titleForFooterInSection section: Int) -> String? {
        return "\(getSectionArray(for: section).count) Trips"
    }
}
