package com.sjaindl.travelcompanion.remember.detail

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.GridOff
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.FireStoreUtils
import com.sjaindl.travelcompanion.util.LoadingAnimation
import com.sjaindl.travelcompanion.util.TCFileProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberDetailScreen(
    planName: String,
    modifier: Modifier = Modifier,
    viewModel: RememberDetailViewModel = viewModel(
        factory = RememberDetailViewModelFactory(planName = planName)
    ),
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = { },
) {
    val context = LocalContext.current

    var showGrids by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    var addedPhotos: List<Bitmap> by remember { mutableStateOf(listOf()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            uris.map { uri ->
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            }.forEach { bitmap ->
                addedPhotos += bitmap

                FireStoreUtils.persistRememberPhoto(
                    planName = planName,
                    image = bitmap,
                    onSuccess = {
                        viewModel.addPhoto(photo = it)
                    },
                    onInfo = {
                        viewModel.setInfo(stringRes = it)
                    },
                    onError = {
                        viewModel.setError(exception = it)
                    },
                )
            }
        }
    )

    val cameraPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            val uri = imageUri
            if (!success || uri == null) {
                viewModel.setInfo(R.string.imageNotSaved)
            } else {
                val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                addedPhotos += bitmap

                FireStoreUtils.persistRememberPhoto(
                    planName = planName,
                    image = bitmap,
                    onSuccess = {
                        viewModel.addPhoto(photo = it)
                    },
                    onInfo = {
                        viewModel.setInfo(stringRes = it)
                    },
                    onError = {
                        viewModel.setError(exception = it)
                    },
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    TravelCompanionTheme {
        Scaffold(
            modifier = modifier,
            topBar = {
                TCAppBar(
                    title = "$planName: ${stringResource(R.string.remember)}",
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FloatingActionButton(onClick = {
                        // https://proandroiddev.com/jetpack-compose-new-photo-picker-b280950ba934
                        multiplePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Photo,
                            contentDescription = stringResource(id = R.string.choose_gallery),
                        )
                    }
                    FloatingActionButton(onClick = {
                        val uri = TCFileProvider.getImageUri(context)
                        imageUri = uri
                        cameraPicker.launch(uri)
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.CameraAlt,
                            contentDescription = stringResource(id = R.string.choose_camera),
                        )
                    }
                    FloatingActionButton(onClick = {
                        showGrids = !showGrids
                    }) {
                        Icon(
                            imageVector = if (showGrids) Icons.Rounded.GridOn else Icons.Rounded.GridOff,
                            contentDescription = null,
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colors.background,
        ) { paddingValues ->
            when (state) {
                RememberDetailViewModel.State.Loading -> {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colors.background)
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        LoadingAnimation()
                    }
                }

                is RememberDetailViewModel.State.Error -> {
                    val exception = (state as RememberDetailViewModel.State.Error).exception

                    val errorMessage =
                        exception.localizedMessage ?: exception.message ?: stringResource(id = R.string.couldNotRetrieveData)

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colors.background)
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

                is RememberDetailViewModel.State.Info -> {
                    val info = (state as RememberDetailViewModel.State.Info)

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colors.background)
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

                is RememberDetailViewModel.State.LoadedPhotos -> {
                    val loaded = state as RememberDetailViewModel.State.LoadedPhotos

                    RememberDetailPhotosScreen(
                        modifier = Modifier.padding(paddingValues = paddingValues),
                        showGrids = showGrids,
                        photos = loaded.photos,
                        addedPhotos = addedPhotos,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RememberDetailScreenPreview() {
    RememberDetailScreen(
        planName = "Bled",
        canNavigateBack = false,
    )
}
