//
//  ExplorePlacesSearchViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 02.07.19.
//  Copyright © 2019 Stefan Jaindl. All rights reserved.
//

import RxCocoa
import RxSwift
import shared
import UIKit

class ExplorePlacesSearchViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    
    let searchController = UISearchController(searchResultsController: nil)
    var autocompleteDisposable: Disposable?
    var detailDisposable: Disposable?
    var results = BehaviorRelay<[PlacesPredictions?]>(value: [nil])
    var sessionToken: String = ""
    var callback: PlacePicker! = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationItem.title = "searchPlaces".localized()
        
        autocompleteDisposable = setupAutocompletion(for: searchController)
        
        setupSearchController()
        
        tableView.delegate = self
        
        self.sessionToken = randomString(length: 32)
    }
    
    deinit {
        if let disposable = autocompleteDisposable {
            disposable.dispose()
        }
        
        if let disposable = detailDisposable {
            disposable.dispose()
        }
    }
    
    func setupAutocompletion(for searchController: UISearchController) -> Disposable {
        let disposable = searchController.searchBar.rx.text
            .debug("rxAutocompleteGooglePlaces")
            .throttle(.milliseconds(AutocompleteConfig.autocompletionDelayMilliseconds), scheduler: MainScheduler.instance)
            .distinctUntilChanged()
            //.filter{$0 != nil && $0!.count >= AutocompleteConfig.autocompletionMinChars}
            .flatMapLatest { query in
                GoogleClient.sharedInstance.autocomplete(input: query!, token: self.sessionToken)
                    .startWith([]) // clears results on new search term
                    .catchAndReturn([])
            }
            .observe(on: MainScheduler.instance)
            .subscribe(onNext: { filterStrings in
                self.results.accept(filterStrings)
            })
        //.disposed(by: disposeBag) --> do not dispose immediately, as user may continue typing
        
        // bind to ui:
        _ = self.results.asObservable()
            .bind(to: self.tableView.rx.items(cellIdentifier: Constants.ReuseIds.googlePlaceCellReuseId)) {
                (index, searchResult, cell) in
                cell.textLabel?.text = searchResult?.description
        }
        
        return disposable
    }
    
    private func setupSearchController() {
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.searchBar.placeholder = "searchPlaces".localized()
        navigationItem.searchController = searchController
        navigationItem.hidesSearchBarWhenScrolling = false
        definesPresentationContext = true
        
        tableView.setContentOffset(CGPoint(x: -1, y: 0), animated: true)
    }
    
    func randomString(length: Int) -> String {
        let letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return String((0..<length).map{ _ in letters.randomElement()! })
    }
}

extension ExplorePlacesSearchViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let place = self.results.value[indexPath.row]
        
        guard let placeId = place?.placeId else {
            debugPrint("no place id!")
            return
        }
        
        detailDisposable = searchController.searchBar.rx.text
            .debug("rxGooglePlacesDetails")
            .throttle(.milliseconds(AutocompleteConfig.autocompletionDelayMilliseconds), scheduler: MainScheduler.instance)
            .distinctUntilChanged()
            .flatMapLatest { query in
                GoogleClient.sharedInstance.placeDetail(placeId: placeId, token: self.sessionToken)
                    .startWith(nil) // clears results on new search term
                    .catchErrorJustReturn(nil)
            }
            .observeOn(MainScheduler.instance)
            .subscribe(onNext: { result in
                
                if let result = result {
                    self.navigationController?.popViewController(animated: true)
                    self.callback.didPickPlace(result, for: placeId)
                }
            })
    }
    
}
