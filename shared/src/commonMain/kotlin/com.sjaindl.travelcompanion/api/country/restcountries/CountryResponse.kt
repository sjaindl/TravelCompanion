package com.sjaindl.travelcompanion.api.country.restcountries

import com.sjaindl.travelcompanion.model.Currency
import com.sjaindl.travelcompanion.model.Flag
import com.sjaindl.travelcompanion.model.Language
import com.sjaindl.travelcompanion.model.RegionalBlock
import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(
    val name: String? = null,
    val topLevelDomain: List<String>? = null,
    val alpha2Code: String? = null,
    val alpha3Code: String? = null,
    val callingCodes: List<String>? = null,
    val capital: String? = null,
    val altSpellings: List<String>? = null,
    val subregion: String? = null,
    val region: String? = null,
    val population: Int? = null,
    val latlng: List<Double>? = null,
    val demonym: String? = null,
    val area: Float? = null,
    val gini: Float? = null,
    val timezones: List<String>? = null,
    val borders: List<String>? = null,
    val nativeName: String? = null,
    val numericCode: String? = null,
    val flags: Flag? = null,
    val currencies: List<Currency>? = null,
    val languages: List<Language>? = null,
    val translations: Map<String, String>? = null,
    val flag: String? = null,
    val regionalBlocs: List<RegionalBlock>? = null,
    val cioc: String? = null,
    val independent: Boolean? = null,
)
