//
//  AddTransportDelegate.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Firebase
import Foundation
import UIKit

protocol AddTransportDelegate {
    func initCellData(searchResponse: SearchResponse, date: Date)
    func numberOfSections(in tableView: UITableView) -> Int
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath, searchResponse: SearchResponse) -> UITableViewCell
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath, searchResponse: SearchResponse, date: Date, firestoreDbReference: CollectionReference, plan: Plan, controller: UIViewController, popToController: UIViewController)
    func description() -> String
}
