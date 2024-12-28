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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation
import com.sjaindl.travelcompanion.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ExploreDetailPlacesPhotosScreen(
    modifier: Modifier = Modifier,
    showGrids: Boolean,
    pinId: Long,
    isPickerMode: Boolean,
    viewModel: ExplorePlacesPhotosViewModel = hiltViewModel(
        creationCallback = { factory: ExplorePlacesPhotosViewModelFactory ->
            factory.create(pinId = pinId)
        }
    ),
    onChoosePhoto: (bitmap: ImageBitmap) -> Unit,
) {
    TravelCompanionTheme {
        val state by viewModel.state.collectAsState()

        var fullScreenImage: ImageBitmap? by remember {
            mutableStateOf(null)
        }

        val listState = rememberLazyListState()
        val gridState = rememberLazyStaggeredGridState()

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            if (fullScreenImage != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PhotoFullScreen(bitmap = fullScreenImage, url = null, title = "") {
                        fullScreenImage = null
                    }
                }
            } else {
                when (state) {
                    ExplorePlacesPhotosViewModel.State.Loading -> {
                        LoadingAnimation()
                    }

                    is ExplorePlacesPhotosViewModel.State.Done -> {
                        val doneState = state as ExplorePlacesPhotosViewModel.State.Done
                        val photos = doneState.photos

                        if (photos.isEmpty()) {
                            Text(
                                text = stringResource(
                                    id = R.string.noPhotosFor,
                                    stringResource(id = R.string.place).lowercase()
                                ),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        } else {
                            if (showGrids) {
                                Column {
                                    Text(
                                        text = viewModel.place ?: stringResource(id = R.string.place),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier
                                            .background(colorScheme.background)
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp
                                    )

                                    LazyVerticalStaggeredGrid(
                                        columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(colorScheme.background),
                                        state = gridState,
                                        verticalItemSpacing = 4.dp,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        items(photos) {
                                            Column {
                                                Image(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 12.dp)
                                                        .clickable {
                                                            if (isPickerMode) {
                                                                onChoosePhoto(it.first.asImageBitmap())
                                                            } else {
                                                                fullScreenImage = it.first.asImageBitmap()
                                                            }
                                                        },
                                                    bitmap = it.first.asImageBitmap(),
                                                    contentDescription = it.second,
                                                )

                                                PlaceAttribution(link = it.second)
                                            }

                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(colorScheme.background)
                                        .fillMaxWidth(),
                                    state = listState,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    stickyHeader {
                                        Text(
                                            text = viewModel.place ?: stringResource(id = R.string.place),
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

                                    items(photos) {
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp)
                                                .clickable {
                                                    if (isPickerMode) {
                                                        onChoosePhoto(it.first.asImageBitmap())
                                                    } else {
                                                        fullScreenImage = it.first.asImageBitmap()
                                                    }
                                                },
                                            bitmap = it.first.asImageBitmap(),
                                            contentDescription = it.second,
                                        )

                                        PlaceAttribution(link = it.second)
                                    }
                                }
                            }
                        }
                    }

                    is ExplorePlacesPhotosViewModel.State.Error -> {
                        val error = state as ExplorePlacesPhotosViewModel.State.Error

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
}
