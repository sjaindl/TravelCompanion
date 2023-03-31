package com.sjaindl.travelcompanion.api.serialization

import kotlinx.serialization.Serializable

@Serializable(with = BooleanIntSerializer::class)
data class BooleanInt(val value: Boolean)
