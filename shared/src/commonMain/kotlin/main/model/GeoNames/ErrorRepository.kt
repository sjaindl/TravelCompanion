package main.model.GeoNames

import kotlinx.serialization.Serializable

@Serializable
data class ErrorRepository(val status: Int, val error: String)
