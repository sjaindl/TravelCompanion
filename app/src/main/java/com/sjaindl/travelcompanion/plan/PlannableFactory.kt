package com.sjaindl.travelcompanion.plan

import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.api.google.Photo
import com.sjaindl.travelcompanion.api.google.Plannable
import com.sjaindl.travelcompanion.api.google.asPlannable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap

class PlannableFactory {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun makePlannable(type: String, data: Map<String, Any>): Plannable {
            val place: GooglePlace = when (type) {
                Constants.Plannables.attraction -> {
                    Properties.decodeFromMap(data)
                }

                Constants.Plannables.hotel -> Properties.decodeFromMap(data)
                Constants.Plannables.restaurant -> Properties.decodeFromMap(data)
                else -> throw Exception("Plannable type not supported")
            }

            // Properties.decodeFromMap only supports flat types so far..
            val photoList = data["photos"] as? List<Map<String, Any>>?
            place.photos = photoList?.mapNotNull {
                Photo.customDecode(photos = it)
            } ?: emptyList()

            place.htmlAttributions = data["html_attributions"] as? List<String>
            place.types = data["types"] as? List<String>

            return place.asPlannable()
        }
    }
}
