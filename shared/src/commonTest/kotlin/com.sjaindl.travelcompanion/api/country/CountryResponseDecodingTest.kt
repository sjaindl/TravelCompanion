package com.sjaindl.travelcompanion.api.country

import com.goncalossilva.resources.Resource
import com.sjaindl.travelcompanion.api.country.restcountries.CountryResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

class CountryResponseDecodingTest {

    @Test
    fun testCountryResponseDecoding() {
        val json = Resource("src/commonTest/resources/country_response.json").readText()
        assertNotNull(json)

        try {
            val country: CountryResponse? = Json.decodeFromString(json)
            assertNotNull(country)
            assertEquals("Saudi Arabia", country.name)

            val currency = country.currencies?.firstOrNull()
            assertNotNull(currency)
            assertEquals("SAR", currency.code)
            assertEquals("Saudi riyal", currency.name)
            assertEquals("ر.س", currency.symbol)
        } catch (exception: Exception) {
            fail(exception.message)
        }
    }
}
