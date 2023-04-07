package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
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
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoInfo
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.repository.DataRepositoryImpl
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import com.sjaindl.travelcompanion.sqldelight.DatabaseWrapper
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreDetailFlickrPhotosScreen(
    pinId: Long,
    photoType: PhotoType,
    viewModel: ExploreFlickrPhotosViewModel = viewModel(
        key = photoType.toString(),
        factory = ExploreFlickrPhotosViewModelFactory(
            pinId = pinId,
            photoType = photoType,
            // TODO: Introduce Hilt for DI
            dataRepository = DataRepositoryImpl(DatabaseWrapper(DatabaseDriverFactory(LocalContext.current)).dbQueries),
        )
    )
) {
    TravelCompanionTheme {
        val state by viewModel.state.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center,
        ) {
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
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colors.background),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                stickyHeader {
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
                                }

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
        }
    }
}


@Preview
@Composable
fun ExploreDetailFlickrPhotosScreenPreview() {
    TravelCompanionTheme {
        ExploreDetailFlickrPhotosScreen(pinId = 1, photoType = PhotoType.COUNTRY)
    }
}
