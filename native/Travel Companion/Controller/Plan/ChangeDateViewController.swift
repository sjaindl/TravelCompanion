//
//  ChangeDateViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 08.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit

class ChangeDateViewController: UIViewController {
    
    enum ButtonState: Int {
        case startDate = 0
        case endDate = 1
    }
    
    @IBOutlet weak var startDateLabel: UILabel!
    @IBOutlet weak var startDate: UIDatePicker!
    @IBOutlet weak var endDateLabel: UILabel!
    @IBOutlet weak var endDate: UIDatePicker!
    @IBOutlet weak var changeDate: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    
    var plan: Plan!
    var firestoreDbReference: DocumentReference!
    weak var changeDateDelegate: ChangeDateDelegate?
    
    var buttonState = ButtonState.startDate
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "changeDate".localized()
        
        firestoreDbReference = FirestoreClient.userReference().collection(FirestoreConstants.Collections.plans).document(plan.pinName)
        
        UiUtils.layoutDatePicker(startDate)
        UiUtils.layoutDatePicker(endDate)
        
        startDate.date = plan.startDate.dateValue()
        endDate.date = plan.endDate.dateValue()
        
        layoutButton(with: "next".localized(), showStartDate: true, showEndDate: false)
    }
    
    func layoutButton(with text: String, showStartDate: Bool, showEndDate: Bool) {
        changeDate.setTitle(text, for: .normal)
        
        startDate.isHidden = !showStartDate
        startDateLabel.isHidden = !showStartDate
        endDate.isHidden = !showEndDate
        endDateLabel.isHidden = !showEndDate
    }
    
    @IBAction func addPlan(_ sender: Any) {
        switch buttonState {
        case .startDate:
            layoutButton(with: "changeDate".localized(), showStartDate: false, showEndDate: true)
            buttonState = ButtonState.endDate
            endDate.minimumDate = startDate.date
        default:
            
            plan.startDate = Timestamp(date: startDate.date)
            plan.endDate = Timestamp(date: endDate.date)
            
            updatePlan(plan)
            
            dismiss(animated: true, completion: nil)
        }
    }
    
    @IBAction func cancel(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
    
    func updatePlan(_ plan: Plan) {
        firestoreDbReference.updateData([
            FirestoreConstants.Ids.Plan.startDate: plan.startDate,
            FirestoreConstants.Ids.Plan.endDate: plan.endDate
        ]) { error in
            if let error = error {
                UiUtils.showError(error.localizedDescription, controller: self)
            }
                
            self.dismiss(animated: true, completion: nil)
            self.changeDateDelegate?.changedDate()
        }
    }
}
