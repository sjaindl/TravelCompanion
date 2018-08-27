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
    
    var plan: Plan!
    
    var firestoreFligthDbReference: CollectionReference!
    var firestorePublicTransportDbReference: CollectionReference!
    var firestoreHotelDbReference: CollectionReference!
    var firestoreAttractionDbReference: CollectionReference!
    
    var fligths: [Plan] = []
    var publicTransport: [Plan] = []
    var hotels: [Plan] = []
    var restaurants: [Plan] = []
    var attractions: [Plan] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tripName.text = plan.name
        date.text = "\(plan.startDate.dateValue()) - \(plan.endDate.dateValue())"
        
        //test:
        fligths.append(plan)
        publicTransport.append(plan)
        restaurants.append(plan)
        
        tableView.delegate = self
        tableView.dataSource = self
        
        // Do any additional setup after loading the view.
        
        initFirebaseReferences()
    }
    
    deinit {
        firestoreFligthDbReference = nil
        firestorePublicTransportDbReference = nil
        firestoreHotelDbReference = nil
        firestoreAttractionDbReference = nil
    }
    
    func initFirebaseReferences() {
        firestoreFligthDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.FLIGTHS)
        
        firestorePublicTransportDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.PUBLIC_TRANSPORT)
        
        firestoreHotelDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.HOTELS)
        
        firestoreAttractionDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.RESTAURANTS)
        
        firestoreAttractionDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS).document(plan.name).collection(FirestoreConstants.Collections.ATTRACTIONS)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

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
