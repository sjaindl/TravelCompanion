package com.sjaindl.travelcompanion.explore.details.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.flickr.FlickrConstants
import com.sjaindl.travelcompanion.api.flickr.FlickrPhoto
import com.sjaindl.travelcompanion.api.flickr.FlickrPhotoResponse
import com.sjaindl.travelcompanion.api.flickr.FlickrRepository
import com.sjaindl.travelcompanion.api.flickr.FlickrRepositoryImpl
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class ExploreFlickrPhotosViewModel(
    dataRepository: DataRepository,
    pinId: Long,
    private val photoType: PhotoType,
) :
    ViewModel() {

    companion object {
        val tag = "ExploreFlickrPhotosViewModel"
    }

    sealed class State {
        object Initial : State()
        object Loading : State()
        data class Done(val response: FlickrPhotoResponse) : State()
        data class Error(val throwable: Throwable) : State()
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private val flickrRepository: FlickrRepository by lazy {
        FlickrRepositoryImpl(client = flickrClient)
    }

    private val flickrClient by lazy {
        TCInjector.flickrClient
    }

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Initial)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    val country: String?
        get() = pin?.country

    var pageOffset = AtomicInteger(0)
        private set

    private var _photosCount: MutableStateFlow<Int> = MutableStateFlow(0)
    var photosCount = _photosCount.asStateFlow()

    suspend fun fetchNextPhotos() {
        if (shouldFetchMore()) {
            fetchPhotos(offset = pageOffset.addAndGet(1))
        }
    }

    private suspend fun fetchPhotos(offset: Int = 0, limit: Int = FlickrConstants.ParameterValues.limit) {
        if (state.value == State.Initial) {
            _state.value = State.Loading
        }

        Timber.tag(tag).d("Fetch photos at offset: $offset")
        when (photoType) {
            PhotoType.COUNTRY -> {
                val country = pin?.country ?: return

                flickrClient.fetchPhotos(text = country, offset = offset, limit = limit)
                    .onSuccess { response ->
                        val allPhotos: MutableList<FlickrPhoto> =
                            (state.value as? State.Done)?.response?.metaData?.photos?.toMutableList() ?: mutableListOf()

                        _state.value = State.Done(
                            response = response.apply {
                                allPhotos.addAll(this.metaData.photos)
                                Timber.tag(tag).d("all photos: ${allPhotos.size}")
                                metaData.photos = allPhotos
                            }
                        )

                        _photosCount.value = allPhotos.size
                    }
                    .onFailure {
                        _state.value = State.Error(it)
                    }
            }

            PhotoType.LOCATION -> {
                val latitude = pin?.latitude ?: return
                val longitude = pin?.longitude ?: return

                flickrClient.fetchPhotos(latitude = latitude, longitude = longitude, offset = offset, limit = limit)
                    .onSuccess { response ->
                        val allPhotos: MutableList<FlickrPhoto> =
                            (state.value as? State.Done)?.response?.metaData?.photos?.toMutableList() ?: mutableListOf()

                        _state.value = State.Done(
                            response = response.apply {
                                allPhotos.addAll(this.metaData.photos)
                                Timber.tag(tag).d("all photos: ${allPhotos.size}")
                                this.metaData.photos = allPhotos
                            }
                        )

                        _photosCount.value = allPhotos.size
                    }
                    .onFailure {
                        _state.value = State.Error(it)
                    }
            }
        }
    }

    fun fetchPhotosFlow(): Flow<PagingData<FlickrPhoto>> {
        when (photoType) {
            PhotoType.COUNTRY -> {
                val country = pin?.country ?: return emptyFlow()

                return flickrRepository.fetchPhotos(text = country).cachedIn(viewModelScope)
            }

            PhotoType.LOCATION -> {
                val latitude = pin?.latitude ?: return emptyFlow()
                val longitude = pin?.longitude ?: return emptyFlow()

                return flickrRepository.fetchPhotos(latitude = latitude, longitude = longitude).cachedIn(viewModelScope)
            }
        }
    }

    private fun shouldFetchMore(): Boolean {
        if (state.value == State.Loading) return false
        val metadata = (state.value as? State.Done)?.response?.metaData ?: return true

        return metadata.page < metadata.pages
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
