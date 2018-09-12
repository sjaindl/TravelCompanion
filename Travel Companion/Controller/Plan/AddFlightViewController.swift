//
//  AddFlightViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 05.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class AddFlightViewController: UIViewController, UITextFieldDelegate {

    var firestoreFligthDbReference: CollectionReference!
    var planDetailController: PlanDetailViewController!
    
    @IBOutlet weak var origin: SearchTextField!
    @IBOutlet weak var destination: SearchTextField!
    @IBOutlet weak var date: UIDatePicker!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        origin.delegate = self
        destination.delegate = self
        
        date.datePickerMode = .date
    }

    @IBAction func search(_ sender: Any) {
        searchFlight()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.SEGUES.PLAN_ADD_FLIGHT_DETAIL {
            let controller = segue.destination as! AddFlightDetailViewController
            controller.searchResponse = sender as! SearchResponse
            controller.date = date.date
            controller.firestoreFligthDbReference = firestoreFligthDbReference
            controller.planDetailController = planDetailController
        }
    }
    
    func searchFlight() {
        Rome2RioClient.sharedInstance.search(origin: origin.text!, destination: destination.text!, flight: true) { (error, searchResponse) in
            if let error = error {
                UiUtils.showToast(message: error, view: self.view)
                return
            }
            
            guard let searchResponse = searchResponse else {
                UiUtils.showToast(message: "No flights available", view: self.view)
                return
            }
            
            print(searchResponse)
            DispatchQueue.main.async {
                self.performSegue(withIdentifier: Constants.SEGUES.PLAN_ADD_FLIGHT_DETAIL, sender: searchResponse)
            }
        }
    }
}

extension AddFlightViewController {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        Rome2RioClient.sharedInstance.autocomplete(with: textField.text! + string) { (error, autoCompleteResponse) in
            
            guard error == nil else {
                debugPrint("autocompletion threw an error.. skip it.")
                return
            }
            
            var filterStrings: [String] = []
            
            for place in (autoCompleteResponse?.places)! {
                filterStrings.append(place.longName)
            }
            
            if filterStrings.count > 0 {
                self.origin.filterStrings(filterStrings)
            }
        }
        return true
    }
}
