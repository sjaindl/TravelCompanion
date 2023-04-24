package com.sjaindl.travelcompanion.api.flickr

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface FlickrRepository {
    fun fetchPhotos(text: String): Flow<PagingData<FlickrPhoto>>

    fun fetchPhotos(latitude: Double, longitude: Double): Flow<PagingData<FlickrPhoto>>
}
