package main.model.GeoNames

import kotlinx.serialization.Serializable

@Serializable
data class GeoCodeRepository(val countryCode: String)