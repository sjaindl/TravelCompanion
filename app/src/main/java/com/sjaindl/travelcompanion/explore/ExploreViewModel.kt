package com.sjaindl.travelcompanion.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.common.collect.ImmutableList
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

data class PlaceDetail(val latitude: Double, val longitude: Double, val name: String)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val dataRepository: DataRepository,
) : ViewModel() {
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    var dialogTitle = _dialogTitle.asStateFlow()

    private val _showDetails = MutableStateFlow(0L)
    var showDetails = _showDetails.asStateFlow()

    private val _exception: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    var exception = _exception.asStateFlow()

    private val _placeDetails = MutableStateFlow<ImmutableList<PlaceDetail>>(ImmutableList.of())
    val placeDetails = _placeDetails.asStateFlow()

    private val googleClient by lazy {
        TCInjector.googleClient
    }

    private val geoNamesClient by lazy {
        TCInjector.geoNamesClient
    }

    private val sessionToken = randomStringByKotlinRandom(32)

    var newlyAddedLocation: PlaceDetail? = null

    fun onShowDetails() {
        _showBottomSheet.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return

        _showDetails.value = pin.id
    }

    fun onDelete() {
        _showBottomSheet.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return
        dataRepository.deletePin(pin.id)

        addPersistedPinsToMap()
    }

    fun onDismiss() {
        _showBottomSheet.value = false
    }

    fun clickedOnPlace(name: String?) {
        if (name == null) return

        _dialogTitle.value = name
        _showBottomSheet.value = true
    }

    fun clickedOnDetails() {
        _showDetails.value = 0L
    }

    fun fetchPlaceDetails(placeId: String) {
        viewModelScope.launch {
            googleClient.placeDetail(placeId = placeId, token = sessionToken)
                .onSuccess { details ->
                    val location = details.location
                    val name = details.displayName?.text.orEmpty()
                    val placeDetail = PlaceDetail(latitude = location.latitude, longitude = location.longitude, name = name)
                    newlyAddedLocation = placeDetail

                    val list = placeDetails.value.toMutableList().apply {
                        add(placeDetail)
                    }
                    _placeDetails.value = ImmutableList.copyOf(list)

                    try {
                        val countryCode = geoNamesClient.fetchCountryCode(latitude = location.latitude, longitude = location.longitude)

                        val component = details.addressComponents?.firstOrNull {
                            it.types.contains("country")
                        }

                        dataRepository.insertPin(
                            id = 0,
                            address = details.formattedAddress,
                            country = component?.longName,
                            countryCode = countryCode,
                            creationDate = Clock.System.now(),
                            latitude = details.location.latitude,
                            longitude = details.location.longitude,
                            name = name,
                            phoneNumber = null,
                            placeId = placeId,
                            rating = null,
                            url = details.websiteUri,
                        )
                    } catch (exception: Exception) {
                        _exception.value = exception
                    }
                }.onFailure {
                    _exception.value = it
                }
        }
    }

    fun addPersistedPinsToMap() {
        val list: MutableList<PlaceDetail> = mutableListOf()

        dataRepository.allPins().forEach { pin ->
            val lat = pin.latitude ?: return@forEach
            val lng = pin.longitude ?: return@forEach
            val name = pin.name ?: return@forEach
            val detail = PlaceDetail(latitude = lat, longitude = lng, name = name)
            list.add(detail)
        }

        _placeDetails.value = ImmutableList.copyOf(list)
    }

    class ExploreViewModelFactory(private val dataRepository: DataRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExploreViewModel(dataRepository) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}
