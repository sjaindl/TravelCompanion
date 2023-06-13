package com.sjaindl.travelcompanion.api.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
@Serializable
data class GooglePlace(
    @SerialName("id")
    var id: String? = null,

    @SerialName("place_id")
    var placeId: String,

    @SerialName("business_status")
    var businessStatus: String? = null,
    var icon: String? = null,
    var name: String,
    var rating: Double? = null,
    var reference: String,
    var scope: String,
    var types: List<String>? = null,

    @SerialName("user_ratings_total")
    var userRatingsTotal: Long? = null,
    var vicinity: String? = null,
    var geometry: Geometry? = null,
    var photos: List<Photo>? = null,

    @SerialName("plus_code")
    var plusCode: PlusCode? = null,

    @SerialName("price_level")
    var priceLevel: Long? = null,

    // 0: free, 1: inexpensive, 2: moderate, 3: expensive, 4: very Expensive
    @SerialName("html_attributions")
    var htmlAttributions: List<String>? = listOf(),
    var notes: String? = null,
)

@Serializable
data class Geometry(var location: Location? = null)

@Serializable
data class Location(
    var lat: Double? = null,
    var lng: Double? = null
)

@Serializable
data class Photo(
    @SerialName("photo_reference")
    var photoReference: String? = null,
    @SerialName("html_attributions")
    var htmlAttributions: List<String>? = null,
    var height: Long? = null,
    var width: Long? = null
) {
    companion object {
        fun customDecode(photos: Map<String, Any>?): Photo? {
            val photoRef = photos?.get("photo_reference") as? String
            val attributions = photos?.get("html_attributions") as? List<String>
            val height = photos?.get("height") as? Long
            val width = photos?.get("width") as? Long

            if (photoRef == null) return null

            //val photo: Photo? = Properties.decodeFromMap(it) .. not working due to unsupported list inside
            return Photo(
                photoReference = photoRef,
                htmlAttributions = attributions,
                height = height,
                width = width,
            )
        }
    }
}

@Serializable
data class PlusCode(
    @SerialName("compound_code")
    var compoundCode: String? = null,
    @SerialName("global_code")
    var globalCode: String? = null
)
