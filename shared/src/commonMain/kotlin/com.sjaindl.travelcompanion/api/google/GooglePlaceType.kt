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

    /*

    val description: String?
        get() {
            when (this) {
                Lodging -> return R.string.lodging
                Restaurant -> return "restaurant".localized()
                PointOfInterest -> return "pointOfInterest".localized()
                AmusementPark -> return "amusementPark".localized()
                Aquarium -> return "aquarium".localized()
                ArtGallery -> return "artGallery".localized()
                Atm -> return "atm".localized()
                Bank -> return "bank".localized()
                Bar -> return "bar".localized()
                BeautySalon -> return "beautySalon".localized()
                BowlingAlley -> return "bowlingAlley".localized()
                Cafe -> return "cafe".localized()
                Casino -> return "casino".localized()
                Church -> return "church".localized()
                CityHall -> return "cityHall".localized()
                Embassy -> return "embassy".localized()
                Gym -> return "gym".localized()
                HinduTemple -> return "hinduTemple".localized()
                Library -> return "library".localized()
                Mosque -> return "mosque".localized()
                MovieTheater -> return "movieTheater".localized()
                Museum -> return "museum".localized()
                NightClub -> return "nightClub".localized()
                PostOffice -> return "postOffice".localized()
                RvPark -> return "rvPark".localized()
                ShoppingMall -> return "shoppingMall".localized()
                Spa -> return "spa".localized()
                Stadium -> return "stadium".localized()
                Synagogue -> return "synagogue".localized()
                TravelAgency -> return "travelAgency".localized()
                Zoo -> return "zoo".localized()
            }
        }

     */
}
