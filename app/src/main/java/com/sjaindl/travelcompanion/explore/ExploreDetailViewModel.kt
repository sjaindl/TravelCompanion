package com.sjaindl.travelcompanion.explore

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.country.RestCountriesClient
import com.sjaindl.travelcompanion.mapper.PinAndCountryToCountryUiMapper
import com.sjaindl.travelcompanion.model.CountryUi
import com.sjaindl.travelcompanion.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreDetailViewModel(pinId: Long, private val dataRepository: DataRepository) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Done(val countryUi: CountryUi) : State()
        data class Error(val message: String? = null, @StringRes val stringRes: Int? = null) : State()
    }

    // TODO: Hilt
    private val client = RestCountriesClient()
    private val mapper = PinAndCountryToCountryUiMapper()

    private var pin: Pin? = dataRepository.singlePin(pinId)

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val countryCode = pin?.countryCode ?: return@launch
            buildCountryUiData(countryCode = countryCode)
        }
    }

    private suspend fun buildCountryUiData(countryCode: String) {
        val country = dataRepository.singleCountry(countryCode) ?: fetchAndStoreCountry(countryCode)
        val fetchedPin = pin
        if (fetchedPin != null && country != null) {
            val countryUi = mapper.map(pin = fetchedPin, country = country)
            _state.value = State.Done(countryUi)
        } else {
            _state.value = State.Error(stringRes = R.string.couldNotRetrieveData)
        }
    }

    private suspend fun fetchAndStoreCountry(countryCode: String): Country? {
        client.fetchCountryDetails(countryCode = countryCode)
            .onSuccess {
                dataRepository.insertCountry(countryCode = countryCode, country = it)
            }
            .onFailure {
                _state.value = State.Error(message = it.localizedMessage ?: it.message ?: it.stackTrace.toString())
            }

        return dataRepository.singleCountry(countryCode)
    }
}

class ExploreDetailViewModelFactory(private val pinId: Long, private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ExploreDetailViewModel(pinId, dataRepository) as T
}
