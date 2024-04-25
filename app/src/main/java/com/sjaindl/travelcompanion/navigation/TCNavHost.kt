package com.sjaindl.travelcompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.MainScreen
import com.sjaindl.travelcompanion.auth.AUTHENTICATION_NAVIGATION
import com.sjaindl.travelcompanion.auth.authenticationGraph
import com.sjaindl.travelcompanion.explore.details.bottomnav.BottomNavItem.ExploreDetailPhotos
import com.sjaindl.travelcompanion.explore.navigation.EXPLORE_NAVIGATION
import com.sjaindl.travelcompanion.explore.navigation.exploreGraph
import com.sjaindl.travelcompanion.explore.navigation.navigateToExploreDetailContainer
import com.sjaindl.travelcompanion.plan.navigation.PLAN_NAVIGATION
import com.sjaindl.travelcompanion.plan.navigation.navigateToAddPlan
import com.sjaindl.travelcompanion.plan.navigation.planGraph
import com.sjaindl.travelcompanion.profile.navigation.PROFILE_NAVIGATION
import com.sjaindl.travelcompanion.profile.navigation.profileGraph
import com.sjaindl.travelcompanion.remember.navigation.rememberGraph
import com.sjaindl.travelcompanion.remember.navigation.rememberNavigation
import com.sjaindl.travelcompanion.util.navigateSingleTopTo

// Compile-time safe navigation annotation processing lib (had some issues with kotlin versioning, though):
// https://proandroiddev.com/safe-compose-arguments-an-improved-way-to-navigate-in-jetpack-compose-95c84722eec2

// https://developer.android.com/jetpack/compose/navigation#nested-nav
// https://medium.com/google-developer-experts/modular-navigation-with-jetpack-compose-fda9f6b2bef7

const val TC_HOME_ROUTE = "tcHome"

private fun NavGraphBuilder.mainScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToRemember: () -> Unit,
    onNavigateToProfile: () -> Unit,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
) {
    composable(
        route = TC_HOME_ROUTE,
    ) {
        MainScreen(
            onNavigateToExplore = onNavigateToExplore,
            onNavigateToPlan = onNavigateToPlan,
            onNavigateToRemember = onNavigateToRemember,
            onNavigateToProfile = onNavigateToProfile,
            canNavigateBack = canNavigateBack,
            navigateUp = navigateUp,
        )
    }
}

@Composable
fun TCNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = { },
    onShowDetails: (Long) -> Unit = { pin ->
        navController.navigateToExploreDetailContainer(
            pinId = pin,
            navOptions = NavOptions
                .Builder()
                .setLaunchSingleTop(singleTop = true)
                .build(),
        )
    },
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
    onClickedProfile: () -> Unit = { },
    openProfile: Boolean = false,
    profileOpened: () -> Unit = { },
    openAuthentication: Boolean = false,
    authenticationOpened: () -> Unit = { },
    onAuthenticateAndOpenPlan: () -> Unit = { },
    onAuthenticateAndOpenAddPlan: (String) -> Unit = { },
    openPlan: Boolean = false,
    openAddPlan: String? = null,
    openedPlan: () -> Unit = { },
    openedAddPlan: () -> Unit = { },
) {
    if (openAuthentication) {
        navController.navigateSingleTopTo(route = AUTHENTICATION_NAVIGATION)
        authenticationOpened()
    }

    if (openProfile) {
        navController.navigateSingleTopTo(route = PROFILE_NAVIGATION)
        profileOpened()
    }

    if (openPlan) {
        navController.navigateSingleTopTo(route = PLAN_NAVIGATION)
        openedPlan()
    }

    openAddPlan?.let { destination ->
        navController.navigateToAddPlan(destination = destination)
        openedAddPlan()
    }

    NavHost(
        navController = navController,
        startDestination = TC_HOME_ROUTE,
        modifier = modifier,
    ) {
        mainScreen(
            onNavigateToExplore = {
                navController.navigateSingleTopTo(route = EXPLORE_NAVIGATION)
            },
            onNavigateToPlan = {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    navController.navigateSingleTopTo(route = PLAN_NAVIGATION)
                } else {
                    onAuthenticateAndOpenPlan()
                }
            },
            onNavigateToRemember = {
                navController.navigateSingleTopTo(route = rememberNavigation)
            },
            onNavigateToProfile = {
                onClickedProfile()
            },
            canNavigateBack = false,
            navigateUp = {
                navController.navigateUp()
            },
        )

        authenticationGraph(
            navController = navController,
            signInWithGoogle = signInWithGoogle,
            signInWithFacebook = signInWithFacebook,
            signInWithMail = signInWithMail,
            signUpWithMail = signUpWithMail,
        )

        profileGraph(navController = navController, onClose = onClose)

        exploreGraph(
            navController = navController,
            onPlanTrip = { destination ->
                if (FirebaseAuth.getInstance().currentUser != null) {
                    navController.navigateToAddPlan(destination = destination)
                } else {
                    onAuthenticateAndOpenAddPlan(destination)
                }
            }
        )

        planGraph(
            navController = navController,
            onShowDetails = onShowDetails,
            onChoosePlanImage = { pinId ->
                val route = ExploreDetailPhotos.routeWithSetArguments(pinId, true)
                navController.navigate(route)
            }
        )

        rememberGraph(
            navController = navController,
        )
    }
}
