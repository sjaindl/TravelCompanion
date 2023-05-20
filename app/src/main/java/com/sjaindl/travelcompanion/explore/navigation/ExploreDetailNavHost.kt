package com.sjaindl.travelcompanion.explore.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
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

private val exploreDetailPhotos by lazy {
    BottomNavItem.ExploreDetailPhotos()
}

private val exploreDetailInfo by lazy {
    BottomNavItem.ExploreDetailInfo()
}

@Composable
fun ExploreDetailNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pinId: Long,
    onGoToFullScreenPhoto: (bitmap: ImageBitmap?, url: String?, title: String) -> Unit,
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
            val argPinId =
                navBackStackEntry.arguments?.getLong(BottomNavItem.pinArg) ?: throw IllegalStateException("No pinId given")
            ExploreDetailPhotosMainScreen(
                pinId = argPinId,
                onGoToFullScreenPhoto = { _bitmap, _url, _title ->
                    onGoToFullScreenPhoto(_bitmap, _url, _title)
                },
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
