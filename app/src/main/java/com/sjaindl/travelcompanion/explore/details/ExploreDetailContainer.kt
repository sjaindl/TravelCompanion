package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.TCNavHost
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailContainer(pinId: Long) {
    val navController = rememberNavController()
    TravelCompanionTheme {
        Scaffold(
            bottomBar = { DetailsBottomNavigation(navController = navController) }
        ) { innerPadding ->
            TCNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                pinId = pinId,
            )
        }
    }
}
