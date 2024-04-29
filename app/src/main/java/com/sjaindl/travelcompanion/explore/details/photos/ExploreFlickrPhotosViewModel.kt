package com.sjaindl.travelcompanion.explore.details.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.flickr.FlickrPhoto
import com.sjaindl.travelcompanion.api.flickr.FlickrRepository
import com.sjaindl.travelcompanion.api.flickr.FlickrRepositoryImpl
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.explore.details.photos.model.PhotoType
import com.sjaindl.travelcompanion.repository.DataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@HiltViewModel(assistedFactory = ExploreFlickrPhotosViewModelFactory::class)
class ExploreFlickrPhotosViewModel @AssistedInject constructor(
    dataRepository: DataRepository,
    @Assisted pinId: Long,
    @Assisted private val photoType: PhotoType,
) : ViewModel() {

    companion object {
        const val tag = "ExploreFlickrPhotosViewModel"
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private val flickrRepository: FlickrRepository by lazy {
        FlickrRepositoryImpl(client = flickrClient)
    }

    private val flickrClient by lazy {
        TCInjector.flickrClient
    }

    val place: String?
        get() = pin?.name

    val country: String?
        get() = pin?.country

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
}

@AssistedFactory
interface ExploreFlickrPhotosViewModelFactory {
    fun create(
        pinId: Long,
        photoType: PhotoType,
    ): ExploreFlickrPhotosViewModel
}
