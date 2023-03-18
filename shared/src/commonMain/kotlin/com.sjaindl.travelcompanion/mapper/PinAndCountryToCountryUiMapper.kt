package com.sjaindl.travelcompanion.mapper

import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.model.CountryUi

class PinAndCountryToCountryUiMapper {
    fun map(pin: Pin, country: Country): CountryUi {
        val countryUi = CountryUi(countryCode = country.countryCode)

        setPinData(pin = pin, countryUi = countryUi)
        setCountryData(country = country, countryUi = countryUi)

        return countryUi
    }

    private fun setPinData(pin: Pin, countryUi: CountryUi) {
        countryUi.placeName = pin.name

        /*
        val placeType
            get() = {
                if let placeTypes = pin?.placetypes, let placeTypeArray = Array(placeTypes) as? [PlaceType], placeTypeArray.count > 0
                {
                    placeType.text = ""

                    for type in placeTypeArray {
                        placeType.text?.append(type.type!+", ")
                    }
                    placeType.text = placeType.text?.trimmingCharacters(in: CharacterSet(charactersIn: ", "))
                }
            }
         */

        countryUi.phoneNumber = pin.phoneNumber

        val lat = pin.latitude ?: 0.0
        val lng = pin.longitude ?: 0.0
        val latitudePostfix = if (lat < 0.0) "south" else "north"
        val longitudePostfix = if (lng < 0.0) "west" else "east"

        val latitudeLongitude = "$lat° $latitudePostfix, $lng° $longitudePostfix"
        countryUi.latitudeLongitude = latitudeLongitude

        countryUi.website = pin.url
    }

    private fun setCountryData(country: Country, countryUi: CountryUi) {
        countryUi.countryName = country.name
        countryUi.flagLink = country.flag
        countryUi.capital = country.capital
        countryUi.languages = country.language ?: emptyList()
        countryUi.currencies = country.currency ?: emptyList()

        countryUi.areaSquareKilometers = country.area
        countryUi.population = country.population
        countryUi.timezones = country.timezones ?: emptyList()

        var region = country.region
        country.subregion.let {
            region = region?.plus(" ($it)")
        }
        countryUi.region = region

        var isoCode = country.alpha2Code
        country.alpha3Code.let {
            isoCode = isoCode?.plus(" ($it)")
        }
        countryUi.isoCode = isoCode

        countryUi.callingCodes = country.callingCodes ?: emptyList()
        countryUi.domains = country.topLevelDomain ?: emptyList()
        countryUi.nativeName = country.nativeName
        countryUi.regionalBlocks = country.regionalBlock ?: emptyList()
    }
}
