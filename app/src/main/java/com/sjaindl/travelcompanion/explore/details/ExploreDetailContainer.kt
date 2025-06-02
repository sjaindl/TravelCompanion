package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailHome
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailsBottomNavigation
import com.sjaindl.travelcompanion.explore.navigation.ExploreDetailNavDisplay
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailContainer(pinId: Long, isChoosePlanImageMode: Boolean, rootBackStack: NavBackStack) {
    val backStack = rememberNavBackStack(
        if (isChoosePlanImageMode) ExploreDetailPhotos(pinId = pinId, pickerMode = true)
        else ExploreDetailHome(pinId = pinId)
    )

    TravelCompanionTheme {
        Scaffold(
            bottomBar = {
                ExploreDetailsBottomNavigation(backStack = backStack, pinId = pinId)
            },
        ) { innerPadding ->
            ExploreDetailNavDisplay(
                backStack = backStack,
                rootBackStack = rootBackStack,
                pinId = pinId,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

@Preview
@Composable
fun ExploreDetailContainerPreview() {
    val backStack = rememberNavBackStack(ExploreDetailHome(pinId = 1))

    ExploreDetailContainer(
        pinId = 1,
        isChoosePlanImageMode = false,
        rootBackStack = backStack,
    )
}
