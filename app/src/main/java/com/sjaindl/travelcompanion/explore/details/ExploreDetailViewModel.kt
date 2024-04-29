package com.sjaindl.travelcompanion.explore.details

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Country
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.api.firestore.CountryApiType.CountryApi
import com.sjaindl.travelcompanion.api.firestore.CountryApiType.CountryApiLocal
import com.sjaindl.travelcompanion.api.firestore.CountryApiType.RestCountries
import com.sjaindl.travelcompanion.api.firestore.FireStoreRemoteConfig
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.mapper.PinAndCountryToCountryUiMapper
import com.sjaindl.travelcompanion.model.CountryUi
import com.sjaindl.travelcompanion.repository.DataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExploreDetailViewModel.ExploreDetailViewModelFactory::class)
class ExploreDetailViewModel @AssistedInject constructor(
    private val savedStateHandle: SavedStateHandle,
    @Assisted pinId: Long,
    private val dataRepository: DataRepository,
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data class Done(val countryUi: CountryUi) : State()
        data class Error(val message: String? = null, @StringRes val stringRes: Int? = null) : State()
    }

    private val restCountriesClient by lazy {
        TCInjector.restCountriesClient
    }

    private val countryApiClient by lazy {
        TCInjector.countryApiClient
    }

    private val mapper = PinAndCountryToCountryUiMapper()

    private var pin = dataRepository.singlePin(pinId)

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
        when (FireStoreRemoteConfig.countryApiType) {
            RestCountries -> {
                restCountriesClient.fetchCountryDetails(countryCode = countryCode)
                    .onSuccess {
                        dataRepository.insertCountry(countryCode = countryCode, country = it)
                    }
                    .onFailure {
                        _state.value = State.Error(message = it.localizedMessage ?: it.message ?: it.stackTrace.toString())
                    }

                return dataRepository.singleCountry(countryCode)
            }

            CountryApi -> {
                countryApiClient.fetchCountryDetails(countryCode = countryCode)
                    .onSuccess {
                        dataRepository.insertCountry(countryCode = countryCode, country = it)
                    }
                    .onFailure {
                        _state.value = State.Error(message = it.localizedMessage ?: it.message ?: it.stackTrace.toString())
                    }

                return dataRepository.singleCountry(countryCode)
            }

            CountryApiLocal -> {
                val response = countryApiClient.fetchCountryDetailsLocal(countryCode = countryCode)
                if (response != null) {
                    dataRepository.insertCountry(countryCode = countryCode, country = response)
                } else {
                    _state.value = State.Error(message = "Could not determine country data")
                }

                return dataRepository.singleCountry(countryCode)
            }
        }
    }

    @AssistedFactory
    interface ExploreDetailViewModelFactory {
        fun create(
            pinId: Long,
        ): ExploreDetailViewModel
    }
}
