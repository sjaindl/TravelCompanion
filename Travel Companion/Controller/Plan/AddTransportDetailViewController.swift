//
//  AddFlightDetailViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 10.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import FirebaseFirestore
import UIKit

class AddTransportDetailViewController: UITableViewController {
    
    var weekDayToDayFlagMap: [Int: Int] =  [1: 0x01, /* Sunday */
                                2: 0x02, /* Monday */
                                3: 0x04, /* Tuesday */
                                4: 0x08, /* Wednesday */
                                5: 0x10, /* Thursday */
                                6: 0x20, /* Friday */
                                7: 0x40] /* Saturday */
    
    var firestoreDbReference: CollectionReference!
    var searchResponse: SearchResponse!
    var planDetailController: PlanDetailViewController!
    var transportDelegate: AddTransportDelegate!
    
    var date = Date()
    
    var plan: Plan!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = String(format: "addTranport".localized(), transportDelegate.description())
        
        initCellData()
    }
    
    func initCellData() {
        transportDelegate.initCellData(searchResponse: searchResponse, date: date)
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return transportDelegate.numberOfSections(in: tableView)
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return transportDelegate.tableView(tableView, numberOfRowsInSection: section)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return transportDelegate.tableView(tableView, cellForRowAt: indexPath, searchResponse: searchResponse)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        transportDelegate.tableView(tableView, didSelectRowAt: indexPath, searchResponse: searchResponse, date: date, firestoreDbReference: firestoreDbReference, plan: plan, controller: self, popToController: self.planDetailController)
    }
}
