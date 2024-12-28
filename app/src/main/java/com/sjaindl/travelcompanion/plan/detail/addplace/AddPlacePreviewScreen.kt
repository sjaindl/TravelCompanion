package com.sjaindl.travelcompanion.plan.detail.addplace

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sjaindl.travelcompanion.Constants
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.Geometry
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.api.google.Photo
import com.sjaindl.travelcompanion.api.google.asPlannable
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun AddPlacePreviewScreen(
    searchedLatitude: Double,
    searchedLongitude: Double,
    googlePlace: GooglePlace,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    onCancel: () -> Unit,
    onAdd: () -> Unit,
) {
    val placeLatitude = googlePlace.geometry?.location?.lat
    val placeLongitude = googlePlace.geometry?.location?.lng

    val cameraPositionState = rememberCameraPositionState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        val latLng = if (placeLatitude != null && placeLongitude != null)
            LatLng(placeLatitude, placeLongitude)
        else
            LatLng(searchedLatitude, searchedLongitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            latLng,
            Constants.Settings.zoomLevelStandardGooglePlaceAdd,
        )
    }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = stringResource(id = R.string.place_details),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = googlePlace.name,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )

                    Text(
                        text = googlePlace.vicinity.orEmpty(),
                        color = colorScheme.primary,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )

                    googlePlace.asPlannable().imageUrl()?.let { data ->
                        val model = ImageRequest.Builder(LocalContext.current)
                            .data(data)
                            .size(Size.ORIGINAL)
                            .placeholder(R.drawable.ic_map_marker)
                            .crossfade(enable = true)
                            .build()

                        val painter = rememberAsyncImagePainter(model)

                        Image(
                            modifier = Modifier
                                .requiredHeight(200.dp)
                                .fillMaxWidth(),
                            painter = painter,
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.FillWidth,
                        )
                    }

                    GoogleMap(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .height(250.dp),
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
                    ) {
                        if (placeLatitude != null && placeLongitude != null) {
                            Marker(
                                state = MarkerState(position = LatLng(placeLatitude, placeLongitude)),
                                tag = googlePlace.name,
                                title = googlePlace.name,
                            )
                        }
                    }

                    if (placeLatitude != null && placeLongitude != null) {
                        val placeLocation = Location("place")
                        placeLocation.latitude = placeLatitude
                        placeLocation.longitude = placeLongitude

                        val searchedLocation = Location("searched")
                        searchedLocation.latitude = searchedLatitude
                        searchedLocation.longitude = searchedLongitude

                        val distanceKilometres = placeLocation.distanceTo(searchedLocation) / 1000

                        Text(
                            text = "${stringResource(id = R.string.distance)} ${
                                stringResource(
                                    id = R.string.km,
                                    distanceKilometres
                                )
                            }",
                            color = colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        )
                    }

                    val rating = googlePlace.rating
                    var ratingText = stringResource(id = R.string.noRating)
                    if (rating != null) {
                        ratingText = "${stringResource(id = R.string.rating)} $rating /5 *"
                        val numberOfRatings = googlePlace.userRatingsTotal
                        if (numberOfRatings != null) {
                            ratingText += " ($numberOfRatings ${stringResource(id = R.string.ratings)})"
                        }
                    }

                    Text(
                        text = ratingText,
                        color = colorScheme.primary,
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp
                    )

                    Row {
                        TextButton(onClick = {
                            onCancel()
                        }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary,
                                modifier = Modifier,
                                fontSize = 20.sp,
                            )
                        }

                        TextButton(onClick = {
                            onAdd()
                        }) {
                            Text(
                                text = stringResource(id = R.string.select),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary,
                                modifier = Modifier,
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddPlacePreviewScreenPreview() {
    AddPlacePreviewScreen(
        searchedLatitude = MapLocationData.default.latitude.toDouble(),
        searchedLongitude = MapLocationData.default.longitude.toDouble(),
        googlePlace = GooglePlace(
            placeId = "ChIJi6C1MxquEmsR9-c-3O48ykI",
            name = "Sidney",
            reference = "ChIJi6C1MxquEmsR9-c-3O48ykI",
            vicinity = "32 The Promenade, Sydney",
            geometry = Geometry(com.sjaindl.travelcompanion.api.google.Location(lat = -33.8675219, lng = 151.2016502)),
            photos = listOf(
                Photo(photoReference = "Aap_uEBVsYnNcrpRixtrlHBztigZh70CwYkNWZzQnqJ39SjeBo_wvgKf-kXc6tgaMLBdQrRKmxmSKjOezoZrv-sHKVbTX0OI48HBqYYVnQiZQ-WGeuQDsLEPwX7LaVPa68nUAxX114Zpqt7bryoO9wL4qXdgEnopbOp5WWLALhKEHoIEH7f7")
            ),
            scope = "GOOGLE"
        ),
        canNavigateBack = true,
        onCancel = { },
        onAdd = { },
    )
}
