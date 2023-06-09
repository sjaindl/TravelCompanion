package com.sjaindl.travelcompanion.plan.detail.addplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.api.google.GooglePlaceType
import com.sjaindl.travelcompanion.di.TCInjector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchPlacesViewModel : ViewModel() {
    private val googleClient by lazy {
        TCInjector.googleClient
    }

    private val _viewHolders: MutableStateFlow<List<SearchPlacesViewHolderType.GooglePlaceItem>> = MutableStateFlow(emptyList())
    val viewHolders: StateFlow<List<SearchPlacesViewHolderType.GooglePlaceItem>> = _viewHolders

    fun search(
        text: String,
        latitude: Double,
        longitude: Double,
        placeType: GooglePlaceType,
        radius: Double,
    ) = viewModelScope.launch(Dispatchers.IO) {
        googleClient.searchPlaces(
            text = text,
            latitude = latitude,
            longitude = longitude,
            type = placeType.key,
            radius = radius.toString(),
        ).onSuccess { nearbyPlaces ->
            val places = nearbyPlaces.results

            withContext(Dispatchers.Main) {
                println(places)

                val viewHolders = places.map {
                    SearchPlacesViewHolderType.GooglePlaceItem(it)
                }

                _viewHolders.value = viewHolders
            }
        }.onFailure {
            Timber.e(it)
            _viewHolders.value = emptyList()
        }
    }
}
