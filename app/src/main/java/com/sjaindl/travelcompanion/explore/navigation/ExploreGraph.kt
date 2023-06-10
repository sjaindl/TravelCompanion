package com.sjaindl.travelcompanion.explore.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.explore.ExploreScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailContainer
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen
import com.sjaindl.travelcompanion.explore.search.PickPlaceScreen
import com.sjaindl.travelcompanion.explore.search.SearchPlaceAutocompleteScreen
import com.sjaindl.travelcompanion.navigation.DestinationItem

private const val pinArg = "pin"
private const val placesArg = "places"
private const val latitudeArg = "latitude"
private const val longitudeArg = "longitude"

private val placesArgs = listOf(navArgument(placesArg) {
    type = NavType.StringType
    nullable = true
})

private val pinArgs = listOf(navArgument(pinArg) {
    type = NavType.LongType
    // defaultValue = 0
})

private val pickPlaceArgs = listOf(
    navArgument(latitudeArg) {
        type = NavType.FloatType
        // defaultValue = 0
    },
    navArgument(longitudeArg) {
        type = NavType.FloatType
        //defaultValue = false
    },
)

private const val exploreRoute = "explore"
private const val searchPlaceRoute = "searchPlaces"
private const val pickPlaceRoute = "pickPlace"
private const val exploreDetailsContainerRoute = "exploreDetailsContainer"

private val exploreHome by lazy {
    ExploreHome()
}

private val searchPlace by lazy {
    SearchPlace()
}

private val pickPlace by lazy {
    PickPlace()
}

val exploreDetailContainer by lazy {
    ExploreDetailContainer()
}

const val exploreNavigation = "exploreNavigation"

data class ExploreHome(
    override var route: String = exploreRoute,
    override var arguments: List<NamedNavArgument> = placesArgs,
    override var routeWithArgs: String = "$route?$placesArg={$placesArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val encodedPlaces = arguments.firstOrNull() as? String ?: return route

        return "$route?$placesArg=$encodedPlaces"
    }
}

data class ExploreDetailContainer(
    override var route: String = exploreDetailsContainerRoute,
    override var arguments: List<NamedNavArgument> = pinArgs,
    override var routeWithArgs: String = "$route/{$pinArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val pinId = arguments.firstOrNull() as? Long ?: return route

        return "$route/$pinId"
    }
}

data class SearchPlace(
    override var route: String = searchPlaceRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class PickPlace(
    override var route: String = pickPlaceRoute,
    override var arguments: List<NamedNavArgument> = pickPlaceArgs,
    override var routeWithArgs: String = "$route/{$latitudeArg}/{$longitudeArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        if (arguments.size < 2) return route
        val latitude = arguments.firstOrNull() as? Float ?: return route
        val longitude = arguments[1] as? Float ?: return route

        return "$route/$latitude/$longitude"
    }
}

fun NavGraphBuilder.exploreGraph(
    navController: NavController,
    onPlanTrip: (String) -> Unit,
) {
    navigation(startDestination = exploreHome.routeWithArgs, route = exploreNavigation) {
        composable(
            route = exploreHome.routeWithArgs,
            arguments = exploreHome.arguments,
        ) { navBackStackEntry ->
            val encodedPlaces = navBackStackEntry.arguments?.getString(placesArg)
            ExploreScreen(
                encodedPlaces = encodedPlaces,
                onSearch = {
                    navController.navigate(searchPlace.route)
                },
                onPickedLocation = { latitude, longitude ->
                    navController.navigate(pickPlace.routeWithSetArguments(latitude, longitude))
                },
                onNavigateToExploreDetails = { pinId ->
                    navController.navigate(exploreDetailContainer.routeWithSetArguments(pinId))
                },
                onPlanTrip = onPlanTrip,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = searchPlace.route,
        ) {
            SearchPlaceAutocompleteScreen(
                onPickedPlace = { place ->
                    navController.popBackStack()
                    navController.navigate(route = exploreHome.routeWithSetArguments(place)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = pickPlace.routeWithArgs,
        ) { navBackStackEntry ->
            val latitude =
                navBackStackEntry.arguments?.getString(latitudeArg)?.toFloat() ?: throw IllegalStateException("No latitude given")
            val longitude =
                navBackStackEntry.arguments?.getString(longitudeArg)?.toFloat() ?: throw IllegalStateException("No longitude given")

            PickPlaceScreen(
                latitude = latitude,
                longitude = longitude,
                onPickedPlace = { place ->
                    navController.popBackStack()
                    navController.navigate(route = exploreHome.routeWithSetArguments(place)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = exploreDetailContainer.routeWithArgs,
            arguments = exploreDetailContainer.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw IllegalStateException("No pinId given")
            ExploreDetailContainer(pinId = pinId)
        }

        composable(
            route = exploreDetailPhotos.routeWithArgs,
            arguments = exploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.pinArg) ?: throw IllegalStateException("No pinId given")
            val isPickerMode =
                navBackStackEntry.arguments?.getBoolean(BottomNavItem.pickerMode) ?: throw IllegalStateException("No pickerMode given")

            ExploreDetailPhotosMainScreen(
                pinId = argPinId,
                isPickerMode = isPickerMode,
                onPhotoChosen = {
                    navController.navigateUp()
                },
                isChoosePlanImageMode = false,
            )
        }
    }
}
