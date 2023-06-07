package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.home.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.info.ExploreDetailInfoMainScreen
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen

private val exploreDetailHome by lazy {
    BottomNavItem.ExploreDetailHome()
}

private val exploreDetailInfo by lazy {
    BottomNavItem.ExploreDetailInfo()
}

val exploreDetailPhotos by lazy {
    BottomNavItem.ExploreDetailPhotos()
}

@Composable
fun ExploreDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pinId: Long,
) {
    NavHost(
        navController = navController,
        startDestination = exploreDetailHome.route,
        modifier = modifier,
    ) {
        composable(
            route = exploreDetailHome.route,
            arguments = emptyList(),
        ) {
            ExploreDetailHomeScreen(pinId = pinId)
        }

        composable(
            route = exploreDetailHome.routeWithArgs,
            arguments = exploreDetailHome.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.pinArg) ?: throw IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId = argPinId)
        }

        composable(
            route = exploreDetailPhotos.routeWithArgs,
            arguments = exploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val argPinId = navBackStackEntry.arguments?.getLong(BottomNavItem.pinArg) ?: throw IllegalStateException("No pinId given")
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

        composable(
            route = exploreDetailInfo.routeWithArgs,
            arguments = exploreDetailInfo.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.pinArg) ?: throw IllegalStateException("No pinId given")
            ExploreDetailInfoMainScreen(pinId = argPinId)
        }
    }
}
