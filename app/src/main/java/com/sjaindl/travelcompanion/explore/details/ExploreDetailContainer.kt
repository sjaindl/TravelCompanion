package com.sjaindl.travelcompanion.explore.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.explore.details.bottomnav.ExploreDetailsBottomNavigation
import com.sjaindl.travelcompanion.explore.navigation.ExploreDetailNavHost
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ExploreDetailContainer(
    pinId: Long,
    onGoToFullScreenPhoto: (bitmap: ImageBitmap?, url: String?, title: String) -> Unit,
) {
    val navController = rememberNavController()

    TravelCompanionTheme {
        Scaffold(
            bottomBar = {
                ExploreDetailsBottomNavigation(navController = navController, pinId = pinId)
            },
        ) { innerPadding ->
            ExploreDetailNavHost(
                navController = navController,
                pinId = pinId,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onGoToFullScreenPhoto = onGoToFullScreenPhoto,
            )
        }
    }
}

@Preview
@Composable
fun ExploreDetailContainerPreview() {
    ExploreDetailContainer(
        pinId = 1,
        onGoToFullScreenPhoto = { _, _, _ -> }
    )
}
