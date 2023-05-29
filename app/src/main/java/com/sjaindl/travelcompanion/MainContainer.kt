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
    onAuthenticateAndOpenAddPlan: (String) -> Unit = { },
    openPlan: Boolean,
    openAddPlan: String? = null,
    openedPlan: () -> Unit = { },
    openedAddPlan: () -> Unit = { },
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
            onAuthenticateAndOpenAddPlan = onAuthenticateAndOpenAddPlan,
            openPlan = openPlan,
            openAddPlan = openAddPlan,
            onClose = {
                navController.popBackStack()
            },
            openedPlan = openedPlan,
            openedAddPlan = openedAddPlan,
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
