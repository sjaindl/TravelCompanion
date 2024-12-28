package com.sjaindl.travelcompanion.profile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.activity
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.DataAccessRationaleActivity
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen
import com.sjaindl.travelcompanion.profile.ProfileViewModel

private const val PROFILE_ROUTE = "profile"
private const val PERSONAL_INFO_ROUTE = "personalInfo"
private const val DATA_ACCESS_ROUTE = "dataAccess"

const val PROFILE_NAVIGATION = "profileNavigation"

private fun NavGraphBuilder.profileScreen(
    onClose: () -> Unit = { },
    goToPersonalInfo: () -> Unit = { },
    goToDataAccessRationaleInfo: () -> Unit = { },
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    composable(route = PROFILE_ROUTE) {
        val viewModel = hiltViewModel<ProfileViewModel>()

        ProfileScreen(
            initials = viewModel.initials,
            userName = viewModel.userName,
            logout = viewModel::logout,
            deleteAccount = viewModel::deleteAccount,
            onClose = onClose,
            goToPersonalInfo = goToPersonalInfo,
            goToDataAccessRationaleInfo = goToDataAccessRationaleInfo,
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

fun NavController.navigateToPersonalInfoScreen(navOptions: NavOptions? = null) {
    this.navigate(route = PERSONAL_INFO_ROUTE, navOptions = navOptions)
}

fun NavController.navigateToDataAccessRationale(navOptions: NavOptions? = null) {
    this.navigate(route = DATA_ACCESS_ROUTE, navOptions = navOptions)
}

fun NavGraphBuilder.profileGraph(navController: NavController, onClose: () -> Unit = { }) {
    navigation(startDestination = PROFILE_ROUTE, route = PROFILE_NAVIGATION) {
        profileScreen(
            onClose = onClose,
            goToPersonalInfo = {
                navController.navigateToPersonalInfoScreen()
            },
            goToDataAccessRationaleInfo = {
                navController.navigateToDataAccessRationale()
            },
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
        )

        personalInfoScreen(
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
        )

        activity(route = DATA_ACCESS_ROUTE) {
            activityClass = DataAccessRationaleActivity::class
        }
    }
}
