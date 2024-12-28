package com.sjaindl.travelcompanion.explore.details.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailFlickrPhotosScreen(
    modifier: Modifier = Modifier,
    showGrids: Boolean,
    pinId: Long,
    photoType: PhotoType,
    isPickerMode: Boolean,
    viewModel: ExploreFlickrPhotosViewModel = hiltViewModel(
        key = photoType.toString(),
        creationCallback = { factory: ExploreFlickrPhotosViewModelFactory ->
            factory.create(
                pinId = pinId,
                photoType = photoType,
            )

        }
    ),
    onChoosePhoto: (url: String?) -> Unit,
) {
    TravelCompanionTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            if (showGrids) {
                ExploreDetailFlickrLazyGridPhotosScreen(
                    modifier = Modifier,
                    photoType = photoType,
                    pinId = pinId,
                    viewModel = viewModel,
                    isPickerMode = isPickerMode,
                    onChoosePhoto = { url ->
                        onChoosePhoto(url)
                    }
                )
            } else {
                ExploreDetailFlickrLazyColPhotosScreen(
                    modifier = Modifier,
                    photoType = photoType,
                    pinId = pinId,
                    viewModel = viewModel,
                    isPickerMode = isPickerMode,
                    onChoosePhoto = { url ->
                        onChoosePhoto(url)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ExploreDetailFlickrPhotosScreenPreview() {
    TravelCompanionTheme {
        ExploreDetailFlickrPhotosScreen(
            modifier = Modifier,
            pinId = 1,
            showGrids = false,
            photoType = PhotoType.COUNTRY,
            isPickerMode = false,
            onChoosePhoto = { _ -> },
        )
    }
}
