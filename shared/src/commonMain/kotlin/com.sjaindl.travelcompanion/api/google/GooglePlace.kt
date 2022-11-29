package com.sjaindl.travelcompanion.api.google

import com.sjaindl.travelcompanion.SecretConstants
import com.sjaindl.travelcompanion.api.Plannable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://developers.google.com/maps/documentation/places/web-service/search-nearby
@Serializable
class GooglePlace : Plannable {
    @SerialName("place_id")
    var placeId: String

    @SerialName("business_status")
    var businessStatus: String? = null
    var icon: String? = null
    var name: String
    var rating: Double? = null
    var reference: String
    var scope: String
    var types: List<String>? = null

    @SerialName("user_ratings_total")
    var userRatingsTotal: Int? = null
    var vicinity: String
    var geometry: Geometry? = null
    var photos: List<Photo>? = null

    @SerialName("plus_code")
    var plusCode: PlusCode? = null

    @SerialName("price_level")
    var priceLevel: Int? = null

    // 0: free, 1: inexpensive, 2: moderate, 3: expensive, 4: very Expensive
    @SerialName("html_attributions")
    var htmlAttributions: List<String>? = listOf()
    var notes: String? = null

    constructor(
        placeId: String,
        name: String,
        reference: String,
        scope: String,
        vicinity: String
    ) {
        this.placeId = placeId
        this.name = name
        this.placeId = placeId
        this.reference = reference
        this.scope = scope
        this.vicinity = vicinity
    }

    override fun getId(): String = placeId
    override fun description(): String = name

    // TODO:
    /*
    fun details(): NSMutableAttributedString {
        var details = NSMutableAttributedString(string = vicinity)
        val rating = rating
        if (rating != null) {
            details = NSMutableAttributedString(string = "${details.string}. ${rating}/5*")
        }
        val photos = photos
        val photoAttribution = photos[0].htmlAttributions?[0]?.htmlUnescape()
        val linkText = FormatUtils.getLinkAttributedText(photoAttribution)
        if (photos != null && photos.size > 0 && photoAttribution != null && linkText != null) {
            details.append(linkText)
        }
        return details
    }


    fun getLink(): String? {
        val photos = photos ?: return null

        val photoAttribution = photos[0].htmlAttributions?.firstOrNull()?.htmlUnescape()
        if (photos.isNotEmpty() && photoAttribution != null) {
            return FormatUtils.getLink(photoAttribution)
        }
        return null
    }

    fun getLinkText(): NSMutableAttributedString? {
        val photos = photos
        val photoAttribution = photos[0].htmlAttributions?[0]
        if (photos != null && photos.size > 0 && photoAttribution != null) {
            return FormatUtils.getLinkAttributedText(photoAttribution)
        }
        return null
    }
     */

    fun imageUrl(): String? {
        val photos = photos ?: return null

        val photoReference = photos[0].photoReference
        if (photos.isNotEmpty() && photoReference != null) {
            return "${GoogleConstants.UrlComponents.pathPhotos}?${GoogleConstants.ParameterKeys.maxWidth}=${GoogleConstants.ParameterValues.maxWidth}&${GoogleConstants.ParameterKeys.photoReference}=${photoReference}&${GoogleConstants.ParameterKeys.key}=${SecretConstants.apiKeyGooglePlaces}"
        }
        return ""
    }

    /*
    fun encode(): Map<String, Any> = FirestoreEncoder().encode(this)
    */
}

@Serializable
data class Geometry(var location: Location)

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
    var height: Int? = null,
    var width: Int? = null
)

@kotlinx.serialization.Serializable
data class PlusCode(
    @SerialName("compound_code")
    var compoundCode: String,
    @SerialName("global_code")
    var globalCode: String
)
