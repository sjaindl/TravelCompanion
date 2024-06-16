package com.sjaindl.travelcompanion.explore.navigation

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.explore.ExploreScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailContainer
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen
import com.sjaindl.travelcompanion.explore.search.PickPlaceScreen
import com.sjaindl.travelcompanion.explore.search.SearchPlaceAutocompleteScreen
import com.sjaindl.travelcompanion.navigation.DestinationItem

private const val PLACES_ARG = "places"

private object ExploreHome : DestinationItem {
    override var route = "explore"
    override var arguments = listOf(navArgument(PLACES_ARG) {
        type = NavType.StringType
        nullable = true
    })
    override var routeWithArgs = "$route?$PLACES_ARG={$PLACES_ARG}"
}

private fun NavController.navigateToExploreHome(encodedPlaces: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${ExploreHome.route}?$PLACES_ARG=$encodedPlaces", navOptions = navOptions)
}

private const val PIN_ARG = "pin"

private object ExploreDetailContainer : DestinationItem {
    override var route = "exploreDetailsContainer"
    override var arguments = listOf(navArgument(PIN_ARG) {
        type = NavType.LongType
    })
    override val routeWithArgs = "$route/{$PIN_ARG}"
}

fun NavController.navigateToExploreDetailContainer(pinId: Long, navOptions: NavOptions? = null) {
    this.navigate(route = "${ExploreDetailContainer.route}/$pinId", navOptions = navOptions)
}

private object SearchPlace : DestinationItem {
    override var route = "searchPlaces"
}

private fun NavController.navigateToSearchPlace(navOptions: NavOptions? = null) {
    this.navigate(route = SearchPlace.route, navOptions = navOptions)
}

private const val LATITUDE_ARG = "latitude"
private const val LONGITUDE_ARG = "longitude"

private object PickPlace : DestinationItem {
    override var route = "pickPlace"
    override var arguments = listOf(
        navArgument(LATITUDE_ARG) {
            type = NavType.FloatType
        },
        navArgument(LONGITUDE_ARG) {
            type = NavType.FloatType
        },
    )
    override var routeWithArgs = "$route/{$LATITUDE_ARG}/{$LONGITUDE_ARG}"
}

private fun NavController.navigateToPickPlace(latitude: Float, longitude: Float, navOptions: NavOptions? = null) {
    this.navigate(route = "${PickPlace.route}/$latitude/$longitude", navOptions = navOptions)
}

const val EXPLORE_NAVIGATION = "exploreNavigation"
private const val EXPLORE_HOME_DEEPLINK = "com.sjaindl.travelcompanion://explore"

fun NavGraphBuilder.exploreGraph(
    navController: NavController,
    onPlanTrip: (String) -> Unit,
) {
    navigation(startDestination = ExploreHome.routeWithArgs, route = EXPLORE_NAVIGATION) {
        composable(
            route = ExploreHome.routeWithArgs,
            arguments = ExploreHome.arguments,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = EXPLORE_HOME_DEEPLINK
                    action = Intent.ACTION_VIEW
                }
            )
        ) { navBackStackEntry ->
            val encodedPlaces = navBackStackEntry.arguments?.getString(PLACES_ARG)

            ExploreScreen(
                encodedPlaces = encodedPlaces,
                onSearch = {
                    navController.navigateToSearchPlace()
                },
                onPickedLocation = { latitude, longitude ->
                    navController.navigateToPickPlace(latitude = latitude, longitude = longitude)
                },
                onNavigateToExploreDetails = { pinId ->
                    navController.navigateToExploreDetailContainer(pinId = pinId)
                },
                onPlanTrip = onPlanTrip,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },

                /*
                Stable without strong skipping mode enabled:
                onSearch = remember(navController) {
                    {
                        navController.navigateToSearchPlace()
                    }
                },
                onPickedLocation = remember(navController) {
                    { latitude, longitude ->
                        navController.navigateToPickPlace(latitude = latitude, longitude = longitude)
                    }
                },
                onNavigateToExploreDetails = remember(navController) {
                    { pinId ->
                        navController.navigateToExploreDetailContainer(pinId = pinId)
                    }
                },
                onPlanTrip = onPlanTrip,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = navController::navigateUp,
             */
            )
        }

        composable(
            route = SearchPlace.route,
        ) {
            SearchPlaceAutocompleteScreen(
                onPickedPlace = { place ->
                    navController.popBackStack()

                    navController.navigateToExploreHome(
                        encodedPlaces = place,
                        navOptions = NavOptions
                            .Builder()
                            .setLaunchSingleTop(singleTop = true)
                            .build(),
                    )
                }
            )
        }

        composable(
            route = PickPlace.routeWithArgs,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val latitude = args?.getString(LATITUDE_ARG)?.toFloat() ?: throw IllegalStateException("No latitude given")
            val longitude = args.getString(LONGITUDE_ARG)?.toFloat() ?: throw IllegalStateException("No longitude given")

            PickPlaceScreen(
                latitude = latitude,
                longitude = longitude,
                onPickedPlace = { place ->
                    navController.popBackStack()

                    navController.navigateToExploreHome(
                        encodedPlaces = place,
                        navOptions = NavOptions
                            .Builder()
                            .setLaunchSingleTop(singleTop = true)
                            .build(),
                    )
                }
            )
        }

        composable(
            route = ExploreDetailContainer.routeWithArgs,
            arguments = ExploreDetailContainer.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(PIN_ARG) ?: throw IllegalStateException("No pinId given")
            ExploreDetailContainer(pinId = pinId)
        }

        composable(
            route = ExploreDetailPhotos.routeWithArgs,
            arguments = ExploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val argPinId = args?.getLong(BottomNavItem.PIN_ARG) ?: throw IllegalStateException("No pinId given")
            val isPickerMode = args.getBoolean(BottomNavItem.PICKER_MODE)

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
