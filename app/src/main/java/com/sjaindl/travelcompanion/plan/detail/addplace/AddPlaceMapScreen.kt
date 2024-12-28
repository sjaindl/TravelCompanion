package com.sjaindl.travelcompanion.plan.detail.addplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.api.google.description
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.plan.add.PlaceTypePicker
import com.sjaindl.travelcompanion.plan.detail.PlanDetailItemType
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.LoadingAnimation

@Composable
fun AddPlaceMapScreen(
    modifier: Modifier,
    planDetailItemType: PlanDetailItemType,
    initialLocation: MapLocationData,
    planName: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    viewModel: AddPlaceViewModel = hiltViewModel(
        creationCallback = { factory: AddPlaceViewModel.AddPlaceViewModelFactory ->
            factory.create(planName = planName)
        }
    ),
) {
    val cameraPositionState = rememberCameraPositionState()

    var showPlaceTypePicker by remember {
        mutableStateOf(false)
    }

    var placeType: GooglePlaceType? by remember {
        mutableStateOf(null)
    }

    var selectedPlaceCoordinatesOnMap: LatLng? by remember {
        mutableStateOf(null)
    }

    var selectedGooglePlace: GooglePlace? by remember {
        mutableStateOf(null)
    }

    val searchDistance = remember {
        mutableDoubleStateOf((viewModel.maxDistanceKm - viewModel.minDistanceKm + 1) / 2f)
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialLocation) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(initialLocation.latitude.toDouble(), initialLocation.longitude.toDouble()),
            initialLocation.radius,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.fetchPlan()
    }

    TravelCompanionTheme {
        val placeCoordinates = selectedPlaceCoordinatesOnMap
        val place = selectedGooglePlace

        PlaceTypePicker(
            modifier = Modifier,
            show = showPlaceTypePicker,
            title = stringResource(id = R.string.choosePlaceType),
            onPickedPlace = { type ->
                placeType = type
                showPlaceTypePicker = false
            },
            onCancel = {
                showPlaceTypePicker = false
            },
        )

        if (placeCoordinates != null) {
            if (place != null) {
                AddPlacePreviewScreen(
                    searchedLatitude = placeCoordinates.latitude,
                    searchedLongitude = placeCoordinates.longitude,
                    googlePlace = place,
                    canNavigateBack = true,
                    navigateUp = {
                        selectedGooglePlace = null
                    },
                    onCancel = {
                        selectedGooglePlace = null
                    },
                    onAdd = {
                        viewModel.persistPlace(place, placeType)
                        selectedPlaceCoordinatesOnMap = null
                        selectedGooglePlace = null
                    },
                )
            } else {
                placeType?.let { placeType ->
                    SearchPlacesScreen(
                        modifier = Modifier,
                        placeType = placeType,
                        latitude = placeCoordinates.latitude,
                        longitude = placeCoordinates.longitude,
                        radius = searchDistance.doubleValue * 1000,
                        canNavigateBack = true,
                        navigateUp = {
                            selectedPlaceCoordinatesOnMap = null
                            selectedGooglePlace = null
                        },
                        onPickedPlace = {
                            selectedGooglePlace = it
                        }
                    )
                }
            }
        } else {
            when (planDetailItemType) {
                PlanDetailItemType.HOTEL -> placeType = GooglePlaceType.Lodging
                PlanDetailItemType.RESTAURANT -> placeType = GooglePlaceType.Restaurant
                else -> if (placeType == null) showPlaceTypePicker = true
            }

            placeType?.let {
                Scaffold(
                    topBar = {
                        TCAppBar(
                            title = stringResource(id = R.string.addPlace, stringResource(id = it.description.resourceId)),
                            canNavigateBack = canNavigateBack,
                            navigateUp = navigateUp,
                        )
                    },
                ) { paddingValues ->

                    when (val addPlaceState = state) {
                        AddPlaceViewModel.State.Initial -> {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxSize()
                                    .background(colorScheme.background)
                                    .padding(all = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                LoadingAnimation()
                            }
                        }

                        is AddPlaceViewModel.State.Error -> {
                            val throwable = addPlaceState.exception

                            val errorMessage = (throwable?.localizedMessage ?: throwable.toString())

                            Text(
                                text = errorMessage,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }

                        is AddPlaceViewModel.State.PlanReady, AddPlaceViewModel.State.Finished -> {
                            Column(
                                modifier = Modifier
                                    .padding(paddingValues = paddingValues)
                            ) {
                                GoogleMap(
                                    modifier = modifier
                                        .padding(bottom = 16.dp)
                                        .weight(8f),
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
                                    onMapClick = {
                                        selectedPlaceCoordinatesOnMap = it
                                    }
                                )

                                Slider(
                                    modifier = Modifier
                                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                                        .weight(1f),
                                    value = searchDistance.doubleValue.toFloat(),
                                    onValueChange = { sliderValue ->
                                        searchDistance.doubleValue = sliderValue.toDouble()
                                    },
                                    valueRange = 1f..50f,
                                    steps = viewModel.steps,
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(bottom = 16.dp, start = 24.dp, end = 16.dp)
                                        .weight(1f),
                                    text = "${stringResource(id = R.string.searchRadius)} ${
                                        stringResource(
                                            R.string.km,
                                            searchDistance.doubleValue
                                        )
                                    }",
                                    color = colorScheme.primary,
                                )
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
fun AddPlaceMapScreenPreview() {
    AddPlaceMapScreen(
        modifier = Modifier,
        planDetailItemType = PlanDetailItemType.ATTRACTION,
        initialLocation = MapLocationData.default,
        planName = "Bled",
        canNavigateBack = true,
    )
}
