package com.sjaindl.travelcompanion.repository

import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.TravelCompanionDatabaseQueries
import com.sjaindl.travelcompanion.api.country.countryapi.CountryApiResponse
import com.sjaindl.travelcompanion.api.country.restcountries.CountryResponse
import com.sjaindl.travelcompanion.model.Currency
import com.sjaindl.travelcompanion.model.Flag
import com.sjaindl.travelcompanion.model.Language
import com.sjaindl.travelcompanion.model.RegionalBlock
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

open class DataRepositoryImpl(private val dbQueries: TravelCompanionDatabaseQueries) : DataRepository {
    override fun singlePin(id: Long): Pin? {
        return dbQueries.selectPinById(id).executeAsList().lastOrNull()
    }

    override fun singlePin(name: String): Pin? {
        return dbQueries.selectPinByName(name).executeAsList().lastOrNull()
    }

    override fun lastPinId(): Long? {
        return dbQueries.selectLastId().executeAsOneOrNull()?.MAX
    }

    override fun allPins(): List<Pin> {
        return dbQueries.selectAllPins().executeAsList()
    }

    override fun pinsAsFlow(): Flow<List<Pin>> {
        return dbQueries.selectAllPins()
            .asFlow()
            .mapToList()
    }

    override fun createPins(pins: List<Pin>) {
        dbQueries.transaction {
            pins.forEach {
                insertPin(it)
            }
        }
    }

    override fun updatePins(pins: List<Pin>) {
        dbQueries.transaction {
            pins.forEach {
                updatePin(it)
            }
        }
    }

    override fun insertPin(
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
    ) {
        dbQueries.insertPin(
            id = if (id > 0) id else null, // if passing null, we autoincrement id
            address = address,
            country = country,
            countryCode = countryCode,
            creationDate = creationDate ?: Clock.System.now(),
            latitude = latitude,
            longitude = longitude,
            name = name,
            phoneNumber = phoneNumber,
            placeId = placeId,
            rating = rating,
            url = url,
        )
    }

    override fun singleCountry(countryCode: String): Country? {
        return dbQueries.selectCountryByCountryCode(countryCode = countryCode).executeAsOneOrNull()
    }

    override fun insertCountry(
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
        independent: Boolean?
    ) {
        dbQueries.insertCountry(
            countryCode = countryCode,
            name = name,
            topLevelDomain = topLevelDomain,
            alpha2Code = alpha2Code,
            alpha3Code = alpha3Code,
            callingCodes = callingCodes,
            capital = capital,
            altSpellings = altSpellings,
            subregion = subregion,
            region = region,
            population = population,
            latlng = latlng,
            demonym = demonym,
            area = area,
            gini = gini,
            timezones = timezones,
            borders = borders,
            nativeName = nativeName,
            numericCode = numericCode,
            flags = flags,
            currency = currency,
            language = language,
            translations = translations,
            flag = flag,
            regionalBlock = regionalBlock,
            cioc = cioc,
            independent = independent,
        )
    }

    override fun insertCountry(countryCode: String, country: CountryResponse) {
        insertCountry(
            countryCode = countryCode,
            name = country.name,
            topLevelDomain = country.topLevelDomain,
            alpha2Code = country.alpha2Code,
            alpha3Code = country.alpha3Code,
            callingCodes = country.callingCodes,
            capital = country.capital,
            altSpellings = country.altSpellings,
            subregion = country.subregion,
            region = country.region,
            population = country.population,
            latlng = country.latlng,
            demonym = country.demonym,
            area = country.area,
            gini = country.gini,
            timezones = country.timezones,
            borders = country.borders,
            nativeName = country.nativeName,
            numericCode = country.numericCode,
            flags = country.flags,
            currency = country.currencies,
            language = country.languages,
            translations = country.translations,
            flag = country.flag,
            regionalBlock = country.regionalBlocs,
            cioc = country.cioc,
            independent = country.independent,
        )
    }

    override fun insertCountry(countryCode: String, country: CountryApiResponse) {
        insertCountry(
            countryCode = countryCode,
            name = country.name,
            topLevelDomain = country.topLevelDomain,
            alpha2Code = country.alpha2Code,
            alpha3Code = country.alpha3Code,
            callingCodes = country.callingCode?.let { listOf(it) },
            capital = country.capital,
            altSpellings = country.altSpellings,
            subregion = country.subregion,
            region = country.region,
            population = country.population,
            latlng = country.latLng?.get("country"),
            demonym = country.demonyms?.get("eng")?.get("m"),
            area = country.area,
            gini = null,
            timezones = country.timezones,
            borders = country.borders,
            nativeName = country.nativeNames?.values?.firstOrNull()?.get("official"),
            numericCode = country.numericCode,
            flags = country.flag?.get("large")?.let { Flag(png = it) },
            currency = country.currencies?.map { currency ->
                Currency(code = currency.key, name = currency.value.name, symbol = currency.value.symbol)
            },
            language = country.languages?.map {
                Language(iso639_1 = it.key, name = it.value)
            },
            translations = country.translations,
            flag = country.flag?.get("large"),
            regionalBlock = country.regionalBlocs,
            cioc = country.cioc,
            independent = null,
        )
    }

    override fun updateCountry(
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
        independent: Boolean?
    ) {
        dbQueries.updateCountry(
            countryCode = countryCode,
            name = name,
            topLevelDomain = topLevelDomain,
            alpha2Code = alpha2Code,
            alpha3Code = alpha3Code,
            callingCodes = callingCodes,
            capital = capital,
            altSpellings = altSpellings,
            subregion = subregion,
            region = region,
            population = population,
            latlng = latlng,
            demonym = demonym,
            area = area,
            gini = gini,
            timezones = timezones,
            borders = borders,
            nativeName = nativeName,
            numericCode = numericCode,
            flags = flags,
            currency = currency,
            language = language,
            translations = translations,
            flag = flag,
            regionalBlock = regionalBlock,
            cioc = cioc,
            independent = independent,
            countryCode_ = countryCode, // Where
        )
    }


    override fun insertPin(pin: Pin) {
        insertPin(
            id = pin.id,
            address = pin.address,
            country = pin.country,
            countryCode = pin.countryCode,
            creationDate = pin.creationDate,
            latitude = pin.latitude,
            longitude = pin.longitude,
            name = pin.name,
            phoneNumber = pin.phoneNumber,
            placeId = pin.placeId,
            rating = pin.rating,
            url = pin.url,
        )
    }

    override fun updatePin(pin: Pin) {
        dbQueries.updatePin(
            id = pin.id,
            address = pin.address,
            country = pin.country,
            countryCode = pin.countryCode,
            creationDate = pin.creationDate,
            latitude = pin.latitude,
            longitude = pin.longitude,
            name = pin.name,
            phoneNumber = pin.phoneNumber,
            placeId = pin.placeId,
            rating = pin.rating,
            url = pin.url,
            id_ = pin.id // Where
        )
    }

    override fun deletePin(id: Long) {
        dbQueries.removePinById(id)
    }

    override fun clearDatabase() {
        dbQueries.transaction {
            dbQueries.removeAllCountries()
            dbQueries.removeAllPins()
        }
    }
}
