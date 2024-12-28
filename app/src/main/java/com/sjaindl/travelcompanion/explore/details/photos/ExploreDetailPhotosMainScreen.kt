package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.GridOff
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.explore.details.tabnav.DetailsTabBarLayout
import com.sjaindl.travelcompanion.explore.details.tabnav.TabItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreDetailPhotosMainScreen(
    pinId: Long,
    isPickerMode: Boolean,
    onPhotoChosen: () -> Unit,
    modifier: Modifier = Modifier,
    isChoosePlanImageMode: Boolean,
    viewModel: ExploreDetailPhotosViewModel = hiltViewModel(
        creationCallback = { factory: ExploreDetailPhotosViewModelFactory ->
            factory.create(pinId, isChoosePlanImageMode)
        },
    ),
) {
    var showGrids by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    val tabRowItems = listOf(
        TabItem(
            title = stringResource(id = R.string.country),
            screen = {
                TravelCompanionTheme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                showGrids = !showGrids
                            }) {
                                Icon(
                                    imageVector = if (showGrids) Icons.Rounded.GridOn else Icons.Rounded.GridOff,
                                    contentDescription = null,
                                )
                            }
                        },
                    ) {
                        ExploreDetailFlickrPhotosScreen(
                            modifier = Modifier.padding(paddingValues = it),
                            showGrids = showGrids,
                            pinId = pinId,
                            photoType = PhotoType.COUNTRY,
                            isPickerMode = isPickerMode,
                            onChoosePhoto = { url ->
                                viewModel.persistPlan(bitmap = null, url = url)
                            }
                        )
                    }
                }

            },
            icon = Icons.Rounded.EmojiFlags,
        ),
        TabItem(
            title = stringResource(id = R.string.place),
            screen = {
                TravelCompanionTheme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                showGrids = !showGrids
                            }) {
                                Icon(
                                    imageVector = if (showGrids) Icons.Rounded.GridOn else Icons.Rounded.GridOff,
                                    contentDescription = null,
                                )
                            }
                        },
                    ) {
                        ExploreDetailPlacesPhotosScreen(
                            modifier = Modifier.padding(paddingValues = it),
                            showGrids = showGrids,
                            pinId = pinId,
                            isPickerMode = isPickerMode,
                            onChoosePhoto = { bitmap ->
                                viewModel.persistPlan(bitmap = bitmap, url = null)
                            },
                        )
                    }
                }

            },
            icon = Icons.Rounded.Place,
        ),
        TabItem(
            title = stringResource(id = R.string.location),
            screen = {
                TravelCompanionTheme {
                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                showGrids = !showGrids
                            }) {
                                Icon(
                                    imageVector = if (showGrids) Icons.Rounded.GridOn else Icons.Rounded.GridOff,
                                    contentDescription = null,
                                )
                            }
                        },
                    ) {
                        ExploreDetailFlickrPhotosScreen(
                            modifier = Modifier.padding(paddingValues = it),
                            showGrids = showGrids,
                            pinId = pinId,
                            photoType = PhotoType.LOCATION,
                            isPickerMode = isPickerMode,
                            onChoosePhoto = { url ->
                                viewModel.persistPlan(bitmap = null, url = url)
                            },
                        )
                    }
                }
            },
            icon = Icons.Rounded.LocationCity,
        )
    )

    TravelCompanionTheme {
        if (!isChoosePlanImageMode) {
            DetailsTabBarLayout(tabRowItems = tabRowItems, userScrollEnabled = true)
        } else {
            when (state) {
                is ExploreDetailPhotosViewModel.State.Error -> {
                    val exception = (state as ExploreDetailPhotosViewModel.State.Error).exception

                    val errorMessage =
                        exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = errorMessage,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 20.sp,
                        )
                    }
                }

                is ExploreDetailPhotosViewModel.State.Info -> {
                    val info = (state as ExploreDetailPhotosViewModel.State.Info)

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(all = 16.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(id = info.stringRes),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            fontSize = 20.sp,
                        )
                    }
                }

                is ExploreDetailPhotosViewModel.State.Loaded -> {
                    DetailsTabBarLayout(tabRowItems = tabRowItems, userScrollEnabled = true)
                }

                ExploreDetailPhotosViewModel.State.Loading -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .background(colorScheme.background)
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingAnimation()
                    }
                }

                is ExploreDetailPhotosViewModel.State.PhotoChosen -> {
                    onPhotoChosen()
                }
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailPhotosMainScreenPreview() {
    ExploreDetailPhotosMainScreen(
        pinId = 1,
        isPickerMode = false,
        onPhotoChosen = { },
        isChoosePlanImageMode = false,
    )
}
