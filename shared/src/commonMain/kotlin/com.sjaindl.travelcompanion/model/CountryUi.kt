package com.sjaindl.travelcompanion.model

data class CountryUi(
    val countryCode: String,
    var phoneNumber: String? = null,
    var website: String? = null,
    var countryName: String? = null,
    var placeName: String? = null,
    var capital: String? = null,
    var flagLink: String? = null,
    var areaSquareKilometers: Float? = null,
    var languages: List<Language> = emptyList(),
    var currencies: List<Currency> = emptyList(),
    var population: Int? = null,
    var timezones: List<String> = emptyList(),
    var region: String? = null,
    var isoCode: String? = null,
    var callingCodes: List<String> = emptyList(),
    var domains: List<String> = emptyList(),
    var nativeName: String? = null,
    var regionalBlocks: List<RegionalBlock> = emptyList(),
) {

    lateinit var latitudeLongitude: String
}
