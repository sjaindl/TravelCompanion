//
//  AddFlightDelegate.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 17.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import Foundation
import UIKit

class AddFlightDelegate: NSObject, AddTransportDelegate {
    var weekDayToDayFlagMap: [Int: Int] =  [1: 0x01, /* Sunday */
        2: 0x02, /* Monday */
        3: 0x04, /* Tuesday */
        4: 0x08, /* Wednesday */
        5: 0x10, /* Thursday */
        6: 0x20, /* Friday */
        7: 0x40] /* Saturday */
    
    struct CellData {
        var opened = Bool()
        var airHop = [AirHop]()
        var airLeg: AirLeg?
        var route: Route?
        var segment: Segment?
    }
    
    var cellData = [CellData]()
    
    func initCellData(searchResponse: SearchResponse, date: Date) {
        for route in searchResponse.routes {
            for segment in route.segments {
                if let legs = segment.outbound {
                    for leg in legs {
                        if dateIsRelevant(date, in: leg), let hops = leg.hops {
                            cellData.append(CellData(opened: false, airHop: hops, airLeg: leg, route: route, segment: segment))
                        }
                    }
                }
            }
        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return cellData.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellData[section].opened ? cellData[section].airHop.count + 1 : 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath, searchResponse: SearchResponse) -> UITableViewCell {
        
        if indexPath.row == 0 {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: Constants.ReuseIds.transportDetailWithoutImageCell) else {
                return UITableViewCell()
            }
            
            let route = cellData[indexPath.section].route!
            let segment = cellData[indexPath.section].segment!
            let leg = cellData[indexPath.section].airLeg!
            
            let depPlace = searchResponse.places[route.depPlace]
            let arrPlace = searchResponse.places[route.arrPlace]
            
            cell.textLabel?.text = "\(route.name): \(depPlace.shortName) - \(arrPlace.shortName)"
            cell.imageView?.image  = nil
            
            var duration = 0
            if let hops = leg.hops {
                for hop in hops {
                    duration += hop.duration
                    if let layoverDuration = hop.layoverDuration {
                        duration += Int(layoverDuration)
                    }
                }
            }
            
            if duration == 0 {
                duration = route.totalDuration
            }
            
            var detailText = String(format: "flightDetail".localized(), segment.distance, duration / 60, duration % 60)
            
            if let prices = leg.indicativePrices, prices.count > 0 {
                if let minPrice = prices[0].nativePriceLow, let maxPrice = prices[0].nativePriceHigh {
                    detailText += ", \(minPrice) - \(maxPrice) \(prices[0].currency)"
                } else {
                    detailText += ", ≈\(prices[0].price) \(prices[0].currency)"
                }
            }
            cell.detailTextLabel?.text = detailText
            
            return cell
        } else {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: Constants.ReuseIds.transportDetailWithImageCell) else {
                return UITableViewCell()
            }
            
            cell.imageView?.image  = nil
            
            let hop = cellData[indexPath.section].airHop[indexPath.row - 1]
            
            let airline = searchResponse.airlines[hop.airline]
            
            if let airlineUrl = airline.icon?.url, let url = URL(string: "\(Rome2RioConstants.UrlComponents.urlProtocol)://\(Rome2RioConstants.UrlComponents.domain)\(airlineUrl)") {
                ((try? cell.imageView?.image = UIImage(data: Data(contentsOf: url))) as ()??)
            }
            
            var text = airline.name
            if let aircraftIndex = hop.aircraft {
                let aircraft = searchResponse.aircrafts[aircraftIndex]
                text += ", \(aircraft.manufacturer) \(aircraft.model)"
            }
            
            cell.textLabel?.text = text
            
            let depPlace = searchResponse.places[hop.depPlace]
            let arrPlace = searchResponse.places[hop.arrPlace]
            
            cell.detailTextLabel?.text = "\(airline.code)\(hop.flight): \(depPlace.shortName) \(hop.depTime) - \(arrPlace.shortName) \(hop.arrTime)"
            
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath, searchResponse: SearchResponse, date: Date, firestoreDbReference: CollectionReference, plan: Plan, controller: UIViewController, popToController: UIViewController) {
        if indexPath.row == 0 {
            let sections = IndexSet.init(integer: indexPath.section)
            cellData[indexPath.section].opened = !cellData[indexPath.section].opened
            tableView.reloadSections(sections, with: .fade)
        } else {
            
            let leg = cellData[indexPath.section].airLeg!
            
            //choose single flight or whole leg?
            let alert = UIAlertController(title: "addFlight".localized(), message: "addFlight?".localized(), preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: "singleFlight".localized(), style: .default, handler: { _ in
                
                let hop = self.cellData[indexPath.section].airHop[indexPath.row - 1]
                let airline = searchResponse.airlines[hop.airline]
                var aircraft: Aircraft?
                if let aircraftIndex = hop.aircraft {
                    aircraft = searchResponse.aircrafts[aircraftIndex]
                }
                
                self.persistFlight(hop, aircraft: aircraft, airline: airline, searchResponse: searchResponse, date: date, firestoreDbReference: firestoreDbReference, plan: plan, controller: popToController)
                controller.navigationController?.popToViewController(popToController, animated: true)
                UiUtils.showToast(message: "addedRoute".localized(), view: popToController.view)
            }))
            
            alert.addAction(UIAlertAction(title: "wholeLeg".localized(), style: .default, handler: { _ in
                
                if let hops = leg.hops {
                    for hop in hops {
                        let airline = searchResponse.airlines[hop.airline]
                        var aircraft: Aircraft?
                        if let aircraftIndex = hop.aircraft {
                            aircraft = searchResponse.aircrafts[aircraftIndex]
                        }
                        self.persistFlight(hop, aircraft: aircraft, airline: airline, searchResponse: searchResponse, date: date, firestoreDbReference: firestoreDbReference, plan: plan, controller: popToController)
                    }
                }
                
                controller.navigationController?.popToViewController(popToController, animated: true)
                UiUtils.showToast(message: "addedRoute".localized(), view: popToController.view)
            }))
            
            alert.addAction(UIAlertAction(title: "cancel".localized(), style: .default, handler: { _ in
                controller.dismiss(animated: true, completion: nil)
            }))
            
            controller.present(alert, animated: true, completion: nil)
        }
    }
    
    func dateIsRelevant(_ date: Date, in leg: AirLeg) -> Bool {
        let weekdayToTravel = Calendar.current.component(.weekday, from: date)
        
        guard let operatingDays = leg.operatingDays, let weekdayBitMask = weekDayToDayFlagMap[weekdayToTravel] else {
            return false //no operating day data available?
        }
        
        return weekdayBitMask & operatingDays > 0
    }
    
    func persistFlight(_ hop: AirHop, aircraft: Aircraft?, airline: Airline, searchResponse: SearchResponse, date: Date, firestoreDbReference: CollectionReference, plan: Plan, controller: UIViewController) {
        let arrPlace = searchResponse.places[hop.arrPlace].shortName
        let depPlace = searchResponse.places[hop.depPlace].shortName
        
        let flight = Flight(date: Timestamp(date: date), depPlace: depPlace, arrPlace: arrPlace, depTime: hop.depTime, arrTime: hop.arrTime, flight: "\(airline.code)\(hop.flight)", duration: hop.duration, airline: airline.name)
        
        if let aircraft = aircraft {
            flight.aircraft = "\(aircraft.manufacturer) \(aircraft.model)"
        }
        if let depTerminal = hop.depTerminal {
            flight.depTerminal = depTerminal
        }
        if let arrTerminal = hop.arrTerminal {
            flight.arrTerminal = arrTerminal
        }
        if let url = airline.icon?.url {
            flight.airlineUrl = url
        }
        
        let docData = try! FirestoreEncoder().encode(flight)
        FirestoreClient.addData(collectionReference: firestoreDbReference, documentName: flight.id, data: docData) { (error) in
            if let error = error {
                debugPrint("Error adding document: \(error)")
                UiUtils.showToast(message: error.localizedDescription, view: controller.view)
            } else {
                debugPrint("Document added")
                plan.fligths.append(flight)
                if let controller = controller as? PlanDetailViewController {
                    //we have to reload the data here as we have already popped the stack back to PlanDetailViewController
                    //and we add data asynchronously here
                    controller.tableView.reloadData()
                }
            }
        }
    }
    
    func description() -> String {
        return "flight".localized()
    }
}
