//
//  AddFlightViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 05.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit
import RxCocoa
import RxSwift

class AddTransportViewController: UIViewController, UITextFieldDelegate {

    var firestoreDbReference: CollectionReference!
    var planDetailController: PlanDetailViewController!
    var transportDelegate: AddTransportDelegate!
    var transportSearchDelegate: AddTransportSearchDelegate!
    
    @IBOutlet weak var origin: SearchTextField!
    @IBOutlet weak var destination: SearchTextField!
    @IBOutlet weak var date: UIDatePicker!
    
    var plan: Plan!
    
    var disposableOrigin: Disposable?
    var disposableDestination: Disposable?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = String(format: "addTransport".localized(), transportDelegate.description())
        
        disposableOrigin = setupAutocompletion(for: origin)
        disposableDestination = setupAutocompletion(for: destination)
        
        date.datePickerMode = .date
        UiUtils.layoutDatePicker(date)
    }
    
    deinit {
        if let disposableOrigin = disposableOrigin {
            disposableOrigin.dispose()
        }
        
        if let disposableDestination = disposableDestination {
            disposableDestination.dispose()
        }
    }
    
    func setupAutocompletion(for searchTextField: SearchTextField) -> Disposable {
        return searchTextField.rx.text
            .debug("rxAutocomplete")
            .throttle(.milliseconds(1000), scheduler: MainScheduler.instance)
            .distinctUntilChanged()
            .filter{$0 != nil && $0!.count >= 5}
            .flatMapLatest { query in
                Rome2RioClient.sharedInstance.autocomplete(with: query!)
                .startWith([]) // clears results on new search term
                .catchErrorJustReturn([])
            }
            .observeOn(MainScheduler.instance)
            .subscribe(onNext: { filterStrings in
                // bind to ui
                if filterStrings.count > 0 {
                    searchTextField.filterStrings(filterStrings)
                }
            })
            //.disposed(by: disposeBag) --> do not dispose immediately, as user may continue typing
    }
    
    @IBAction func search(_ sender: Any) {
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
        let queryItems = transportSearchDelegate.buildSearchQueryItems(origin: origin.text!, destination: destination.text!)
        
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
