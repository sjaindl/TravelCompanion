package com.sjaindl.travelcompanion.explore.details.bottomnav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.sjaindl.travelcompanion.R
import com.sjaindl.travelcompanion.explore.navigation.NamedDestinationItem

@Composable
fun ExploreDetailsBottomNavigation(backStack: NavBackStack, pinId: Long) {
    val items: List<NamedDestinationItem> = listOf(
        ExploreDetailHome(pinId),
        ExploreDetailPhotos(pinId, false),
        ExploreDetailInfo(pinId),
    )

    NavigationBar(
        containerColor = colorResource(id = R.color.colorMain),
        contentColor = Color.Black,
    ) {
        val currentRoute = backStack.getOrNull(backStack.size - 1) as? NamedDestinationItem

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.destination.icon(), contentDescription = "")
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
                selected = currentRoute == item,
                onClick = {
                    backStack.add(item as NavKey)
                }
            )
        }
    }
}
