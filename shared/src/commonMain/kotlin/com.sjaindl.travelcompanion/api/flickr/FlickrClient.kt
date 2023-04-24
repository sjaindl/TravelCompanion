package com.sjaindl.travelcompanion.api.flickr

import com.sjaindl.travelcompanion.util.Mockable

@Mockable
interface FlickrClient {
    suspend fun fetchPhotos(text: String, offset: Int, limit: Int): Result<FlickrPhotoResponse>

    suspend fun fetchPhotos(latitude: Double, longitude: Double, offset: Int, limit: Int): Result<FlickrPhotoResponse>
}
