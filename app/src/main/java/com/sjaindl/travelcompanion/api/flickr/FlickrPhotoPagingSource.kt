package com.sjaindl.travelcompanion.api.flickr

import androidx.paging.PagingSource
import androidx.paging.PagingState
import timber.log.Timber

sealed class FlickrPhotosSourceType {
    data class Text(val text: String) : FlickrPhotosSourceType()

    data class Geo(val latitude: Double, val longitude: Double) : FlickrPhotosSourceType()
}

class FlickrPhotoPagingSource(
    private val client: FlickrClient,
    private val type: FlickrPhotosSourceType,
) : PagingSource<Int, FlickrPhoto>() {
    private val initialPage = 0
    private val tag = "FlickrPhotoPagingSource"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FlickrPhoto> {
        val page = params.key ?: initialPage
        val limit = params.loadSize

        Timber.tag(tag).d("load page $page")

        val response = when (type) {
            is FlickrPhotosSourceType.Text -> {
                client.fetchPhotos(text = type.text, offset = page, limit = limit)
            }

            is FlickrPhotosSourceType.Geo -> {
                client.fetchPhotos(
                    latitude = type.latitude,
                    longitude = type.longitude,
                    offset = page,
                    limit = limit,
                )
            }
        }

        response.onSuccess {
            val photos = it.metaData.photos
            Timber.tag(tag).d("Success - photos: ${photos.size}")

            return LoadResult.Page(
                data = photos,
                prevKey = if (page == initialPage) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1,
            )
        }.onFailure {
            Timber.tag(tag).d("Failure: $it")
            return LoadResult.Error(it)
        }

        return LoadResult.Invalid()
    }

    override fun getRefreshKey(state: PagingState<Int, FlickrPhoto>): Int? {
        val position = state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(other = 1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(other = 1)
        }

        Timber.tag(tag).d("getRefreshKey: $position")

        return position
    }
}
