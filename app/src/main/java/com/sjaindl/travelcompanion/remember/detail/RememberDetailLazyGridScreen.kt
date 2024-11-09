package com.sjaindl.travelcompanion.remember.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.photos.PhotoFullScreen
import com.sjaindl.travelcompanion.remember.detail.bottomsheet.RememberItemActionBottomSheet
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun RememberDetailLazyGridScreen(
    modifier: Modifier = Modifier,
    loadedPhotos: List<LoadedPhoto>,
    planName: String,
    onShowActions: (Boolean) -> Unit,
    onDeleted: (String?) -> Unit,
    viewModel: RememberDetailLazyScreenViewModel = hiltViewModel(
        creationCallback = { factory: RememberDetailLazyScreenViewModel.RememberDetailLazyScreenViewModelFactory ->
            factory.create(planName = planName)
        }
    ),
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyStaggeredGridState()
    val showDialogState by viewModel.showDialog.collectAsState()

    var fullScreenImage: Bitmap? by remember {
        mutableStateOf(null)
    }

    TravelCompanionTheme {
        if (showDialogState != null) {
            RememberItemActionBottomSheet(
                title = stringResource(id = R.string.chooseAction),
                onFullScreen = {
                    onShowActions(true)
                    fullScreenImage = viewModel.showDialog.value?.bitmap
                    viewModel.onDismiss()
                },
                onDelete = {
                    onShowActions(true)
                    onDeleted(showDialogState?.documentId)
                    viewModel.onDelete()
                },
                onCancel = {
                    onShowActions(true)
                    viewModel.onDismiss()
                }
            )
        }

        when (state) {
            is RememberDetailLazyScreenViewModel.State.Error -> {
                val exception = (state as RememberDetailLazyScreenViewModel.State.Error).exception

                val errorMessage =
                    exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.couldNotRetrieveData)

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = errorMessage,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                    )
                }
            }

            is RememberDetailLazyScreenViewModel.State.Info -> {
                val info = (state as RememberDetailLazyScreenViewModel.State.Info)

                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = info.stringRes),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 20.sp,
                    )
                }
            }

            RememberDetailLazyScreenViewModel.State.InitialOrDone -> {
                if (fullScreenImage != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PhotoFullScreen(bitmap = fullScreenImage?.asImageBitmap(), url = null, title = "") {
                            fullScreenImage = null
                        }
                    }
                } else {
                    Column {
                        if (loadedPhotos.isEmpty()) {
                            Box(
                                modifier = modifier
                                    .fillMaxSize()
                                    .background(colorScheme.background),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.noImageData),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 20.sp,
                                )
                            }
                        } else {
                            LazyVerticalStaggeredGrid(
                                columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                                modifier = modifier
                                    .fillMaxSize()
                                    .background(colorScheme.background),
                                state = gridState,
                                verticalItemSpacing = 4.dp,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                items(loadedPhotos) { photo ->
                                    val model = ImageRequest.Builder(LocalContext.current)
                                        .data(photo.bitmap)
                                        .size(Size.ORIGINAL)
                                        .placeholder(android.R.drawable.gallery_thumb)
                                        .crossfade(true)
                                        .build()
                                    val painter = rememberAsyncImagePainter(model)

                                    Image(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                            .clickable {
                                                onShowActions(false)
                                                viewModel.clickedOnImage(photo)
                                            },
                                        painter = painter,
                                        contentDescription = null,
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
    }
}

@Preview
@Composable
fun ExploreDetailFlickrLazyGridPhotosScreenPreview() {
    val rememberDrawable = ResourcesCompat.getDrawable(LocalContext.current.resources, R.drawable.remember, null)

    val testPhoto = LoadedPhoto(
        url = "",
        documentId = null,
        BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.plan),
    )

    val testPhoto2 = LoadedPhoto(
        url = "",
        documentId = null,
        (rememberDrawable as BitmapDrawable).bitmap,
    )

    TravelCompanionTheme {
        RememberDetailLazyGridScreen(
            modifier = Modifier,
            loadedPhotos = listOf(
                testPhoto,
                testPhoto2,
                testPhoto,
                testPhoto2,
                testPhoto,
                testPhoto2,
            ),
            planName = "Bled",
            onShowActions = { },
            onDeleted = { },
        )
    }
}
