package com.sjaindl.travelcompanion.api.google

//
//  GooglePlaceType.swift
//  Travel Companion
//
//  Created by Stefan Jaindl on 31.10.18.
//  Copyright © 2018 Stefan Jaindl. All rights reserved.
//
enum class GooglePlaceType(private val rawValue: String) {
    Lodging("lodging"),
    Restaurant("restaurant"),
    PointOfInterest("pointOfInterest"),
    AmusementPark("amusementPark"),
    Aquarium("aquarium"),
    ArtGallery("artGallery"),
    Atm("atm"),
    Bank("bank"),
    Bar("bar"),
    BeautySalon("beautySalon"),
    BowlingAlley("bowlingAlley"),
    Cafe("cafe"),
    Casino("casino"),
    Church("church"),
    CityHall("cityHall"),
    Embassy("embassy"),
    Gym("gym"),
    HinduTemple("hinduTemple"),
    Library("library"),
    Mosque("mosque"),
    MovieTheater("movieTheater"),
    Museum("museum"),
    NightClub("nightClub"),
    PostOffice("postOffice"),
    RvPark("rvPark"),
    ShoppingMall("shoppingMall"),
    Spa("spa"),
    Stadium("stadium"),
    Synagogue("synagogue"),
    TravelAgency("travelAgency"),
    Zoo("zoo");

    //hotels
    //restaurants
    //attractions:
    val key: String
        get() {
            when (this) {
                PointOfInterest -> return "point_of_interest"
                AmusementPark -> return "amusement_park"
                ArtGallery -> return "art_gallery"
                BeautySalon -> return "beauty_salon"
                BowlingAlley -> return "bowling_alley"
                CityHall -> return "city_hall"
                HinduTemple -> return "hindu_temple"
                MovieTheater -> return "movie_theater"
                NightClub -> return "night_club"
                PostOffice -> return "post_office"
                RvPark -> return "rv_park"
                ShoppingMall -> return "shopping_mall"
                TravelAgency -> return "travel_agency"
                else -> return this.rawValue
            }
        }

    // TODO: MP String resources
    /*
    val description: String?
        get() {
            when (this) {
                lodging -> return "lodging".localized()
                restaurant -> return "restaurant".localized()
                pointOfInterest -> return "pointOfInterest".localized()
                amusementPark -> return "amusementPark".localized()
                aquarium -> return "aquarium".localized()
                artGallery -> return "artGallery".localized()
                atm -> return "atm".localized()
                bank -> return "bank".localized()
                bar -> return "bar".localized()
                beautySalon -> return "beautySalon".localized()
                bowlingAlley -> return "bowlingAlley".localized()
                cafe -> return "cafe".localized()
                casino -> return "casino".localized()
                church -> return "church".localized()
                cityHall -> return "cityHall".localized()
                embassy -> return "embassy".localized()
                gym -> return "gym".localized()
                hinduTemple -> return "hinduTemple".localized()
                library -> return "library".localized()
                mosque -> return "mosque".localized()
                movieTheater -> return "movieTheater".localized()
                museum -> return "museum".localized()
                nightClub -> return "nightClub".localized()
                postOffice -> return "postOffice".localized()
                rvPark -> return "rvPark".localized()
                shoppingMall -> return "shoppingMall".localized()
                spa -> return "spa".localized()
                stadium -> return "stadium".localized()
                synagogue -> return "synagogue".localized()
                travelAgency -> return "travelAgency".localized()
                zoo -> return "zoo".localized()
            }
        }
     */
}
