package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailFlickrPhotosScreen(
    modifier: Modifier = Modifier,
    showGrids: Boolean,
    pinId: Long,
    photoType: PhotoType,
    viewModel: ExploreFlickrPhotosViewModel = viewModel(
        key = photoType.toString(),
        factory = ExploreFlickrPhotosViewModelFactory(
            pinId = pinId,
            photoType = photoType,
            dataRepository = AndroidPersistenceInjector(LocalContext.current).shared.dataRepository,
        )
    )
) {
    TravelCompanionTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center,
        ) {
            if (showGrids) {
                /*
                TODO: Adapt to paging
                val state by viewModel.state.collectAsState()

                when (state) {
                    ExploreFlickrPhotosViewModel.State.Loading -> {
                        LoadingAnimation()
                    }

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

                            if (photoInfo.isNotEmpty()) {

                                // https://medium.com/mobile-app-development-publication/staggeredverticalgrid-of-android-jetpack-compose-fa565e5363e1
                                val place = viewModel.place ?: stringResource(id = R.string.place)
                                val placeText = stringResource(id = R.string.around, place)
                                val countryText = viewModel.country ?: stringResource(id = R.string.country)
                                val text = if (photoType == PhotoType.COUNTRY) countryText else placeText

                                Column {
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

                                    LazyVerticalStaggeredGrid(
                                        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(colors.background),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        photoInfo.forEach {
                                            item {
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
                                                        .padding(bottom = 12.dp),
                                                    painter = painter,
                                                    contentDescription = it.info,
                                                    alignment = Alignment.Center,
                                                    contentScale = ContentScale.FillWidth,
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                    is ExploreFlickrPhotosViewModel.State.Error -> {
                        val error = state as ExploreFlickrPhotosViewModel.State.Error

                        val errorMessage =
                            if (error.throwable is OfflineException) stringResource(id = R.string.offline)
                            else (error.throwable.localizedMessage ?: error.throwable.toString())

                        Text(
                            text = errorMessage,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }
                }
                */
            } else {
                ExploreDetailFlickrLazyColPhotosScreen(
                    modifier = Modifier,
                    photoType = photoType,
                    pinId = pinId,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailFlickrPhotosScreenPreview() {
    TravelCompanionTheme {
        ExploreDetailFlickrPhotosScreen(modifier = Modifier, pinId = 1, showGrids = false, photoType = PhotoType.COUNTRY)
    }
}
