package com.sjaindl.travelcompanion.explore.details.photos

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
                ExploreDetailFlickrLazyGridPhotosScreen(
                    modifier = Modifier,
                    photoType = photoType,
                    pinId = pinId,
                    viewModel = viewModel,
                )
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
