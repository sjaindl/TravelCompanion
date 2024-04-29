package com.sjaindl.travelcompanion.explore

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sjaindl.travelcompanion.api.google.GeocodingResult
import com.sjaindl.travelcompanion.api.google.PlacePrediction
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.explore.search.PlaceActionBottomSheet
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.prefs.MapLocationDataPrefs
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.sjaindl.travelcompanion.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    encodedPlaces: String? = null,
    viewModel: ExploreViewModel = hiltViewModel(),
    onSearch: () -> Unit,
    onPickedLocation: (latitude: Float, longitude: Float) -> Unit,
    onNavigateToExploreDetails: (Long) -> Unit,
    onPlanTrip: (String) -> Unit,
    canNavigateBack: Boolean,
    showPermissionRationale: () -> Unit = { },
    navigateUp: () -> Unit = {},
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    val attributionContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.createAttributionContext("userLocation")
    } else {
        context
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(attributionContext)

    var initialLocation: MapLocationData? by remember {
        mutableStateOf(null)
    }

    var searchPlace by remember {
        mutableStateOf(false)
    }

    val showBottomSheetState by viewModel.showBottomSheet.collectAsState()
    val title by viewModel.dialogTitle.collectAsState()
    val onShowDetailsPinId by viewModel.onShowDetails.collectAsState()
    val exception by viewModel.exception.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val prefs by lazy {
        MapLocationDataPrefs(context)
    }

    val permission = Manifest.permission.ACCESS_COARSE_LOCATION

    var isLocationPermissionGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isLocationPermissionGranted = isGranted
    }

    if (searchPlace) {
        onSearch()
        searchPlace = false
    }

    if (onShowDetailsPinId > 0) {
        onNavigateToExploreDetails(onShowDetailsPinId)
        viewModel.clickedOnDetails()
    }

    if (encodedPlaces != null) {
        var description: String?
        var placeId: String?
        try {
            val placePredictions = Json.decodeFromString(PlacePrediction.serializer(), encodedPlaces)
            placeId = placePredictions.placeId
            description = placePredictions.description?.text
        } catch (exc: Exception) {
            val geocodingResult = Json.decodeFromString(GeocodingResult.serializer(), encodedPlaces)
            description = geocodingResult.formattedAddress
            placeId = geocodingResult.placeId
        }

        if (placeId != null) {
            LaunchedEffect(key1 = placeId) {
                viewModel.fetchPlaceDetails(placeId = placeId)
            }
        }

        if (description != null) {
            val message = stringResource(id = R.string.picked, description)
            LaunchedEffect(key1 = placeId) {
                snackBarHostState.showSnackbar(
                    message = message,
                )
            }
        }
    }

    if (exception != null) {
        val message = exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.unknown_error)
        LaunchedEffect(exception) {
            snackBarHostState.showSnackbar(
                message = message
            )
        }
    }

    LaunchedEffect(initialLocation) {
        initialLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude.toDouble(), it.longitude.toDouble()), it.radius)
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && initialLocation != null) {
            val location = cameraPositionState.position.target
            prefs.updateLastLocation(location.latitude.toFloat(), location.longitude.toFloat(), cameraPositionState.position.zoom)
        }
    }

    TravelCompanionTheme {
        PlaceActionBottomSheet(
            show = showBottomSheetState,
            title = title,
            onShowDetails = viewModel::onShowDetails,
            onPlanTrip = {
                viewModel.onDismiss()
                onPlanTrip(title)
            },
            onDelete = viewModel::onDelete,
            onCancel = viewModel::onDismiss,
        ) {
            Scaffold(
                modifier = Modifier
                    .background(Color.Gray),
                snackbarHost = { SnackbarHost(snackBarHostState) },
                topBar = {
                    val customActionIcon = if (!isLocationPermissionGranted) Icons.Rounded.MyLocation else null
                    TCAppBar(
                        title = stringResource(R.string.explore),
                        canNavigateBack = canNavigateBack,
                        navigateUp = navigateUp,
                        customActionIcon = customActionIcon,
                        onCustomAction = {
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                isLocationPermissionGranted = true
                            } else if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
                                showPermissionRationale()
                            } else {
                                permissionLauncher.launch(permission)
                            }
                        },
                    )
                },
                floatingActionButton = {
                    TravelCompanionTheme {
                        FloatingActionButton(
                            onClick = {
                                searchPlace = true
                            },
                            containerColor = colors.primary,
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(colors.primary)
                                    .padding(8.dp),
                            ) {
                                Text(text = stringResource(id = R.string.searchPlaces))
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = stringResource(id = R.string.search),
                                )
                            }
                        }
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
            ) { paddingValues ->
                GoogleMap(
                    modifier = Modifier.padding(paddingValues),
                    cameraPositionState = cameraPositionState,
                    googleMapOptionsFactory = {
                        GoogleMapOptions()
                    },
                    properties = MapProperties(isMyLocationEnabled = isLocationPermissionGranted),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = isLocationPermissionGranted),
                    onMapClick = { latLng ->
                        onPickedLocation(latLng.latitude.toFloat(), latLng.longitude.toFloat())
                    },
                    onMapLoaded = {
                        coroutineScope.launch {
                            initialLocation = prefs.lastLocationFlow.first()
                            viewModel.addPersistedPinsToMap()
                        }
                    },
                    onMyLocationButtonClick = {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null && location.hasAccuracy()) {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = context.getString(
                                            R.string.accuracy,
                                            location.accuracy.toString(),
                                        ),
                                    )
                                }
                            }
                        }

                        false
                    }
                ) {
                    val placeDetail by viewModel.placeDetails.collectAsState()

                    placeDetail.forEach {
                        Marker(
                            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                            tag = it.name,
                            title = it.name,
                            onClick = { marker ->
                                viewModel.clickedOnPlace(marker.title)
                                true
                            },
                        )
                    }

                    viewModel.newlyAddedLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            cameraPositionState.position.zoom
                        )

                        coroutineScope.launch {
                            prefs.updateLastLocation(
                                latitude = it.latitude.toFloat(),
                                longitude = it.longitude.toFloat(),
                                radius = cameraPositionState.position.zoom,
                            )
                        }

                        viewModel.clickedOnPlace(viewModel.newlyAddedLocation?.name)

                        viewModel.newlyAddedLocation = null
                    }
                }
            }
        }


        /*
        PlaceActionDialog(
            show = showDialogState,
            title = title,
            onShowDetails = viewModel::onShowDetails,
            onPlanTrip = viewModel::onPlanTrip,
            onDelete = viewModel::onDelete,
            onCancel = viewModel::onDismiss,
        )
        */
    }
}

@Preview
@Composable

fun ExploreScreenPreview() {
    ExploreScreen(
        onSearch = { },
        onPickedLocation = { _, _ -> },
        onNavigateToExploreDetails = { },
        onPlanTrip = { },
        canNavigateBack = true,
    )
}
