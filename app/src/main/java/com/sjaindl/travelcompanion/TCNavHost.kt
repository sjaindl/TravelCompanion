package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.BottomNavItem.Companion.pinArg
import com.sjaindl.travelcompanion.explore.details.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailInfoScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailPhotosMainScreen

@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestinationPinId: Long,
) {
    val exploreDetailHome = BottomNavItem.ExploreDetailHome()
    val exploreDetailPhotos = BottomNavItem.ExploreDetailPhotos()
    val exploreDetailInfo = BottomNavItem.ExploreDetailInfo()

    NavHost(
        navController = navController,
        startDestination = exploreDetailHome.route,
        modifier = modifier
    ) {
        composable(
            route = exploreDetailHome.route,
        ) {
            ExploreDetailHomeScreen(pinId = startDestinationPinId)
        }

        composable(
            route = exploreDetailHome.routeWithArgs,
            arguments = exploreDetailHome.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId = pinId)
        }

        composable(
            route = exploreDetailPhotos.routeWithArgs,
            arguments = exploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailPhotosMainScreen(pinId = pinId)
        }

        composable(
            route = exploreDetailInfo.routeWithArgs,
            arguments = exploreDetailInfo.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailInfoScreen(pinId = pinId)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        graph.startDestinationRoute?.let {
            popUpTo(it) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }

// Compile-time safe navigation annotation processing lib (had some issues with kotlin versioning, though):
// https://proandroiddev.com/safe-compose-arguments-an-improved-way-to-navigate-in-jetpack-compose-95c84722eec2
