package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.sjaindl.travelcompanion.explore.ExploreScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailContainer
import com.sjaindl.travelcompanion.explore.search.PickPlaceScreen
import com.sjaindl.travelcompanion.explore.search.SearchPlaceAutocompleteScreen
import com.sjaindl.travelcompanion.extensions.launchInGoogleEarth
import kotlinx.serialization.Serializable

@Serializable
data class ExploreHome(val encodedPlaces: String?) : NavKey

@Serializable
data class ExploreDetail(val pinId: Long, val isChoosePlanImageMode: Boolean) : NavKey

@Serializable
data object SearchPlace : NavKey

@Serializable
data class PickPlace(val latitude: Float, val longitude: Float) : NavKey

const val EXPLORE_HOME_DEEPLINK = "com.sjaindl.travelcompanion://explore"

@Composable
fun EntryProviderBuilder<NavKey>.ExploreNavigation(
    backStack: NavBackStack,
    onPlanTrip: (String) -> Unit,
) {
    val context = LocalContext.current

    entry<ExploreHome> {
        ExploreScreen(
            encodedPlaces = it.encodedPlaces,
            onSearch = {
                backStack.add(SearchPlace)
            },
            onPickedLocation = { latitude, longitude ->
                backStack.add(PickPlace(latitude = latitude, longitude = longitude))
            },
            onNavigateToExploreDetails = { pinId ->
                backStack.add(ExploreDetail(pinId = pinId, isChoosePlanImageMode = false))
            },
            onPlanTrip = onPlanTrip,
            canNavigateBack = backStack.size > 1,
            onShowInGoogleEarth = { latitude, longitude ->
                context.launchInGoogleEarth(latitude = latitude, longitude = longitude)
            },
            navigateUp = {
                backStack.removeAt(backStack.size - 1)
            },
        )
    }

    entry<SearchPlace> {
        SearchPlaceAutocompleteScreen(
            onPickedPlace = { place ->
                backStack.removeAt(backStack.size - 1)
                backStack.add(
                    ExploreHome(encodedPlaces = place)
                )
            }
        )
    }

    entry<PickPlace> {
        PickPlaceScreen(
            latitude = it.latitude,
            longitude = it.longitude,
            onPickedPlace = { place ->
                backStack.removeAt(backStack.size - 1)
                backStack.add(
                    ExploreHome(encodedPlaces = place)
                )
            }
        )
    }

    entry<ExploreDetail> {
        ExploreDetailContainer(pinId = it.pinId, isChoosePlanImageMode = it.isChoosePlanImageMode, rootBackStack = backStack)
    }
}
