//
//  AddTransportDateViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 27.06.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class AddTransportDateViewController: UIViewController {
    
    var firestoreDbReference: CollectionReference!
    var planDetailController: PlanDetailViewController!
    var transportDelegate: AddTransportDelegate!
    var transportSearchDelegate: AddTransportSearchDelegate!
    
    var plan: Plan!
    var transport: Transport!
    
    @IBOutlet weak var date: UIDatePicker!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = String(format: "addTransport".localized(), transportDelegate.description())
        
        date.datePickerMode = .date
        UiUtils.layoutDatePicker(date)
    }
    
    @IBAction func search(_ sender: UIButton) {
        searchForTransport()
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.planAddTransportDetail {
            let controller = segue.destination as! AddTransportDetailViewController
            controller.searchResponse = sender as? SearchResponse
            controller.transportDelegate = transportDelegate
            controller.date = date.date
            controller.firestoreDbReference = firestoreDbReference
            controller.planDetailController = planDetailController
            controller.plan = plan
        }
    }
    
    func searchForTransport() {
        guard let origin = transport.origin, let destination = transport.destination else {
            debugPrint("Something is wrong with origin or destination")
            return
        }
        
        let queryItems = transportSearchDelegate.buildSearchQueryItems(origin: origin, destination: destination)
        
        Rome2RioClient.sharedInstance.search(with: queryItems) { (error, searchResponse) in
            if let error = error {
                DispatchQueue.main.async {
                    UiUtils.showError(error, controller: self)
                }
                return
            }
            
            guard let searchResponse = searchResponse else {
                DispatchQueue.main.async {
                    UiUtils.showError("noTransportData".localized(), controller: self)
                }
                return
            }
            
            debugPrint(searchResponse)
            
            DispatchQueue.main.async {
                self.performSegue(withIdentifier: Constants.Segues.planAddTransportDetail, sender: searchResponse)
            }
        }
    }

}
