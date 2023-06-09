package com.sjaindl.travelcompanion.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.repository.DataRepository
import com.sjaindl.travelcompanion.util.randomStringByKotlinRandom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class PlaceDetail(val latitude: Double, val longitude: Double, val name: String)

class ExploreViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _dialogTitle = MutableStateFlow("")
    var dialogTitle = _dialogTitle.asStateFlow()

    private val _onShowDetails = MutableStateFlow(0L)
    var onShowDetails = _onShowDetails.asStateFlow()

    private val _exception: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    var exception = _exception.asStateFlow()

    private val _placeDetails = MutableStateFlow<List<PlaceDetail>>(emptyList())
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
        _showDialog.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return

        _onShowDetails.value = pin.id
    }

    fun onDelete() {
        _showDialog.value = false

        val pin = dataRepository.singlePin(name = dialogTitle.value) ?: return
        dataRepository.deletePin(pin.id)

        addPersistedPinsToMap()
    }

    fun onDismiss() {
        _showDialog.value = false
    }

    fun clickedOnPlace(name: String?) {
        if (name == null) return

        _dialogTitle.value = name
        _showDialog.value = true
    }

    fun clickedOnDetails() {
        _onShowDetails.value = 0L
    }

    fun fetchPlaceDetails(placeId: String) = viewModelScope.launch {
        googleClient.placeDetail(placeId, sessionToken)
            .onSuccess { details ->
                val location = details.result.geometry.location
                val name = details.result.name ?: return@launch
                val placeDetail = PlaceDetail(latitude = location.lat, longitude = location.lng, name = name)
                newlyAddedLocation = placeDetail

                val list = placeDetails.value.toMutableList().apply {
                    add(placeDetail)
                }
                _placeDetails.value = list

                try {
                    val countryCode = geoNamesClient.fetchCountryCode(latitude = location.lat, longitude = location.lng)

                    val component = details.result.addressComponents?.firstOrNull {
                        it.types.contains("country")
                    }

                    dataRepository.insertPin(
                        id = 0,
                        address = details.result.formattedAddress,
                        country = component?.longName,
                        countryCode = countryCode,
                        creationDate = Clock.System.now(),
                        latitude = details.result.geometry.location.lat,
                        longitude = details.result.geometry.location.lng,
                        name = name,
                        phoneNumber = null,
                        placeId = placeId,
                        rating = null,
                        url = details.result.url,
                    )
                } catch (exception: Exception) {
                    _exception.value = exception
                }
            }.onFailure {
                _exception.value = it
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

        _placeDetails.value = list
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
