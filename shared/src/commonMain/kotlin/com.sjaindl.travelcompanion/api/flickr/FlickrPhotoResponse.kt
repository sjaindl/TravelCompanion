package com.sjaindl.travelcompanion.api.flickr

import com.sjaindl.travelcompanion.api.serialization.BooleanInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlickrPhotoResponse(
    @SerialName("photos")
    val metaData: FlickrPhotosMetaData,
)

@Serializable
data class FlickrPhotosMetaData(
    val page: Int,
    val pages: Int,
    @SerialName("perpage")
    val perPage: Int,
    val total: Int,
    @SerialName("photo")
    var photos: List<FlickrPhoto>
)

@Serializable
data class FlickrPhoto(
    val id: String,
    val owner: String,
    val secret: String,
    val server: Int,
    val farm: Int,
    val title: String,
    @SerialName("ispublic")
    val isPublic: BooleanInt,
    @SerialName("isfriend")
    val isFriend: BooleanInt,
    @SerialName("isfamily")
    val isFamily: BooleanInt,
    @SerialName("url_m")
    val url: String? = null,
    @SerialName("height_m")
    val height: Int? = null,
    @SerialName("width_m")
    val width: Int? = null,
)
