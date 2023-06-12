package com.sjaindl.travelcompanion.api.google

import dev.icerock.moko.resources.StringResource
import com.sjaindl.travelcompanion.shared.R as SharedR

val GooglePlaceType.description: StringResource
    get() {
        when (this) {
            GooglePlaceType.Lodging -> return StringResource(SharedR.string.lodging)
            GooglePlaceType.Restaurant -> return StringResource(SharedR.string.restaurant)
            GooglePlaceType.PointOfInterest -> return StringResource(SharedR.string.pointOfInterest)
            GooglePlaceType.AmusementPark -> return StringResource(SharedR.string.amusementPark)
            GooglePlaceType.Aquarium -> return StringResource(SharedR.string.aquarium)
            GooglePlaceType.ArtGallery -> return StringResource(SharedR.string.artGallery)
            GooglePlaceType.Atm -> return StringResource(SharedR.string.atm)
            GooglePlaceType.Bank -> return StringResource(SharedR.string.bank)
            GooglePlaceType.Bar -> return StringResource(SharedR.string.bar)
            GooglePlaceType.BeautySalon -> return StringResource(SharedR.string.beautySalon)
            GooglePlaceType.BowlingAlley -> return StringResource(SharedR.string.bowlingAlley)
            GooglePlaceType.Cafe -> return StringResource(SharedR.string.cafe)
            GooglePlaceType.Casino -> return StringResource(SharedR.string.casino)
            GooglePlaceType.Church -> return StringResource(SharedR.string.church)
            GooglePlaceType.CityHall -> return StringResource(SharedR.string.cityHall)
            GooglePlaceType.Embassy -> return StringResource(SharedR.string.embassy)
            GooglePlaceType.Gym -> return StringResource(SharedR.string.gym)
            GooglePlaceType.HinduTemple -> return StringResource(SharedR.string.hinduTemple)
            GooglePlaceType.Library -> return StringResource(SharedR.string.library)
            GooglePlaceType.Mosque -> return StringResource(SharedR.string.mosque)
            GooglePlaceType.MovieTheater -> return StringResource(SharedR.string.movieTheater)
            GooglePlaceType.Museum -> return StringResource(SharedR.string.museum)
            GooglePlaceType.NightClub -> return StringResource(SharedR.string.nightClub)
            GooglePlaceType.PostOffice -> return StringResource(SharedR.string.postOffice)
            GooglePlaceType.RvPark -> return StringResource(SharedR.string.rvPark)
            GooglePlaceType.ShoppingMall -> return StringResource(SharedR.string.shoppingMall)
            GooglePlaceType.Spa -> return StringResource(SharedR.string.spa)
            GooglePlaceType.Stadium -> return StringResource(SharedR.string.stadium)
            GooglePlaceType.Synagogue -> return StringResource(SharedR.string.synagogue)
            GooglePlaceType.TravelAgency -> return StringResource(SharedR.string.travelAgency)
            GooglePlaceType.Zoo -> return StringResource(SharedR.string.zoo)
        }
    }
