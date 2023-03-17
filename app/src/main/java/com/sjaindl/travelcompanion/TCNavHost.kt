package com.sjaindl.travelcompanion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailInfoScreen
import com.sjaindl.travelcompanion.explore.details.ExploreDetailPhotosScreen

@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pinId: Long,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.ExploreDetailHome().route,
        modifier = modifier
    ) {
        val exploreDetailHome = BottomNavItem.ExploreDetailHome()

        composable(
            route = exploreDetailHome.route,
            arguments = exploreDetailHome.arguments
        ) { navBackStackEntry ->
            //val pinId = navBackStackEntry.arguments?.getLong(BottomNavItem.pinIdArg) ?: throw java.lang.IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId)
        }
        composable(route = BottomNavItem.ExploreDetailPhotos().route) {
            ExploreDetailPhotosScreen()
        }
        composable(route = BottomNavItem.ExploreDetailInfo().route) {
            ExploreDetailInfoScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
