//
//  AddFlightDetailViewController.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 10.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import CodableFirebase
import Firebase
import UIKit

class AddFlightDetailViewController: UITableViewController {
    
    struct CellData {
        var opened = Bool()
        var airHop = [AirHop]()
        var airLeg: AirLeg?
        var route: Route?
        var segment: Segment?
    }
    
    var weekDayToDayFlagMap: [Int: Int] =  [1: 0x01, /* Sunday */
                                2: 0x02, /* Monday */
                                3: 0x04, /* Tuesday */
                                4: 0x08, /* Wednesday */
                                5: 0x10, /* Thursday */
                                6: 0x20, /* Friday */
                                7: 0x40] /* Saturday */
    
    var firestoreFligthDbReference: CollectionReference!
    var searchResponse: SearchResponse!
    var planDetailController: PlanDetailViewController!
    
    var date = Date()
    var cellData = [CellData]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initCellData()
    }
    
    func initCellData() {
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
    
    func dateIsRelevant(_ date: Date, in leg: AirLeg) -> Bool {
        let weekdayToTravel = Calendar.current.component(.weekday, from: date)
        
        guard let operatingDays = leg.operatingDays, let weekdayBitMask = weekDayToDayFlagMap[weekdayToTravel] else {
            return false //no operating day data available?
        }
        
        return weekdayBitMask & operatingDays > 0
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return cellData.count
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cellData[section].opened ? cellData[section].airHop.count + 1 : 1
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.row == 0 {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: Constants.REUSE_IDS.FLIGHT_DETAIL_SECTION_CELL_REUSE_ID) else {
                return UITableViewCell()
            }
            
            let route = cellData[indexPath.section].route!
            let segment = cellData[indexPath.section].segment!
            let leg = cellData[indexPath.section].airLeg!
            
            let depPlace = searchResponse.places[route.depPlace]
            let arrPlace = searchResponse.places[route.arrPlace]
            
            cell.textLabel?.text = "\(route.name): \(depPlace.shortName) - \(arrPlace.shortName)"
            
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
            
            var detailText = "≈\(segment.distance) km, \(duration / 60) hours, \(duration % 60) minutes"
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
            guard let cell = tableView.dequeueReusableCell(withIdentifier: Constants.REUSE_IDS.FLIGHT_DETAIL_CELL_REUSE_ID) else {
                return UITableViewCell()
            }
            
            let hop = cellData[indexPath.section].airHop[indexPath.row - 1]
            
            let airline = searchResponse.airlines[hop.airline]
            
            if let airlineUrl = airline.icon?.url, let url = URL(string: "\(Rome2RioConstants.UrlComponents.PROTOCOL)://\(Rome2RioConstants.UrlComponents.DOMAIN)\(airlineUrl)") {
                try? cell.imageView?.image = UIImage(data: Data(contentsOf: url))
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

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row == 0 {
            let sections = IndexSet.init(integer: indexPath.section)
            cellData[indexPath.section].opened = !cellData[indexPath.section].opened
            tableView.reloadSections(sections, with: .fade)
        } else {
            
            let leg = cellData[indexPath.section].airLeg!
  
            //choose single flight or whole leg?
            let alert = UIAlertController(title: "Add Flight", message: "Do you want to add the tapped flight or whole leg?", preferredStyle: .alert)
            
            alert.addAction(UIAlertAction(title: NSLocalizedString("Single flight", comment: "Add single flight"), style: .default, handler: { _ in
                
                let hop = self.cellData[indexPath.section].airHop[indexPath.row - 1]
                let airline = self.searchResponse.airlines[hop.airline]
                var aircraft: Aircraft?
                if let aircraftIndex = hop.aircraft {
                    aircraft = self.searchResponse.aircrafts[aircraftIndex]
                }
                
                self.persistFlight(hop, aircraft: aircraft, airline: airline)
                self.navigationController?.popToViewController(self.planDetailController, animated: true)
            }))
            
            alert.addAction(UIAlertAction(title: NSLocalizedString("Whole leg", comment: "Add whole leg"), style: .default, handler: { _ in
                
                if let hops = leg.hops {
                    for hop in hops {
                        let airline = self.searchResponse.airlines[hop.airline]
                        var aircraft: Aircraft?
                        if let aircraftIndex = hop.aircraft {
                            aircraft = self.searchResponse.aircrafts[aircraftIndex]
                        }
                        self.persistFlight(hop, aircraft: aircraft, airline: airline)
                    }
                }
                
                self.navigationController?.popToViewController(self.planDetailController, animated: true)
            }))
            
            alert.addAction(UIAlertAction(title: NSLocalizedString("Cancel", comment: "cancel"), style: .default, handler: { _ in
                self.dismiss(animated: true, completion: nil)
            }))
            
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func persistFlight(_ hop: AirHop, aircraft: Aircraft?, airline: Airline) {
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
        
        FirestoreClient.addData(collectionReference: firestoreFligthDbReference,
                                data: docData) { (error) in
            if let error = error {
                print("Error adding document: \(error)")
            } else {
                print("Document added")
            }
        }
    }
}
