package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.ui.graphics.ImageBitmap
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
import com.sjaindl.travelcompanion.explore.search.SearchPlaceAutocompleteScreen
import com.sjaindl.travelcompanion.navigation.DestinationItem

private const val pinArg = "pin"
private const val placesArg = "places"

private val placesArgs = listOf(navArgument(placesArg) {
    type = NavType.StringType
    nullable = true
})

private val pinArgs = listOf(navArgument(pinArg) {
    type = NavType.LongType
    // defaultValue = 0
})

private const val exploreRoute = "explore"
private const val searchPlaceRoute = "searchPlaces"
private const val exploreDetailsContainerRoute = "exploreDetailsContainer"
private const val photoFullScreenRoute = "photoFullScreen"

private val exploreHome by lazy {
    ExploreHome()
}

private val searchPlace by lazy {
    SearchPlace()
}

private val photoFullScreen by lazy {
    PhotoFullScreen()
}

val exploreDetailContainer by lazy {
    ExploreDetailContainer()
}

const val exploreNavigation = "exploreNavigation"

private var url: String? = null
private var title: String? = null
private var bitmap: ImageBitmap? = null

data class PhotoFullScreen(
    override var route: String = photoFullScreenRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

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
