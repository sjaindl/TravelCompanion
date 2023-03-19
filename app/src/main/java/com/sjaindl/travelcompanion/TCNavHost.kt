package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.BottomNavItem.Companion.pinIdArg
import com.sjaindl.travelcompanion.explore.details.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailInfoScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailPhotosScreen

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
        startDestination = exploreDetailHome.routeWithArgs,
        modifier = modifier
    ) {

        // Hack: need to set arg of startDestination as default value:
        // https://stackoverflow.com/questions/70404038/jetpack-compose-navigation-pass-argument-to-startdestination
        val pinArgsWithDefault = BottomNavItem.pinArgsWithDefaultValue(startDestinationPinId)

        composable(
            route = exploreDetailHome.routeWithArgs,
            arguments = pinArgsWithDefault,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinIdArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId = pinId)
        }
        composable(
            route = exploreDetailPhotos.routeWithArgs,
            arguments = exploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinIdArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailPhotosScreen(pinId = pinId)
        }
        composable(
            route = exploreDetailInfo.routeWithArgs,
            arguments = exploreDetailInfo.arguments,
        ) { navBackStackEntry ->
            val pinId = navBackStackEntry.arguments?.getLong(pinIdArg) ?: throw java.lang.IllegalStateException("No pinId given")
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
