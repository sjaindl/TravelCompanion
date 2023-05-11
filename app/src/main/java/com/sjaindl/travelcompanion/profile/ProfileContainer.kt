package com.sjaindl.travelcompanion.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.TCNavHost
import com.sjaindl.travelcompanion.profileHome
import com.sjaindl.travelcompanion.theme.TravelCompanionTheme

@Composable
fun ProfileContainer(onClose: () -> Unit) {
    val navController = rememberNavController()

    TravelCompanionTheme {
        TCNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize(),
            startDestination = profileHome.route,
            onClose = onClose,
        )
    }
}
