package com.sjaindl.travelcompanion.explore.search

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjaindl.travelcompanion.databinding.FragmentSearchPlaceBinding
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.CustomDividerItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@Composable
fun PickPlaceScreen(
    modifier: Modifier = Modifier,
    latitude: Float,
    longitude: Float,
    onPickedPlace: (String) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val googleClient by lazy {
        TCInjector.googleClient
    }
    val searchPlaceAdapter by lazy {
        SearchPlaceAdapter { item ->
            focusManager.clearFocus(force = true)

            (item as? SearchPlaceViewHolderType.PlaceItem)?.let {
                val encodedResult = Json.encodeToString(item.geocoded)
                onPickedPlace(encodedResult)
            }
        }
    }

    TravelCompanionTheme {
        AndroidViewBinding(
            modifier = modifier,
            factory = FragmentSearchPlaceBinding::inflate,
        ) {
            this.autocompleteCountry.visibility = View.GONE

            scope.launch {
                googleClient.reverseGeocode(
                    latitude = latitude,
                    longitude = longitude,
                ).onSuccess { geocodingResponse ->
                    scope.launch(Dispatchers.Main) {
                        println(geocodingResponse)

                        val viewHolders = geocodingResponse.results.map {
                            SearchPlaceViewHolderType.PlaceItem(it)
                        }
                        searchPlaceAdapter.submitList(viewHolders)
                    }

                }.onFailure {
                    Timber.e(it)
                    searchPlaceAdapter.submitList(emptyList())
                }
            }

            // setupRecyclerView
            this.placeSuggestions.layoutManager = LinearLayoutManager(context)
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

@Preview
@Composable
fun PickPlaceScreenPreview() {
    PickPlaceScreen(
        modifier = Modifier,
        latitude = 0.0f,
        longitude = 0.0f,
        onPickedPlace = { },
    )
}
