package com.sjaindl.travelcompanion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun MainContainer(
    onClickedProfile: () -> Unit,
    openProfile: Boolean,
    profileOpened: () -> Unit = { },
    onAuthenticateAndOpenPlan: () -> Unit,
    openPlan: Boolean,
) {
    val navController = rememberNavController()
    
    TravelCompanionTheme {
        TCNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize(),
            onClickedProfile = onClickedProfile,
            openProfile = openProfile,
            profileOpened = profileOpened,
            onAuthenticateAndOpenPlan = onAuthenticateAndOpenPlan,
            openPlan = openPlan,
            onClose = {
                navController.popBackStack()
            }
        )
    }
}

@Preview
@Composable
fun MainContainerPreview() {
    MainContainer(
        onClickedProfile = { },
        openProfile = false,
        profileOpened = { },
        onAuthenticateAndOpenPlan = { },
        openPlan = false,
    )
}
