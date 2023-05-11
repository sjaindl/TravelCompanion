package com.sjaindl.travelcompanion.plan

import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.api.Plannable
import com.sjaindl.travelcompanion.api.google.GooglePlace
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap

class PlannableFactory {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun makePlannable(type: String, data: Map<String, Any>): Plannable {
            val place: GooglePlace = when (type) {
                Constants.Plannables.attraction -> Properties.decodeFromMap(data)
                Constants.Plannables.hotel -> Properties.decodeFromMap(data)
                Constants.Plannables.restaurant -> Properties.decodeFromMap(data)
                else -> throw Exception("Plannable type not supported")
            }

            return place
        }
    }
}
