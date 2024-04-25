package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailHome
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailInfo
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.details.home.ExploreDetailHomeScreen
import com.sjaindl.travelcompanion.explore.details.info.ExploreDetailInfoMainScreen
import com.sjaindl.travelcompanion.explore.details.photos.ExploreDetailPhotosMainScreen

@Composable
fun ExploreDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pinId: Long,
) {
    NavHost(
        navController = navController,
        startDestination = ExploreDetailHome.route,
        modifier = modifier,
    ) {
        composable(
            route = ExploreDetailHome.route,
        ) {
            ExploreDetailHomeScreen(pinId = pinId)
        }

        composable(
            route = ExploreDetailHome.routeWithArgs,
            arguments = ExploreDetailHome.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.PIN_ARG) ?: throw IllegalStateException("No pinId given")
            ExploreDetailHomeScreen(pinId = argPinId)
        }

        composable(
            route = ExploreDetailPhotos.routeWithArgs,
            arguments = ExploreDetailPhotos.arguments,
        ) { navBackStackEntry ->
            val argPinId = navBackStackEntry.arguments?.getLong(BottomNavItem.PIN_ARG) ?: throw IllegalStateException("No pinId given")
            val isPickerMode =
                navBackStackEntry.arguments?.getBoolean(BottomNavItem.PICKER_MODE) ?: throw IllegalStateException("No pickerMode given")
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
            route = ExploreDetailInfo.routeWithArgs,
            arguments = ExploreDetailInfo.arguments,
        ) { navBackStackEntry ->
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.PIN_ARG) ?: throw IllegalStateException("No pinId given")
            ExploreDetailInfoMainScreen(pinId = argPinId)
        }
    }
}
