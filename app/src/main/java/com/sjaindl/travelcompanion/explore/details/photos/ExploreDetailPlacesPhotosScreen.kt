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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GooglePlacesClientImpl
import com.sjaindl.travelcompanion.exception.OfflineException
import com.sjaindl.travelcompanion.repository.DataRepositoryImpl
import com.sjaindl.travelcompanion.sqldelight.DatabaseDriverFactory
import com.sjaindl.travelcompanion.sqldelight.DatabaseWrapper
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExploreDetailPlacesPhotosScreen(
    pinId: Long,
    viewModel: ExplorePlacesPhotosViewModel = viewModel(
        factory = ExplorePlacesPhotosViewModelFactory(
            pinId = pinId,
            // TODO: Introduce Hilt for DI
            dataRepository = DataRepositoryImpl(DatabaseWrapper(DatabaseDriverFactory(LocalContext.current)).dbQueries),
            client = GooglePlacesClientImpl(LocalContext.current),
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
                ExplorePlacesPhotosViewModel.State.Loading -> {
                    LoadingAnimation()
                }
                is ExplorePlacesPhotosViewModel.State.Done -> {
                    val doneState = state as ExplorePlacesPhotosViewModel.State.Done
                    val photos = doneState.photos

                    if (photos.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.noPhotosFor, stringResource(id = R.string.place).lowercase()),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.background)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            stickyHeader {
                                Text(
                                    text = viewModel.place ?: stringResource(id = R.string.place),
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
                            photos.forEach {
                                item {
                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
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
