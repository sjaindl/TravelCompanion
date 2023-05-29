package com.sjaindl.travelcompanion.api.country.restcountries

import com.sjaindl.travelcompanion.util.Mockable

@Mockable
interface RestCountriesClient {
    suspend fun fetchCountryDetails(countryCode: String): Result<CountryResponse>
}
