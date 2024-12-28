package com.sjaindl.travelcompanion.explore.details.bottomnav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailHome
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailInfo
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailPhotos
import com.sjaindl.travelcompanion.util.navigateSingleTopTo

@Composable
fun ExploreDetailsBottomNavigation(navController: NavHostController, pinId: Long) {
    val items = listOf(
        ExploreDetailHome,
        ExploreDetailPhotos,
        ExploreDetailInfo,
    )

    NavigationBar(
        containerColor = colorResource(id = R.color.colorMain),
        contentColor = Color.Black,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = "")
                },
                label = {
                    Text(
                        text = stringResource(id = item.titleRes),
                        fontSize = 9.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Black.copy(0.4f),
                ),
                alwaysShowLabel = true,
                selected = currentRoute == item.routeWithArgs,
                onClick = {
                    navController.navigateSingleTopTo(route = item.routeWithSetArguments(pinId, false))
                }
            )
        }
    }
}
