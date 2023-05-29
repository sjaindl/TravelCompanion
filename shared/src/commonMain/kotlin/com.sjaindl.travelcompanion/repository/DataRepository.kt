package com.sjaindl.travelcompanion.repository

import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.country.countryapi.CountryApiResponse
import com.sjaindl.travelcompanion.api.country.restcountries.CountryResponse
import com.sjaindl.travelcompanion.model.Currency
import com.sjaindl.travelcompanion.model.Flag
import com.sjaindl.travelcompanion.model.Language
import com.sjaindl.travelcompanion.model.RegionalBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface DataRepository {
    fun singlePin(id: Long): Pin?
    fun singlePin(name: String): Pin?
    fun lastPinId(): Long?
    fun allPins(): List<Pin>
    fun pinsAsFlow(): Flow<List<Pin>>

    fun createPins(pins: List<Pin>)
    fun updatePins(pins: List<Pin>)
    fun updatePin(pin: Pin)
    fun insertPin(pin: Pin)
    fun insertPin(
        id: Long,
        address: String?,
        country: String?,
        countryCode: String?,
        creationDate: Instant?,
        latitude: Double?,
        longitude: Double?,
        name: String?,
        phoneNumber: String?,
        placeId: String?,
        rating: Double?,
        url: String?,
    )

    fun singleCountry(countryCode: String): Country?

    fun insertCountry(
        countryCode: String,
        name: String?,
        topLevelDomain: List<String>?,
        alpha2Code: String?,
        alpha3Code: String?,
        callingCodes: List<String>?,
        capital: String?,
        altSpellings: List<String>?,
        subregion: String?,
        region: String?,
        population: Int?,
        latlng: List<Double>?,
        demonym: String?,
        area: Float?,
        gini: Float?,
        timezones: List<String>?,
        borders: List<String>?,
        nativeName: String?,
        numericCode: String?,
        flags: Flag?,
        currency: List<Currency>?,
        language: List<Language>?,
        translations: Map<String, String>?,
        flag: String?,
        regionalBlock: List<RegionalBlock>?,
        cioc: String?,
        independent: Boolean?,
    )

    fun insertCountry(countryCode: String, country: CountryResponse)

    fun insertCountry(countryCode: String, country: CountryApiResponse)

    fun updateCountry(
        countryCode: String,
        name: String?,
        topLevelDomain: List<String>?,
        alpha2Code: String?,
        alpha3Code: String?,
        callingCodes: List<String>?,
        capital: String?,
        altSpellings: List<String>?,
        subregion: String?,
        region: String?,
        population: Int?,
        latlng: List<Double>?,
        demonym: String?,
        area: Float?,
        gini: Float?,
        timezones: List<String>?,
        borders: List<String>?,
        nativeName: String?,
        numericCode: String?,
        flags: Flag?,
        currency: List<Currency>?,
        language: List<Language>?,
        translations: Map<String, String>?,
        flag: String?,
        regionalBlock: List<RegionalBlock>?,
        cioc: String?,
        independent: Boolean?,
    )

    fun deletePin(id: Long)

    fun clearDatabase()
}
