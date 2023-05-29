package com.sjaindl.travelcompanion.api.country.countryapi

import com.sjaindl.travelcompanion.util.Mockable

@Mockable
interface CountryApiClient {
    suspend fun fetchCountryDetails(countryCode: String): Result<CountryApiResponse>

    suspend fun fetchCountryDetailsLocal(countryCode: String): CountryApiResponse?
}
