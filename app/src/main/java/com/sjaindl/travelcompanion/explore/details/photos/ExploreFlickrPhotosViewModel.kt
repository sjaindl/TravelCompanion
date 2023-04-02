package com.sjaindl.travelcompanion.explore.details.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.flickr.FlickrClient
import com.sjaindl.travelcompanion.api.flickr.FlickrPhotoResponse
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreFlickrPhotosViewModel(
    dataRepository: DataRepository,
    pinId: Long,
    private val photoType: PhotoType,
) :
    ViewModel() {

    sealed class State {
        object Loading : State()
        data class Done(val response: FlickrPhotoResponse) : State()
        data class Error(val throwable: Throwable) : State()
    }

    // TODO: Hilt
    private var pin: Pin? = dataRepository.singlePin(pinId)
    private var flickrClient = FlickrClient()

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    val country: String?
        get() = pin?.country

    init {
        viewModelScope.launch {
            when (photoType) {
                PhotoType.COUNTRY -> {
                    val country = pin?.country ?: return@launch

                    flickrClient.fetchPhotos(text = country)
                        .onSuccess {
                            _state.value = State.Done(response = it)
                        }
                        .onFailure {
                            _state.value = State.Error(it)
                        }
                }
                PhotoType.LOCATION -> {
                    val latitude = pin?.latitude ?: return@launch
                    val longitude = pin?.longitude ?: return@launch

                    flickrClient.fetchPhotos(latitude = latitude, longitude = longitude)
                        .onSuccess {
                            _state.value = State.Done(response = it)
                        }
                        .onFailure {
                            _state.value = State.Error(it)
                        }
                }
            }
        }
    }
}

class ExploreFlickrPhotosViewModelFactory(
    private val pinId: Long,
    private val photoType: PhotoType,
    private val dataRepository: DataRepository,
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ExploreFlickrPhotosViewModel(dataRepository = dataRepository, pinId = pinId, photoType = photoType) as T
}
