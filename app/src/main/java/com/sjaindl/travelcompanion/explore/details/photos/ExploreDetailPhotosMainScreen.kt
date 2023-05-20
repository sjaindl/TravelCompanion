package com.sjaindl.travelcompanion.explore.details.photos

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.explore.details.tabnav.DetailsTabBarLayout
import com.sjaindl.travelcompanion.explore.details.tabnav.TabItem
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreDetailPhotosMainScreen(
    pinId: Long,
    onGoToFullScreenPhoto: (bitmap: ImageBitmap?, url: String?, title: String) -> Unit,
) {
    var showGrids by remember { mutableStateOf(false) }

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
                            onGoToFullScreenPhoto = { url, title ->
                                onGoToFullScreenPhoto(null, url, title)
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
                            onGoToFullScreenPhoto = { url, title ->
                                onGoToFullScreenPhoto(null, url, title)
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
                            onGoToFullScreenPhoto = { url, title ->
                                onGoToFullScreenPhoto(null, url, title)
                            },
                        )
                    }
                }
            },
            icon = Icons.Rounded.LocationCity,
        )
    )

    TravelCompanionTheme {
        DetailsTabBarLayout(tabRowItems = tabRowItems, userScrollEnabled = true)
    }
}

@Preview
@Composable
fun ExploreDetailPhotosMainScreenPreview() {
    ExploreDetailPhotosMainScreen(
        pinId = 1,
        onGoToFullScreenPhoto = { bitmap, url, title ->

        }
    )
}
