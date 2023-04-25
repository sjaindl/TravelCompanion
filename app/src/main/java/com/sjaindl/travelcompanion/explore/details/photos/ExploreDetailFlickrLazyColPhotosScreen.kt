package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreDetailFlickrLazyColPhotosScreen(
    modifier: Modifier = Modifier,
    photoType: PhotoType,
    pinId: Long,
    viewModel: ExploreFlickrPhotosViewModel = viewModel(
        key = photoType.toString(),
        factory = ExploreFlickrPhotosViewModelFactory(
            pinId = pinId,
            photoType = photoType,
            dataRepository = AndroidPersistenceInjector(LocalContext.current).shared.dataRepository,
        )
    )
) {
    val pagingData = viewModel.fetchPhotosFlow().collectAsLazyPagingItems()

    TravelCompanionTheme {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {

            stickyHeader {
                if (pagingData.loadState.append.endOfPaginationReached && pagingData.itemCount == 0) {
                    Text(
                        text = stringResource(id = R.string.noPhotosFor, photoType.toString().lowercase()),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                } else {
                    val place = viewModel.place ?: stringResource(id = R.string.place)
                    val placeText = stringResource(id = R.string.around, place)
                    val countryText = viewModel.country ?: stringResource(id = R.string.country)
                    val text = if (photoType == PhotoType.COUNTRY) countryText else placeText

                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(MaterialTheme.colors.background)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )
                }
            }

            itemsIndexed(
                items = pagingData,
                key = { index, item ->
                    "$index-${item.id}"
                },
            ) { _, item ->
                item?.let { photo ->
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.url)
                        //.size(width = photo.width!!, height = photo.height!!)
                        .size(Size.ORIGINAL)
                        .placeholder(android.R.drawable.gallery_thumb)
                        .crossfade(true)
                        .build()

                    val painter = rememberAsyncImagePainter(model)

                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        painter = painter,
                        contentDescription = photo.title,
                        alignment = Alignment.Center,
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }
        }

        when (val refreshState = pagingData.loadState.refresh) {
            is LoadState.NotLoading -> {
                // no op
            }

            is LoadState.Loading -> {
                LoadingAnimation()
            }

            is LoadState.Error -> {
                val throwable = refreshState.error

                val errorMessage =
                    if (throwable is OfflineException) stringResource(id = R.string.offline)
                    else (throwable.localizedMessage ?: throwable.toString())

                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        }

        when (val appendState = pagingData.loadState.append) {
            is LoadState.NotLoading -> {
                /* no op */
            }

            is LoadState.Error -> {
                val throwable = appendState.error

                val errorMessage =
                    if (throwable is OfflineException) stringResource(id = R.string.offline)
                    else (throwable.localizedMessage ?: throwable.toString())

                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
            is LoadState.Loading -> {
                LoadingAnimation()
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailFlickrLazyColPhotosScreenPreview() {
    TravelCompanionTheme {
        ExploreDetailFlickrLazyColPhotosScreen(modifier = Modifier, pinId = 1, photoType = PhotoType.COUNTRY)
    }
}
