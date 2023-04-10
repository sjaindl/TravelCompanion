package com.sjaindl.travelcompanion.api.flickr

import com.sjaindl.travelcompanion.util.Mockable

@Mockable
interface FlickrClient {
    suspend fun fetchPhotos(text: String): Result<FlickrPhotoResponse>

    suspend fun fetchPhotos(latitude: Double, longitude: Double): Result<FlickrPhotoResponse>
}
