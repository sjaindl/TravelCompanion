//
//  Place.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 04.09.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

struct SearchResponse: Codable {
    var languageCode: String
    var currencyCode: String
    var places: [Place]
    var airlines: [Airline]
    var aircrafts: [Aircraft]
    var agencies: [Agency]
    var vehicles: [Vehicle]
    var routes: [Route]
}

struct Place: Codable {
    var lat: Double
    var lng: Double
    var kind: String
    var shortName: String
    var longName: String?
    var canonicalName: String?
    var regionCode: String?
    var countryCode: String?
    var timeZone: String?
}

struct Airline: Codable {
    var code: String
    var name: String
    var url: String?
    var icon: Icon?
}

struct Icon: Codable {
    var url: String?
}

struct Aircraft: Codable {
    var code: String
    var manufacturer: String
    var model: String
}

struct Agency: Codable {
    var name: String
    var url: String?
    var phone: String?
    var icon: Icon?
}

struct Vehicle: Codable {
    var name: String
    var kind: String?
}

struct Route: Codable {
    var name: String
    var depPlace: Int
    var arrPlace: Int
    var distance: Double
    var totalDuration: Int
    var totalTransitDuration: Int
    var totalTransferDuration: Int
    var segments: [Segment]
    var indicativePrices: [IndicativePrice]?
    var alternatives: [Alternative]?
}

struct Segment: Codable {
    var segmentKind: String
    var depPlace: Int
    var arrPlace: Int
    var vehicle: Int
    var distance: Double
    var transitDuration: Int
    var transferDuration: Int
    var indicativePrices: [IndicativePrice]?
    
    //SurfaceSegment:
    //path
    var stops: [SurfaceStop]?
    var agencies: [SurfaceAgency]?
    
    //AirSegment:
    var outbound: [AirLeg]?
    var returnLeg: [AirLeg]?
    
    private enum CodingKeys: String, CodingKey {
        case segmentKind
        case depPlace
        case arrPlace
        case vehicle
        case distance
        case transitDuration
        case transferDuration
        case indicativePrices
        case stops
        case agencies
        case outbound
        case returnLeg = "return"
    }
}

struct IndicativePrice: Codable {
    var name: String?
    var price: Float
    var priceLow: Float?
    var priceHigh: Float?
    var currency: String
    var nativePrice: Float?
    var nativePriceLow: Float?
    var nativePriceHigh: Float?
    var nativeCurrency: String?
/*
IndicativePrice example response:
 "name": "Shared",
 "price": 29,
 "priceLow": 28,
 "priceHigh": 30,
 "currency": "USD",
 "nativePrice": 25,
 "nativePriceLow": 24,
 "nativePriceHigh": 26,
 "nativeCurrency": "EUR"
*/
}

struct Alternative: Codable {
    var firstSegment: Int
    var lastSegment: Int
    var route: Route
}

struct SurfaceStop: Codable {
    var place: Int
    var transitDuration: Double?
    var stopDuration: Double?
}

struct SurfaceAgency: Codable {
    var agency: Int
    var frequency: Float?
    var duration: Float?
    var operatingDays: Int? //DayFlag (hex)
    var lineNames: [String]? //SurfaceLineName
    var lineCodes: [String]? //SurfaceLineCode
    var links: [ExternalLink]?
}

struct AirLeg: Codable {
    var operatingDays: Int? //DayFlag (hex)
    var indicativePrices: [IndicativePrice]?
    var hops: [AirHop]?
}

/*
 operatingDays - DayFlags:
 Value    Description
 0x01    Sunday
 0x02    Monday
 0x04    Tuesday
 0x08    Wednesday
 0x10    Thursday
 0x20    Friday
 0x40    Saturday
*/

struct AirHop: Codable {
    var depPlace: Int
    var arrPlace: Int
    var depTerminal: String?
    var arrTerminal: String?
    var depTime: String
    var arrTime: String
    var flight: String
    var duration: Int
    var airline: Int
    var operatingAirline: String?
    var aircraft: Int?
    var dayChange: Int?
    var layoverDuration: Float?
    var layoverDayChange: Int?
    var codeshares: [AirCodeshare]?
}

struct AirCodeshare: Codable {
    var airline: Int
    var flight: String
}

struct ExternalLink: Codable {
    var text: String
    var url: String
    var displayUrl: String?
    var moustacheUrl: String?
}
