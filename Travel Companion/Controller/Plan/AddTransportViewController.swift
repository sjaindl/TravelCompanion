//
//  AddFlightViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 05.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class AddTransportViewController: UIViewController, UITextFieldDelegate {

    var firestoreDbReference: CollectionReference!
    var planDetailController: PlanDetailViewController!
    var transportDelegate: AddTransportDelegate!
    
    @IBOutlet weak var origin: SearchTextField!
    @IBOutlet weak var destination: SearchTextField!
    @IBOutlet weak var date: UIDatePicker!
    
    var plan: Plan!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "Add \(transportDelegate.description())"
        
        origin.delegate = self
        destination.delegate = self
        
        date.datePickerMode = .date
    }

    @IBAction func search(_ sender: Any) {
        searchForTransport()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.SEGUES.PLAN_ADD_TRANSPORT_DETAIL {
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
        Rome2RioClient.sharedInstance.search(origin: origin.text!, destination: destination.text!, with: transportDelegate) { (error, searchResponse) in
            if let error = error {
                DispatchQueue.main.async {
                    UiUtils.showError(error, controller: self)
                }
                return
            }
            
            guard let searchResponse = searchResponse else {
                DispatchQueue.main.async {
                    UiUtils.showError("No transport data available", controller: self)
                }
                return
            }
            
            debugPrint(searchResponse)
            
            DispatchQueue.main.async {
                self.performSegue(withIdentifier: Constants.SEGUES.PLAN_ADD_TRANSPORT_DETAIL, sender: searchResponse)
            }
        }
    }
}

extension AddTransportViewController {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        guard FirestoreRemoteConfig.sharedInstance.transportSearchAutocomplete else {
            debugPrint("Autocompletion is disabled.")
            return true
        }
        
        Rome2RioClient.sharedInstance.autocomplete(with: textField.text! + string) { (error, autoCompleteResponse) in
            
            guard error == nil else {
                debugPrint("autocompletion threw an error.. skip it.")
                return
            }
            
            var filterStrings: [String] = []
            
            for place in (autoCompleteResponse?.places)! {
                filterStrings.append(place.longName)
            }
            
            if filterStrings.count > 0, let searchTextField = textField as? SearchTextField {
                searchTextField.filterStrings(filterStrings)
            }
        }
        
        return true
    }
}
