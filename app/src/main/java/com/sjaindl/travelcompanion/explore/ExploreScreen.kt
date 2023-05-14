package com.sjaindl.travelcompanion.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.PlacesPredictions
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.explore.search.PlaceActionBottomSheet
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.prefs.MapLocationDataPrefs
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun ExploreScreen(
    encodedPlaces: String? = null,
    viewModel: ExploreViewModel = viewModel(
        factory = ExploreViewModelFactory(
            dataRepository = AndroidPersistenceInjector(LocalContext.current).shared.dataRepository,
        )
    ),
    onSearch: () -> Unit,
    onNavigateToExploreDetails: (Long) -> Unit,
) {
    val context = LocalContext.current

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val cameraPositionState = rememberCameraPositionState()

    var initialLocation: MapLocationData? by remember {
        mutableStateOf(null)
    }

    var searchPlace by remember {
        mutableStateOf(false)
    }

    val showDialogState by viewModel.showDialog.collectAsState()
    val title by viewModel.dialogTitle.collectAsState()
    val onShowDetailsPinId by viewModel.onShowDetails.collectAsState()
    val exception by viewModel.exception.collectAsState()

    val prefs by lazy {
        MapLocationDataPrefs(context)
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
        val placesPredictions = Json.decodeFromString(PlacesPredictions.serializer(), encodedPlaces)
        val placeId = placesPredictions.placeId

        if (placeId != null) {
            viewModel.fetchPlaceDetails(placeId = placeId)
        }

        val message = stringResource(id = R.string.picked, placesPredictions.description)
        LaunchedEffect(key1 = placeId) {
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
            )
        }
    }

    if (exception != null) {
        val message = exception?.localizedMessage ?: exception?.message ?: stringResource(id = R.string.unknown_error)
        LaunchedEffect(exception) {
            scaffoldState.snackbarHostState.showSnackbar(
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
        Scaffold(
            modifier = Modifier
                .background(Color.Gray),
            scaffoldState = scaffoldState,
            topBar = {
                TCAppBar(
                    title = stringResource(R.string.explore),
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
                properties = MapProperties(
                    isBuildingEnabled = true,
                    isIndoorEnabled = true,
                    isMyLocationEnabled = false,
                    isTrafficEnabled = false,
                ),
                uiSettings = MapUiSettings(),
                onMapLoaded = {
                    coroutineScope.launch {
                        initialLocation = prefs.lastLocationFlow.first()
                    }
                    viewModel.addPersistedPinsToMap()
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
                    viewModel.newlyAddedLocation = null
                }
            }
        }

        PlaceActionBottomSheet(
            show = showDialogState,
            title = title,
            onShowDetails = viewModel::onShowDetails,
            onPlanTrip = viewModel::onPlanTrip,
            onDelete = viewModel::onDelete,
            onCancel = viewModel::onDismiss,
        )

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
        onNavigateToExploreDetails = { },
    )
}
