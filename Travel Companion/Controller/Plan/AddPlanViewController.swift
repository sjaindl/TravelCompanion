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

    enum ButtonState: Int {
        case destination = 0
        case name = 1
        case startDate = 2
        case endDate = 3
    }
    
    @IBOutlet weak var destinationLabel: UILabel!
    @IBOutlet weak var destinationPicker: UIPickerView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var destinationText: UITextField!
    @IBOutlet weak var addTripButton: UIButton!
    @IBOutlet weak var startDateLabel: UILabel!
    @IBOutlet weak var startDate: UIDatePicker!
    @IBOutlet weak var endDateLabel: UILabel!
    @IBOutlet weak var endDate: UIDatePicker!
    @IBOutlet weak var addTrip: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    
    var pins: [Pin] = []
    var selectedOriginalPinName: String?
    var firestoreDbReference: CollectionReference!
    var dataController: DataController!
    
    var buttonState = ButtonState.destination
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "addPlan".localized()
        
        destinationPicker.delegate = self
        destinationPicker.dataSource = self
        destinationText.delegate = self
        
        if pins.count > 0 {
            destinationText.text = pins[0].name
            selectedOriginalPinName = pins[0].name
        }
        
        setButtonEnabledState()
        
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans)
        
        UiUtils.layoutDatePicker(startDate)
        UiUtils.layoutDatePicker(endDate)
        
        layoutButton(with: "next".localized(), showDestination: true, showName: false, showStartDate: false, showEndDate: false)
    }
    
    func layoutButton(with text: String, showDestination: Bool, showName: Bool, showStartDate: Bool, showEndDate: Bool) {
        addTrip.setTitle(text, for: .normal)
        
        destinationLabel.isHidden = !showDestination
        destinationPicker.isHidden = !showDestination
        nameLabel.isHidden = !showName
        destinationText.isHidden = !showName
        startDate.isHidden = !showStartDate
        startDateLabel.isHidden = !showStartDate
        endDate.isHidden = !showEndDate
        endDateLabel.isHidden = !showEndDate
    }
    
    func setButtonEnabledState() {
        if let text = destinationText.text, !text.isEmpty {
            addTrip.isEnabled = true
        } else {
            addTrip.isEnabled = false
        }
    }
    
    @IBAction func addPlan(_ sender: Any) {
        destinationText.resignFirstResponder()
        
        switch buttonState {
        case .destination:
            layoutButton(with: "next".localized(), showDestination: false, showName: true, showStartDate: false, showEndDate: false)
            buttonState = ButtonState.name
        case .name:
            layoutButton(with: "next".localized(), showDestination: false, showName: false, showStartDate: true, showEndDate: false)
            buttonState = ButtonState.startDate
        case .startDate:
            layoutButton(with: "addPlan".localized(), showDestination: false, showName: false, showStartDate: false, showEndDate: true)
            buttonState = ButtonState.endDate
            endDate.minimumDate = startDate.date
        default:
            let originalName = selectedOriginalPinName ?? destinationText.text!
            
            let plan = Plan(name: destinationText.text!, originalName: originalName, startDate: Timestamp(date: startDate.date), endDate: Timestamp(date: endDate.date))
            
            //TODO: check whether plan already exists and ask if user wants to override
            persistPlan(plan)
            
            self.performSegue(withIdentifier: Constants.Segues.planDetail, sender: plan)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Constants.Segues.planDetail {
            //After successful add plan: Redirect to plan detail
            let controller = segue.destination as! PlanDetailViewController
            let plan = sender as! Plan
            controller.plan = plan
            controller.pins = pins
            controller.dataController = dataController
            controller.firestorePlanDbReference = firestoreDbReference
        }
    }
    
    @IBAction func cancel(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func persistPlan(_ plan: Plan) {
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: plan.pinName, data: [
            FirestoreConstants.Ids.Plan.name: plan.name,
            FirestoreConstants.Ids.Plan.pinName: plan.pinName,
            FirestoreConstants.Ids.Plan.startDate: plan.startDate,
            FirestoreConstants.Ids.Plan.endDate: plan.endDate
        ]) { (error) in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            } else {
                debugPrint("Document added")
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
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        destinationText.text = self.pins[row].name
        selectedOriginalPinName = self.pins[row].name
        setButtonEnabledState()
    }
    
//    func textFieldDidEndEditing(_ textField: UITextField) {
//        //TODO: check whether pin with name exists -> if so, grey out add button + toast
//        setButtonEnabledState()
//        UiUtils.showToast(message: "Please enter a valid destination name", view: self.view)
//    }
    
    func pickerView(_ pickerView: UIPickerView, attributedTitleForRow row: Int, forComponent component: Int) -> NSAttributedString? {
        view.endEditing(true)
        
        let titleData = pins[row].name ?? ""
        
        let title = NSAttributedString(string: titleData, attributes: [NSAttributedString.Key.foregroundColor: UIColor.cyan])
        
        return title
    }
}
