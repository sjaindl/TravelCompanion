package com.sjaindl.travelcompanion.api.flickr

import androidx.paging.Pager
import androidx.paging.PagingConfig

class FlickrRepositoryImpl(private val client: FlickrClient) : FlickrRepository {

    override fun fetchPhotos(text: String) = Pager(
        config = PagingConfig(
            pageSize = FlickrConstants.ParameterValues.limit
        ),
        pagingSourceFactory = {
            FlickrPhotoPagingSource(client, FlickrPhotosSourceType.Text(text = text))
        }
    ).flow

    override fun fetchPhotos(latitude: Double, longitude: Double) = Pager(
        config = PagingConfig(
            pageSize = FlickrConstants.ParameterValues.limit
        ),
        pagingSourceFactory = {
            FlickrPhotoPagingSource(client, FlickrPhotosSourceType.Geo(latitude = latitude, longitude = longitude))
        }
    ).flow
}
