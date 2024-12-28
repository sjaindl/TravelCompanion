package com.sjaindl.travelcompanion.plan.detail.addplace

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.google.GooglePlace
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.baseui.TCAppBar
import com.sjaindl.travelcompanion.databinding.FragmentSearchPlaceBinding
import com.sjaindl.travelcompanion.model.MapLocationData
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.CustomDividerItemDecoration

@Composable
fun SearchPlacesScreen(
    modifier: Modifier = Modifier,
    placeType: GooglePlaceType,
    latitude: Double,
    longitude: Double,
    radius: Double,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    onPickedPlace: (GooglePlace) -> Unit,
    viewModel: SearchPlacesViewModel = viewModel(),
) {
    val context = LocalContext.current

    val holders = viewModel.viewHolders.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.search(
            text = "",
            latitude = latitude,
            longitude = longitude,
            placeType = placeType,
            radius = radius,
        )
    }

    TravelCompanionTheme {
        Scaffold(
            topBar = {
                TCAppBar(
                    title = stringResource(id = R.string.searchPlaces),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateUp,
                )
            },
        ) { paddingValues ->
            AndroidViewBinding(
                modifier = modifier.padding(paddingValues),
                factory = FragmentSearchPlaceBinding::inflate,
            ) {
                val searchPlaceAdapter by lazy {
                    SearchPlacesAdapter { item ->
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        inputMethodManager?.hideSoftInputFromWindow(autocompleteCountry.applicationWindowToken, 0)

                        onPickedPlace(item.places)
                    }
                }

                searchPlaceAdapter.submitList(holders.value)

                // setup autocompletion
                val adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    emptyArray()
                )
                autocompleteCountry.setAdapter(adapter)
                autocompleteCountry.doOnTextChanged { text, _, _, _ ->
                    viewModel.search(
                        text = text.toString(),
                        latitude = latitude,
                        longitude = longitude,
                        placeType = placeType,
                        radius = radius,
                    )
                }

                // setupRecyclerView
                placeSuggestions.layoutManager = LinearLayoutManager(context)
                placeSuggestions.addItemDecoration(
                    CustomDividerItemDecoration(
                        context,
                        android.R.drawable.divider_horizontal_bright,
                    )
                )
                placeSuggestions.adapter = searchPlaceAdapter
            }
        }
    }
}

@Preview
@Composable
fun SearchPlacesScreenPreview() {
    SearchPlacesScreen(
        modifier = Modifier,
        placeType = GooglePlaceType.PointOfInterest,
        latitude = MapLocationData.default.latitude.toDouble(),
        longitude = MapLocationData.default.longitude.toDouble(),
        radius = MapLocationData.default.radius.toDouble(),
        canNavigateBack = true,
        onPickedPlace = { },
    )
}
