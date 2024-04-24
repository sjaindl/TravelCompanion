package com.sjaindl.travelcompanion.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen

private const val PROFILE_ROUTE = "profile"
private const val PERSONAL_INFO_ROUTE = "personalInfo"

const val PROFILE_NAVIGATION = "profileNavigation"

private fun NavGraphBuilder.profileScreen(
    onClose: () -> Unit = { },
    goToPersonalInfo: () -> Unit = { },
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    composable(
        route = PROFILE_ROUTE,
    ) {
        ProfileScreen(
            onClose = onClose,
            goToPersonalInfo = goToPersonalInfo,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
        )
    }
}

private fun NavGraphBuilder.personalInfoScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    composable(
        route = PERSONAL_INFO_ROUTE,
    ) {
        PersonalInfoScreen(
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
        )
    }
}

fun NavGraphBuilder.profileGraph(navController: NavController, onClose: () -> Unit = { }) {
    navigation(startDestination = PROFILE_ROUTE, route = PROFILE_NAVIGATION) {
        profileScreen(
            onClose = onClose,
            goToPersonalInfo = {
                navController.navigate(route = PERSONAL_INFO_ROUTE)
            },
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
        )

        personalInfoScreen(
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
        )
    }
}
