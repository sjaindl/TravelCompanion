package com.sjaindl.travelcompanion.explore.details.photos

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.google.GooglePlacesClient
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExplorePlacesPhotosViewModel(
    dataRepository: DataRepository,
    private val client: GooglePlacesClient,
    pinId: Long,
) :
    ViewModel() {

    sealed class State {
        object Loading : State()
        data class Done(val photos: MutableList<Pair<Bitmap, String>>) : State()
        data class Error(val throwable: Throwable) : State()
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    init {
        viewModelScope.launch {
            val placeId = pin?.placeId ?: return@launch
            fetchPlacePhotos(placeId = placeId)
        }
    }

    private suspend fun fetchPlacePhotos(placeId: String) {
        val metaData = client.fetchPhotoMetaData(placeId = placeId)

        val bitmapWithAttributions: MutableList<Pair<Bitmap, String>> = mutableListOf()

        metaData.forEach { photoMetadata ->
            fetchPhoto(metadata = photoMetadata)?.let {
                bitmapWithAttributions.add(it)
            }
        }

        _state.value = State.Done(photos = bitmapWithAttributions)
    }

    private suspend fun fetchPhoto(metadata: PhotoMetadata): Pair<Bitmap, String>? {
        return client.fetchPhotoWithAttributions(metadata = metadata)
    }
}

class ExplorePlacesPhotosViewModelFactory(
    private val dataRepository: DataRepository,
    private val client: GooglePlacesClient,
    private val pinId: Long,
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ExplorePlacesPhotosViewModel(dataRepository, client, pinId) as T
}
