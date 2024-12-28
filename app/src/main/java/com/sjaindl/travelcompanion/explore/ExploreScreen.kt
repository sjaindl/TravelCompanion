package com.sjaindl.travelcompanion.explore

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.util.trace
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.common.collect.ImmutableList
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GeocodingResult
import com.sjaindl.travelcompanion.api.google.PlacePrediction
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.explore.search.PlaceActionBottomSheet
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.prefs.MapLocationDataPreferences
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.theapache64.rebugger.Rebugger
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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
    navigateUp: () -> Unit,
) {
    val showBottomSheetState by viewModel.showBottomSheet.collectAsState()
    val title by viewModel.dialogTitle.collectAsState()
    val showDetails by viewModel.showDetails.collectAsState()
    val exception by viewModel.exception.collectAsState()
    val placeDetails by viewModel.placeDetails.collectAsState()

    ExploreScreenContent(
        showBottomSheet = showBottomSheetState,
        title = title,
        //exception = exception, // unstable!
        errorMessage = if (exception != null) {
            exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.unknown_error)
        } else null,
        showDetails = showDetails,
        canNavigateBack = canNavigateBack,
        encodedPlaces = encodedPlaces,
        newlyAddedLocation = viewModel.newlyAddedLocation,
        placeDetails = placeDetails, // Stable: kotlinx.ImmutableList
        onSearch = onSearch,
        onPickedLocation = onPickedLocation,
        onNavigateToExploreDetails = onNavigateToExploreDetails,
        onPlanTrip = onPlanTrip,
        showPermissionRationale = remember { showPermissionRationale },
        clickedOnDetails = viewModel::clickedOnDetails,
        fetchPlaceDetails = viewModel::fetchPlaceDetails,
        onDelete = viewModel::onDelete,
        onDismiss = viewModel::onDismiss,
        addPersistedPinsToMap = viewModel::addPersistedPinsToMap,
        onClickedPlace = viewModel::clickedOnPlace,
        onShowDetails = viewModel::onShowDetails,
        setNewlyAddedLocation = {
            viewModel.newlyAddedLocation = it
        },
        navigateUp = navigateUp,
    )
}

@Composable
fun ExploreScreenContent(
    showBottomSheet: Boolean,
    title: String,
    //exception: Throwable? // unstable
    errorMessage: String?, // stable
    showDetails: Long,
    canNavigateBack: Boolean,
    encodedPlaces: String? = null,
    newlyAddedLocation: PlaceDetail? = null,
    placeDetails: ImmutableList<PlaceDetail> = ImmutableList.of(), // stable
    //placeDetails: List<PlaceDetail> = emptyList(), // unstable
    onSearch: () -> Unit,
    onPickedLocation: (latitude: Float, longitude: Float) -> Unit,
    onNavigateToExploreDetails: (Long) -> Unit,
    onPlanTrip: (String) -> Unit,
    showPermissionRationale: () -> Unit,
    clickedOnDetails: () -> Unit,
    fetchPlaceDetails: (String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    onShowDetails: () -> Unit,
    addPersistedPinsToMap: () -> Unit,
    onClickedPlace: (String?) -> Unit,
    setNewlyAddedLocation: (PlaceDetail?) -> Unit,
    navigateUp: () -> Unit,
) = trace(sectionName = "ExploreScreenContent") {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState()

    val attributionContext = context.createAttributionContext("userLocation")

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(attributionContext)
    }

    var initialLocation: MapLocationData? by remember {
        mutableStateOf(null)
    }

    var searchPlace by remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    val preferences by lazy {
        MapLocationDataPreferences(context = context)
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

    Rebugger(
        trackMap = mapOf(
            "showBottomSheet" to showBottomSheet,
            "title" to title,
            "errorMessage" to errorMessage,
            "showDetails" to showDetails,
            "canNavigateBack" to canNavigateBack,
            "encodedPlaces" to encodedPlaces,
            "newlyAddedLocation" to newlyAddedLocation,
            "placeDetails" to placeDetails,
            "errorMessage" to errorMessage,
            "initialLocation" to initialLocation,
            "searchPlace" to searchPlace,
            "isLocationPermissionGranted" to isLocationPermissionGranted,
            "snackBarHostState" to snackBarHostState,

            "cameraPositionState" to cameraPositionState,
            "coroutineScope" to coroutineScope,
            "permissionLauncher" to permissionLauncher,

            "onSearch" to onSearch,
            "onPickedLocation" to onPickedLocation,
            "onNavigateToExploreDetails" to onNavigateToExploreDetails,
            "onPlanTrip" to onPlanTrip,
            "showPermissionRationale" to showPermissionRationale,
            "clickedOnDetails" to clickedOnDetails,
            "fetchPlaceDetails" to fetchPlaceDetails,
            "onDelete" to onDelete,
            "onDismiss" to onDismiss,
            "onShowDetails" to onShowDetails,
            "addPersistedPinsToMap" to addPersistedPinsToMap,
            "onClickedPlace" to onClickedPlace,
            "setNewlyAddedLocation" to setNewlyAddedLocation,
            "navigateUp" to navigateUp,
        ),
    )

    if (searchPlace) {
        onSearch()
        searchPlace = false
    }

    if (showDetails > 0) {
        onNavigateToExploreDetails(showDetails)
        clickedOnDetails()
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
                fetchPlaceDetails(placeId)
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

    /*
        if (exception != null) {
            val message = exception.localizedMessage ?: exception.message ?: stringResource(id = R.string.unknown_error)
            LaunchedEffect(exception) {
                snackBarHostState.showSnackbar(
                    message = message
                )
            }
        }
     */
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            snackBarHostState.showSnackbar(
                message = errorMessage
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
            preferences.updateLastLocation(location.latitude.toFloat(), location.longitude.toFloat(), cameraPositionState.position.zoom)
        }
    }

    TravelCompanionTheme {
        if (showBottomSheet) {
            PlaceActionBottomSheet(
                title = title,
                onShowDetails = onShowDetails,
                onPlanTrip = {
                    onDismiss()
                    onPlanTrip(title)
                },
                onDelete = onDelete,
                onCancel = onDismiss,
            )
        }

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
                    if (initialLocation != null) {
                        FloatingActionButton(
                            onClick = {
                                searchPlace = true
                            },
                            containerColor = colorScheme.primary,
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(colorScheme.primary)
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
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { paddingValues ->
            trace(sectionName = "GoogleMap") {
                GoogleMap(
                    modifier = Modifier
                        .padding(paddingValues),
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
                            trace(sectionName = "onMapLoaded") {
                                addPersistedPinsToMap()
                                initialLocation = preferences.lastLocationFlow.firstOrNull() ?: MapLocationData.default
                            }
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
                    placeDetails.forEach {
                        Marker(
                            state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                            tag = it.name,
                            title = it.name,
                            onClick = { marker ->
                                onClickedPlace(marker.title)
                                true
                            },
                        )
                    }

                    newlyAddedLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            cameraPositionState.position.zoom
                        )

                        coroutineScope.launch {
                            preferences.updateLastLocation(
                                latitude = it.latitude.toFloat(),
                                longitude = it.longitude.toFloat(),
                                radius = cameraPositionState.position.zoom,
                            )
                        }

                        onClickedPlace(newlyAddedLocation.name)

                        setNewlyAddedLocation(null)
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
        navigateUp = { },
    )
}
