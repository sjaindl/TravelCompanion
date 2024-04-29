package com.sjaindl.travelcompanion.api.google

import dev.icerock.moko.resources.StringResource
import com.sjaindl.travelcompanion.R

val GooglePlaceType.description: StringResource
    get() {
        when (this) {
            GooglePlaceType.Lodging -> return StringResource(R.string.lodging)
            GooglePlaceType.Restaurant -> return StringResource(R.string.restaurant)
            GooglePlaceType.PointOfInterest -> return StringResource(R.string.pointOfInterest)
            GooglePlaceType.AmusementPark -> return StringResource(R.string.amusementPark)
            GooglePlaceType.Aquarium -> return StringResource(R.string.aquarium)
            GooglePlaceType.ArtGallery -> return StringResource(R.string.artGallery)
            GooglePlaceType.Atm -> return StringResource(R.string.atm)
            GooglePlaceType.Bank -> return StringResource(R.string.bank)
            GooglePlaceType.Bar -> return StringResource(R.string.bar)
            GooglePlaceType.BeautySalon -> return StringResource(R.string.beautySalon)
            GooglePlaceType.BowlingAlley -> return StringResource(R.string.bowlingAlley)
            GooglePlaceType.Cafe -> return StringResource(R.string.cafe)
            GooglePlaceType.Casino -> return StringResource(R.string.casino)
            GooglePlaceType.Church -> return StringResource(R.string.church)
            GooglePlaceType.CityHall -> return StringResource(R.string.cityHall)
            GooglePlaceType.Embassy -> return StringResource(R.string.embassy)
            GooglePlaceType.Gym -> return StringResource(R.string.gym)
            GooglePlaceType.HinduTemple -> return StringResource(R.string.hinduTemple)
            GooglePlaceType.Library -> return StringResource(R.string.library)
            GooglePlaceType.Mosque -> return StringResource(R.string.mosque)
            GooglePlaceType.MovieTheater -> return StringResource(R.string.movieTheater)
            GooglePlaceType.Museum -> return StringResource(R.string.museum)
            GooglePlaceType.NightClub -> return StringResource(R.string.nightClub)
            GooglePlaceType.PostOffice -> return StringResource(R.string.postOffice)
            GooglePlaceType.RvPark -> return StringResource(R.string.rvPark)
            GooglePlaceType.ShoppingMall -> return StringResource(R.string.shoppingMall)
            GooglePlaceType.Spa -> return StringResource(R.string.spa)
            GooglePlaceType.Stadium -> return StringResource(R.string.stadium)
            GooglePlaceType.Synagogue -> return StringResource(R.string.synagogue)
            GooglePlaceType.TravelAgency -> return StringResource(R.string.travelAgency)
            GooglePlaceType.Zoo -> return StringResource(R.string.zoo)
        }
    }
