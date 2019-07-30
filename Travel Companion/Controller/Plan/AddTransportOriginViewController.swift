//
//  AddTransportOriginViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 25.06.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import Firebase
import UIKit
import RxCocoa
import RxSwift

class AddTransportOriginViewController: UIViewController, UITableViewDelegate {
    
    @IBOutlet weak var tableView: UITableView!
    
    var firestoreDbReference: CollectionReference!
    var planDetailController: PlanDetailViewController!
    var transportDelegate: AddTransportDelegate!
    var transportSearchDelegate: AddTransportSearchDelegate!
    
    let searchController = UISearchController(searchResultsController: nil)
    var results = BehaviorRelay<[String?]>(value: [""])
    
    var plan: Plan!
    
    var disposableOrigin: Disposable?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.hidesSearchBarWhenScrolling = false
        
        self.navigationItem.title = String(format: "addTransport".localized(), transportDelegate.description())
        
        disposableOrigin = setupAutocompletion(for: searchController)

        setupSearchController()
        
        self.tableView.delegate = self
    }
    
    deinit {
        if let disposableOrigin = disposableOrigin {
            disposableOrigin.dispose()
        }
    }
    
    func setupAutocompletion(for searchController: UISearchController) -> Disposable {
        let disposable = searchController.searchBar.rx.text
            .debug("rxAutocomplete")
            .throttle(.milliseconds(AutocompleteConfig.autocompletionDelayMilliseconds), scheduler: MainScheduler.instance)
            .distinctUntilChanged()
            //.filter{$0 != nil && $0!.count >= AutocompleteConfig.autocompletionMinChars}
            .flatMapLatest { query in
                Rome2RioClient.sharedInstance.autocomplete(with: query!)
                    .startWith([]) // clears results on new search term
                    .catchErrorJustReturn([])
            }
            .observeOn(MainScheduler.instance)
            .subscribe(onNext: { filterStrings in
                self.results.accept(filterStrings)
            })
        //.disposed(by: disposeBag) --> do not dispose immediately, as user may continue typing
        
        // bind to ui:
        _ = self.results.asObservable()
            .bind(to: self.tableView.rx.items(cellIdentifier: Constants.ReuseIds.originCellReuseIdCell)) {
            (index, searchResult, cell) in
            cell.textLabel?.text = searchResult
            //cell.detailTextLabel?.text = repository.url
        }
        
        return disposable
    }
    
    private func setupSearchController() {
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "searchOrigin".localized()
        navigationItem.searchController = searchController
        definesPresentationContext = true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
         if segue.identifier == Constants.Segues.planAddDestination {
            let controller = segue.destination as! AddTransportDestinationViewController
            controller.firestoreDbReference = firestoreDbReference
            controller.transportDelegate = transportDelegate
            controller.transportSearchDelegate = transportSearchDelegate
            controller.planDetailController = self.planDetailController
            controller.plan = plan
            controller.transport = sender as? Transport
        }
    }
}

extension AddTransportOriginViewController {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let transport = Transport(origin: self.results.value[indexPath.row], destination: nil, date: nil)
        performSegue(withIdentifier: Constants.Segues.planAddDestination, sender: transport)
    }
}
