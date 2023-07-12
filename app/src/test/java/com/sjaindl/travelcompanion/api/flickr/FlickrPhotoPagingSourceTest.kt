package com.sjaindl.travelcompanion.api.flickr

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.google.common.truth.Truth.assertThat
import com.sjaindl.travelcompanion.api.serialization.BooleanInt
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FlickrPhotoPagingSourceTest {
    private val client: FlickrClient = mockk()

    private val searchText = "Austria"
    private val limit = 10

    private val mockedPhotos = (1..20).map {
        FlickrPhoto(
            id = it.toString(),
            owner = "me",
            secret = "secret text",
            server = 0,
            farm = 0,
            title = "photo $it",
            isPublic = BooleanInt(true),
            isFriend = BooleanInt(true),
            isFamily = BooleanInt(true),
        )
    }

    @Before
    fun setup() {
        listOf(0, 1).forEach { offset ->
            coEvery {
                client.fetchPhotos(text = searchText, offset = offset, limit = any())
            } returns Result.success(
                FlickrPhotoResponse(
                    FlickrPhotosMetaData(
                        page = offset,
                        pages = 2,
                        perPage = limit,
                        total = 2 * limit,
                        photos = mockedPhotos.subList(fromIndex = offset * 10, toIndex = offset * 10 + 9)
                    )
                )
            )
        }
    }

    @Test
    fun testInitialLoad() = runTest {
        val pagingSource = FlickrPhotoPagingSource(
            client = client,
            type = FlickrPhotosSourceType.Text("Austria"),
        )

        val pager = TestPager(
            config = PagingConfig(pageSize = limit),
            pagingSource = pagingSource,
        )

        val result = pager.refresh() as LoadResult.Page

        val initialPaginatedPhotos = mockedPhotos.subList(0, 9)

        assertThat(result.data)
            .containsExactlyElementsIn(initialPaginatedPhotos)
            .inOrder()
    }

    @Test
    fun testConsecutiveLoads() = runTest {
        val pagingSource = FlickrPhotoPagingSource(
            client = client,
            type = FlickrPhotosSourceType.Text("Austria"),
        )

        val pager = TestPager(
            config = PagingConfig(pageSize = FlickrConstants.ParameterValues.limit),
            pagingSource = pagingSource,
        )

        val result = with(pager) {
            refresh()
            append()
        } as LoadResult.Page

        val nextPhotos = mockedPhotos.subList(10, 19)

        assertThat(result.data)
            .containsExactlyElementsIn(nextPhotos)
            .inOrder()
    }
}
