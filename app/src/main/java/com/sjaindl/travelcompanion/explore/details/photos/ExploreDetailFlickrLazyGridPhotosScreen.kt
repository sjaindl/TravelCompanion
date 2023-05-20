package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.flickr.FlickrConstants
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoInfo
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import kotlinx.coroutines.launch
import timber.log.Timber

// https://stackoverflow.com/questions/73276953/android-jetpack-compose-pagination-pagination-not-working-with-staggered-layou
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreDetailFlickrLazyGridPhotosScreen(
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
    ),
    onGoToFullScreenPhoto: (url: String?, title: String) -> Unit,
) {
    val tag = "ExploreDetailFlickrLazyGridPhotosScreen"

    val state by viewModel.state.collectAsState()
    val photosCount by viewModel.photosCount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchNextPhotos() // initial fetch
    }

    val scope = rememberCoroutineScope()

    val gridState = rememberLazyStaggeredGridState()

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

            Column {
                val place = viewModel.place ?: stringResource(id = R.string.place)
                val placeText = stringResource(id = R.string.around, place)
                val countryText = viewModel.country ?: stringResource(id = R.string.country)
                val text = if (photoType == PhotoType.COUNTRY) countryText else placeText

                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(colors.background)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                when (val photoState = state) {
                    is ExploreFlickrPhotosViewModel.State.Done -> {
                        val doneState = state as ExploreFlickrPhotosViewModel.State.Done
                        val metaData = doneState.response.metaData
                        val photos = metaData.photos

                        if (photos.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.noPhotosFor, photoType.toString().lowercase()),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        } else {
                            val photoInfo = photos.mapNotNull {
                                val url = it.url ?: return@mapNotNull null
                                val height = it.height ?: return@mapNotNull null
                                val width = it.width ?: return@mapNotNull null
                                val info = "${it.title} - ${it.owner}"
                                return@mapNotNull PhotoInfo(url = url, height = height, width = width, info = info)
                            }

                            val endReached = remember {
                                derivedStateOf {
                                    Timber.tag(tag)
                                        .d("endReached: firstVisibleItemIndex >= photosCount - 2 * limit -> ${gridState.firstVisibleItemIndex} >= $photosCount - ${2 * FlickrConstants.ParameterValues.limit}")
                                    gridState.firstVisibleItemIndex >= photosCount - 2 * FlickrConstants.ParameterValues.limit
                                }
                            }

                            // https://medium.com/mobile-app-development-publication/staggeredverticalgrid-of-android-jetpack-compose-fa565e5363e1
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                                modifier = modifier
                                    .fillMaxSize()
                                    .background(colors.background),
                                state = gridState,
                                verticalItemSpacing = 4.dp,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {

                                items(photoInfo) {
                                    val model = ImageRequest.Builder(LocalContext.current)
                                        .data(it.url)
                                        .size(width = it.width, height = it.height)
                                        //.size(Size.ORIGINAL)
                                        .placeholder(android.R.drawable.gallery_thumb)
                                        .crossfade(true)
                                        .build()
                                    val painter = rememberAsyncImagePainter(model)

                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                            .clickable {
                                                /*
                                                onGoToFullScreenPhoto(
                                                    it.url,
                                                    it.info,
                                                )
                                                 */
                                                fullScreenImage = it.url
                                            },
                                        painter = painter,
                                        contentDescription = it.info,
                                        alignment = Alignment.Center,
                                        contentScale = ContentScale.FillWidth,
                                    )

                                }
                            }

                            if (endReached.value) {
                                Timber.tag(tag).d("endReached, fetch photos at offset ${viewModel.pageOffset}")
                                LaunchedEffect(key1 = viewModel.pageOffset) {
                                    scope.launch {
                                        viewModel.fetchNextPhotos()
                                    }
                                }
                            }
                        }
                    }

                    is ExploreFlickrPhotosViewModel.State.Error -> {
                        val throwable = photoState.throwable

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

                    else -> {
                        LoadingAnimation()
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun ExploreDetailFlickrLazyGridPhotosScreenPreview() {
    TravelCompanionTheme {
        ExploreDetailFlickrLazyGridPhotosScreen(
            modifier = Modifier,
            pinId = 1,
            photoType = PhotoType.COUNTRY,
            onGoToFullScreenPhoto = { _, _ -> }
        )
    }
}
