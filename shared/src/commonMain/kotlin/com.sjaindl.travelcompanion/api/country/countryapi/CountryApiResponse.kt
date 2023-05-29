package com.sjaindl.travelcompanion.api.country.countryapi

import com.sjaindl.travelcompanion.model.Currency
import com.sjaindl.travelcompanion.model.RegionalBlock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://countryapi.io/documentation
@Serializable
data class CountryApiResponse(
    val name: String? = null,
    @SerialName("official_name")
    val officialName: String? = null,
    val topLevelDomain: List<String>? = null,
    val alpha2Code: String? = null,
    val alpha3Code: String? = null,
    val cioc: String? = null,
    val numericCode: String? = null,
    val callingCode: String? = null,
    val capital: String? = null,
    val altSpellings: List<String>? = null,
    val region: String? = null,
    val subregion: String? = null,
    val population: Int? = null,
    val latLng: Map<String, List<Double>>? = null,
    val demonyms: Map<String, Map<String, String>>? = null,
    val area: Float? = null,
    //val gini: String? = null,
    val timezones: List<String>? = null,
    val borders: List<String>? = null,
    val nativeNames: Map<String, Map<String, String>>? = null,
    val currencies: Map<String, Currency>? = null,
    val languages: Map<String, String>? = null,
    val translations: Map<String, String>? = null,
    val flag: Map<String, String>? = null,
    val regionalBlocs: List<RegionalBlock>? = null,
)
