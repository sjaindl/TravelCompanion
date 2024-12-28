package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.R
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
    isPickerMode: Boolean,
    viewModel: ExploreFlickrPhotosViewModel = hiltViewModel(
        key = photoType.toString(),
        creationCallback = { factory: ExploreFlickrPhotosViewModelFactory ->
            factory.create(pinId = pinId, photoType = photoType)
        },
    ),
    onChoosePhoto: (url: String?) -> Unit,
) {
    val pagingData = viewModel.fetchPhotosFlow().collectAsLazyPagingItems()

    val state = rememberLazyListState()

    var fullScreenImage: String? by remember {
        mutableStateOf(null)
    }

    TravelCompanionTheme {
        if (fullScreenImage != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                PhotoFullScreen(bitmap = null, url = fullScreenImage, title = "") {
                    fullScreenImage = null
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(colorScheme.background),
                state = state,
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
                                .background(colorScheme.background)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }

                items(
                    count = pagingData.itemCount,
                    /*
                    Check: java.lang.IllegalArgumentException: Key "FlickrPhoto(id=28502378924, owner=132366789@N03, secret=c4cb6f1f9f, server=8211, farm=9, title=Seychellen, isPublic=BooleanInt(value=true), isFriend=BooleanInt(value=false), isFamily=BooleanInt(value=false), url=https://live.staticflickr.com/8211/28502378924_c4cb6f1f9f.jpg, height=335, width=500)" was already used. If you are using LazyColumn/Row please make sure you provide a unique key for each item.
                    key = pagingData.itemKey { item ->
                        item.toString()
                    },
                     */
                ) { index ->
                    val item = pagingData[index]
                    item?.let { photo ->
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(photo.url)
                            .size(Size.ORIGINAL)
                            .placeholder(android.R.drawable.gallery_thumb)
                            .crossfade(enable = true)
                            .build()

                        val painter = rememberAsyncImagePainter(model)

                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    if (isPickerMode) {
                                        onChoosePhoto(photo.url)
                                    } else {
                                        fullScreenImage = photo.url
                                    }

                                    // There's an issue that paging is data always reloaded when navigating to another
                                    // composable with paging3 inside a nav host - disabling for now:
                                    // https://issuetracker.google.com/issues/177245496?pli=1

                                    /*
                                    onGoToFullScreenPhoto(
                                        photo.url,
                                        photo.title,
                                    )
                                     */
                                },
                            painter = painter,
                            contentDescription = photo.title,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.FillWidth,
                        )
                    }
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
                    fontSize = 20.sp,
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
        ExploreDetailFlickrLazyColPhotosScreen(
            modifier = Modifier,
            pinId = 1,
            photoType = PhotoType.COUNTRY,
            isPickerMode = false,
            onChoosePhoto = { _ -> },
        )
    }
}
