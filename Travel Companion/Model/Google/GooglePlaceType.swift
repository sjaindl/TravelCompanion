//
//  GooglePlaceType.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//

import Foundation

public enum GooglePlaceType: String {
    case lodging           //hotels
    case restaurant        //restaurants
    
    //attractions:
    case pointOfInterest
    case amusementPark
    case aquarium
    case artGallery
    case atm
    case bank
    case bar
    case beautySalon
    case bowlingAlley
    case cafe
    case casino
    case church
    case cityHall
    case embassy
    case gym
    case hinduTemple
    case library
    case mosque
    case movieTheater
    case museum
    case nightClub
    case postOffice
    case rvPark
    case shoppingMall
    case spa
    case stadium
    case synagogue
    case travelAgency
    case zoo
    
    public var key: String {
        switch self {
        case .pointOfInterest:
            return "point_of_interest"
        case .amusementPark:
            return "amusement_park"
        case .artGallery:
            return "art_gallery"
        case .beautySalon:
            return "beauty_salon"
        case .bowlingAlley:
            return "bowling_alley"
        case .cityHall:
            return "city_hall"
        case .hinduTemple:
            return "hindu_temple"
        case .movieTheater:
            return "movie_theater"
        case .nightClub:
            return "night_club"
        case .postOffice:
            return "post_office"
        case .rvPark:
            return "rv_park"
        case .shoppingMall:
            return "shopping_mall"
        case .travelAgency:
            return "travel_agency"
        default:
            return self.rawValue
        }
    }
    
    public var description: String? {
        switch self {
        case .lodging:
            return "lodging".localized()
        case .restaurant:
            return "restaurant".localized()
        case .pointOfInterest:
            return "pointOfInterest".localized()
        case .amusementPark:
            return "amusementPark".localized()
        case .aquarium:
            return "aquarium".localized()
        case .artGallery:
            return "artGallery".localized()
        case .atm:
            return "atm".localized()
        case .bank:
            return "bank".localized()
        case .bar:
            return "bar".localized()
        case .beautySalon:
            return "beautySalon".localized()
        case .bowlingAlley:
            return "bowlingAlley".localized()
        case .cafe:
            return "cafe".localized()
        case .casino:
            return "casino".localized()
        case .church:
            return "church".localized()
        case .cityHall:
            return "cityHall".localized()
        case .embassy:
            return "embassy".localized()
        case .gym:
            return "gym".localized()
        case .hinduTemple:
            return "hinduTemple".localized()
        case .library:
            return "library".localized()
        case .mosque:
            return "mosque".localized()
        case .movieTheater:
            return "movieTheater".localized()
        case .museum:
            return "museum".localized()
        case .nightClub:
            return "nightClub".localized()
        case .postOffice:
            return "postOffice".localized()
        case .rvPark:
            return "rvPark".localized()
        case .shoppingMall:
            return "shoppingMall".localized()
        case .spa:
            return "spa".localized()
        case .stadium:
            return "stadium".localized()
        case .synagogue:
            return "synagogue".localized()
        case .travelAgency:
            return "travelAgency".localized()
        case .zoo:
            return "zoo".localized()
        }
    }
}
