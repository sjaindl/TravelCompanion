package com.sjaindl.travelcompanion.remember.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@Composable
fun RememberDetailPhotosScreen(
    modifier: Modifier = Modifier,
    showGrids: Boolean,
    photos: List<RememberPhoto>,
    planName: String,
    onShowActions: (Boolean) -> Unit,
    onDeleted: (String?) -> Unit,
    viewModel: RememberDetailPhotosViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val loadedPhotos by viewModel.loadedPhotosFlow.collectAsState()

    LaunchedEffect(key1 = photos) {
        viewModel.loadBitmaps(photos = photos)
    }

    TravelCompanionTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is RememberDetailPhotosViewModel.State.Error -> {
                    val exception = (state as RememberDetailPhotosViewModel.State.Error).exception

                    val errorMessage =
                        exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Text(
                        text = errorMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                    )
                }

                is RememberDetailPhotosViewModel.State.Info -> {
                    val info = (state as RememberDetailPhotosViewModel.State.Info)

                    Text(
                        text = stringResource(id = info.stringRes),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                    )
                }

                RememberDetailPhotosViewModel.State.Loaded -> {
                    if (showGrids) {
                        RememberDetailLazyColScreen(
                            modifier = Modifier,
                            loadedPhotos = loadedPhotos,
                            planName = planName,
                            onShowActions = onShowActions,
                            onDeleted = onDeleted,
                        )
                    } else {
                        RememberDetailLazyGridScreen(
                            modifier = Modifier,
                            loadedPhotos = loadedPhotos,
                            planName = planName,
                            onShowActions = onShowActions,
                            onDeleted = onDeleted,
                        )
                    }
                }

                RememberDetailPhotosViewModel.State.Loading -> {
                    LoadingAnimation()
                }
            }
        }
    }
}

@Preview
@Composable
fun RememberDetailPhotosScreenPreview() {
    TravelCompanionTheme {
        RememberDetailPhotosScreen(
            modifier = Modifier,
            showGrids = false,
            photos = listOf(
                RememberPhoto(
                    url = "gs://travel-compani.appspot.com/jgpIWLWXXpODa35SjVX3fpf8UCC3/plans/Bled/photos/Bled5851237769595690.jpg",
                    documentId = "QJmJsdJhsJvX7VoD4SxC",
                )
            ),
            planName = "Bled",
            onShowActions = { },
            onDeleted = { },
        )
    }
}
