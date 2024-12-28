package com.sjaindl.travelcompanion.explore.details.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjaindl.travelcompanion.Pin
import com.sjaindl.travelcompanion.api.google.GoogleConstants
import com.sjaindl.travelcompanion.api.lonelyplanet.LonelyPlanetConstants
import com.sjaindl.travelcompanion.api.wiki.WikiConstants.UrlComponents.domainWikiVoyage
import com.sjaindl.travelcompanion.api.wiki.WikiConstants.UrlComponents.domainWikipedia
import com.sjaindl.travelcompanion.api.wiki.WikiConstants.UrlComponents.urlProtocol
import com.sjaindl.travelcompanion.api.wiki.WikiConstants.UrlComponents.wikiLinkPath
import com.sjaindl.travelcompanion.di.TCInjector
import com.sjaindl.travelcompanion.repository.DataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExploreInfoViewModelFactory::class)
class ExploreInfoViewModel @AssistedInject constructor(
    dataRepository: DataRepository,
    @Assisted pinId: Long,
    @Assisted private val infoType: InfoType,
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data class Done(val url: String?) : State()
        data class Error(val throwable: Throwable) : State()
        data object NoData : State()
    }

    private var pin: Pin? = dataRepository.singlePin(pinId)
    private var wikiClient = TCInjector.wikiClient

    private var _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    var state = _state.asStateFlow()

    val place: String?
        get() = pin?.name

    init {
        viewModelScope.launch {
            when (infoType) {
                InfoType.WIKIPEDIA -> {
                    val place = place
                    if (place == null) {
                        _state.value = State.NoData
                    } else {
                        wikiClient.fetchWikiLink(name = place, domain = domainWikipedia)
                            .onSuccess {
                                val url = "${urlProtocol}://${domainWikipedia}?${wikiLinkPath}=${it.query.pageId}"
                                _state.value = State.Done(url = url)
                            }
                            .onFailure {
                                _state.value = State.Error(it)
                            }
                    }
                }

                InfoType.WIKIVOYAGE -> {
                    val place = place
                    if (place == null) {
                        _state.value = State.NoData
                    } else {
                        wikiClient.fetchWikiLink(name = place, domain = domainWikiVoyage)
                            .onSuccess {
                                val url = "${urlProtocol}://${domainWikiVoyage}?${wikiLinkPath}=${it.query.pageId}"
                                _state.value = State.Done(url = url)
                            }
                            .onFailure {
                                _state.value = State.Error(it)
                            }
                    }
                }

                InfoType.GOOGLE -> {
                    if (place == null) {
                        _state.value = State.NoData
                    } else {
                        val urlComponents = GoogleConstants.UrlComponents
                        val query = "${GoogleConstants.ParameterKeys.SEARCH_QUERY}=$place"
                        val url = "${urlComponents.URL_PROTOCOL}://${urlComponents.DOMAIN_SEARCH}${urlComponents.PATH_SEARCH}?$query"

                        _state.value = State.Done(url)
                    }
                }

                InfoType.LONELYPLANET -> {
                    if (place == null) {
                        _state.value = State.NoData
                    } else {
                        val urlComponents = LonelyPlanetConstants.UrlComponents
                        val query = "${LonelyPlanetConstants.ParameterKeys.searchQuery}=$place"
                        val url = "${urlComponents.urlProtocol}://www.${urlComponents.domain}/${urlComponents.pathSearch}?$query"

                        _state.value = State.Done(url)
                    }
                }
            }
        }
    }
}

@AssistedFactory
interface ExploreInfoViewModelFactory {
    fun create(
        pinId: Long,
        infoType: InfoType,
    ): ExploreInfoViewModel
}

