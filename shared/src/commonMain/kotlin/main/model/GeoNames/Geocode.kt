package main.model.GeoNames

import kotlinx.serialization.Serializable

@Serializable
data class Geocode (
    val languages: String,
    val countryCode: String,
    val countryName: String
)
