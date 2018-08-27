//
//  AddPlanViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 26.08.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class AddPlanViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate {
    
    @IBOutlet weak var destinationPicker: UIPickerView!
    @IBOutlet weak var destinationText: UITextField!
    @IBOutlet weak var addTripButton: UIButton!
    @IBOutlet weak var startDate: UIDatePicker!
    @IBOutlet weak var endDate: UIDatePicker!
    
    var pins: [Pin] = []
    var firestoreDbReference: CollectionReference!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        destinationPicker.delegate = self
        destinationPicker.dataSource = self
        destinationText.delegate = self
        
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.PLANS)
    }
    
    @IBAction func addPlan(_ sender: Any) {
        let plan = Plan(name: destinationText.text!, startDate: Timestamp(date: startDate.date), endDate: Timestamp(date: endDate.date), imageRef: "")
        persistPlan(of: plan)
        
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func cancel(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    func persistPlan(of plan: Plan) {
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: plan.name, data: [
            FirestoreConstants.Ids.Plan.NAME: plan.name,
            FirestoreConstants.Ids.Plan.START_DATE: plan.startDate,
            FirestoreConstants.Ids.Plan.END_DATE: plan.endDate
        ]) { (error) in
            if let error = error {
                print("Error adding document: \(error)")
            } else {
                print("Document added")
            }
        }
    }
}

extension AddPlanViewController {
    
    public func numberOfComponents(in pickerView: UIPickerView) -> Int{
        return 1
    }
    
    public func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int{
        return pins.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        
        self.view.endEditing(true)
        return pins[row].name
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        
        self.destinationText.text = self.pins[row].name
//        self.destinationPicker.isHidden = true
    }
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        
        if textField == self.destinationText {
            self.destinationPicker.isHidden = false
            //if you don't want the users to se the keyboard type:
            
            textField.endEditing(true)
        }
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        //TODO: check whether pin with name exists -> if so, grey out add button + toast
        
    }
}
