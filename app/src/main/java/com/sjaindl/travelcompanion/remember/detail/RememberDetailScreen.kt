package com.sjaindl.travelcompanion.remember.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.FireStoreUtils
import com.sjaindl.travelcompanion.util.LoadingAnimation
import com.sjaindl.travelcompanion.shared.R as SharedR

sealed class AddMultiplePhotosState {
    data class Error(val exception: Exception) : AddMultiplePhotosState()

    data class Info(val stringRes: Int) : AddMultiplePhotosState()

    data class AddedPhotos(val photos: List<RememberPhoto>) : AddMultiplePhotosState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberDetailScreen(
    planName: String,
    modifier: Modifier = Modifier,
    viewModel: RememberDetailViewModel = viewModel(
        factory = RememberDetailViewModelFactory(planName = planName)
    ),
    canNavigateBack: Boolean,
    showPermissionRationale: () -> Unit = { },
    navigateUp: () -> Unit = { },
) {
    val context = LocalContext.current as Activity

    var showActions by remember { mutableStateOf(true) }
    var showGrids by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    val permission = Manifest.permission.CAMERA
    val cameraAction = "android.media.action.IMAGE_CAPTURE"

    val snackBarHostState = remember { SnackbarHostState() }

    var permissionDenied by remember {
        mutableStateOf(false)
    }

    var cameraEnabled by remember {
        mutableStateOf(context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
    }

    val resultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val success = result.resultCode == Activity.RESULT_OK
        val bitmap: Bitmap? = result.data?.extras?.getParcelable("data")

        if (!success || bitmap == null) {
            viewModel.setInfo(SharedR.string.imageNotSaved)
        } else {
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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            resultLauncher.launch(Intent(cameraAction))
        } else {
            cameraEnabled = false
            permissionDenied = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPhotos()
    }

    TravelCompanionTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackBarHostState) },
            modifier = modifier,
            topBar = {
                TCAppBar(
                    title = "$planName: ${stringResource(SharedR.string.remember)}",
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
            floatingActionButton = {
                if (showActions) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (!cameraEnabled) return@FloatingActionButton

                                resultLauncher.launch(Intent(cameraAction))
                                return@FloatingActionButton

                                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                    resultLauncher.launch(Intent(cameraAction))
                                } else if(ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                                    showPermissionRationale()
                                } else {
                                    permissionLauncher.launch(permission)
                                }
                            },
                            containerColor = if (cameraEnabled) MaterialTheme.colors.secondary else Gray,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = stringResource(id = SharedR.string.choose_camera),
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                showGrids = !showGrids
                            },
                            containerColor = MaterialTheme.colors.secondary,
                        ) {
                            Icon(
                                imageVector = if (showGrids) Icons.Rounded.GridOn else Icons.Rounded.GridOff,
                                contentDescription = null,
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colors.background,
        ) { paddingValues ->

            if (permissionDenied) {
                val message = stringResource(id = SharedR.string.permission_denied)
                LaunchedEffect(Unit) {
                    snackBarHostState.showSnackbar(
                        message = message
                    )
                }
            }

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
                        exception.localizedMessage ?: exception.message ?: stringResource(id = SharedR.string.couldNotRetrieveData)

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
                        planName = planName,
                        onShowActions = {
                            showActions = it
                        },
                        onDeleted = { documentId ->
                            documentId?.let {
                                viewModel.removePhoto(it)
                            }
                        },
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
