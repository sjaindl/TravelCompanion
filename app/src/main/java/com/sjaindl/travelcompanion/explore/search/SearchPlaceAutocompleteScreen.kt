package com.sjaindl.travelcompanion.explore.search

import android.widget.ArrayAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.sjaindl.travelcompanion.databinding.FragmentSearchPlaceBinding
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme
import com.sjaindl.travelcompanion.util.CustomDividerItemDecoration
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

@Composable
fun SearchPlaceAutocompleteScreen(
    modifier: Modifier = Modifier,
    onPickedPlace: (String) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val scope = rememberCoroutineScope()
    val googleClient by lazy {
        TCInjector.googleClient
    }
    val sessionToken = randomStringByKotlinRandom(32)
    val searchPlaceAdapter by lazy {
        SearchPlaceAdapter { item ->
            focusManager.clearFocus(force = true)
            val encodedResult = Json.encodeToString(item.placesPredictions)
            onPickedPlace(encodedResult)
        }
    }

    TravelCompanionTheme {
        AndroidViewBinding(
            modifier = modifier,
            factory = FragmentSearchPlaceBinding::inflate,
        ) {
            // setup autocompletion
            val adapter = ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                emptyArray()
            )
            this.autocompleteCountry.setAdapter(adapter)
            this.autocompleteCountry.doOnTextChanged { text, _, _, _ ->
                scope.launch {
                    googleClient.autocomplete(
                        text.toString(), sessionToken
                    ).onSuccess { autocompleteResult ->
                        val suggestions = autocompleteResult?.predictions ?: emptyList()

                        scope.launch(Dispatchers.Main) {
                            println(suggestions)

                            val viewHolders = suggestions.map {
                                SearchPlaceViewHolderType.PlacesPredictionItem(it)
                            }
                            searchPlaceAdapter.submitList(viewHolders)
                        }
                    }.onFailure {
                        Timber.e(it)
                        searchPlaceAdapter.submitList(emptyList())
                    }
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
fun SearchPlaceScreenPreview() {
    SearchPlaceAutocompleteScreen(
        modifier = Modifier,
        onPickedPlace = { },
    )
}
