package com.sjaindl.travelcompanion.plan.detail.bottomnav

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.util.navigateSingleTopTo

@Composable
fun PlanDetailsBottomNavigation(navController: NavHostController, planName: String) {
    val items = listOf(
        PlanBottomNavItem.PlanDetailAddHotel(),
        PlanBottomNavItem.PlanDetailAddRestaurant(),
        PlanBottomNavItem.PlanDetailAddAttraction(),
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.colorMain),
        contentColor = Color.Black,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = "") },
                label = {
                    Text(
                        text = stringResource(id = item.titleRes),
                        fontSize = 9.sp,
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigateSingleTopTo(route = item.routeWithSetArguments(planName))
                }
            )
        }
    }
}
