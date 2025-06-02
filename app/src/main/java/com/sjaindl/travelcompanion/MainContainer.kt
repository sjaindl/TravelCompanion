package com.sjaindl.travelcompanion

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.google.firebase.auth.FirebaseAuth
import com.sjaindl.travelcompanion.auth.AuthNavigation
import com.sjaindl.travelcompanion.auth.SignInChooser
import com.sjaindl.travelcompanion.explore.navigation.ExploreDetail
import com.sjaindl.travelcompanion.explore.navigation.ExploreHome
import com.sjaindl.travelcompanion.explore.navigation.ExploreNavigation
import com.sjaindl.travelcompanion.plan.navigation.AddPlan
import com.sjaindl.travelcompanion.plan.navigation.PlanHome
import com.sjaindl.travelcompanion.plan.navigation.PlanNavigation
import com.sjaindl.travelcompanion.profile.navigation.Profile
import com.sjaindl.travelcompanion.profile.navigation.ProfileNavigation
import com.sjaindl.travelcompanion.remember.navigation.RememberHome
import com.sjaindl.travelcompanion.remember.navigation.RememberNavigation
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Composable
fun MainContainer(
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
    onClickedProfile: () -> Unit,
    openAuthentication: Boolean,
    authenticationOpened: () -> Unit = { },
    openProfile: Boolean,
    profileOpened: () -> Unit = { },
    onAuthenticateAndOpenPlan: () -> Unit,
    onAuthenticateAndOpenAddPlan: (String) -> Unit = { },
    openPlan: Boolean,
    openAddPlan: String? = null,
    openedPlan: () -> Unit = { },
    openedAddPlan: () -> Unit = { },
    deeplinkIntent: Intent? = null,
    onNavigateToDataAccessRationaleActivity: () -> Unit,
) {
    val backStack = rememberNavBackStack(Main)

    if (openAuthentication) {
        backStack.add(SignInChooser)
        authenticationOpened()
    }

    if (openProfile) {
        onClickedProfile()
        profileOpened()
    }

    if (openPlan) {
        backStack.add(PlanHome)
        openedPlan()
    }

    openAddPlan?.let { destination ->
        backStack.add(AddPlan(destination = destination))
        openedAddPlan()
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Main> {
                MainScreen(
                    onNavigateToExplore = {
                        backStack.add(ExploreHome(encodedPlaces = null))
                    },
                    onNavigateToPlan = {
                        if (FirebaseAuth.getInstance().currentUser != null) {
                            backStack.add(PlanHome)
                        } else {
                            onAuthenticateAndOpenPlan()
                        }
                    },
                    onNavigateToRemember = {
                        backStack.add(RememberHome)
                    },
                    onNavigateToProfile = {
                        backStack.add(Profile)
                    },
                    canNavigateBack = false,
                    navigateUp = {
                        backStack.removeAt(backStack.size - 1)
                    },
                )
            }

            ExploreNavigation(
                backStack = backStack,
                onPlanTrip = {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        backStack.add(AddPlan(destination = it))
                    } else {
                        onAuthenticateAndOpenAddPlan(it)
                    }
                }
            )

            PlanNavigation(
                backStack = backStack,
                onShowDetails = {
                    backStack.add(ExploreDetail(pinId = it, isChoosePlanImageMode = false))
                },
                onChoosePlanImage = {
                    backStack.add(ExploreDetail(pinId = it, isChoosePlanImageMode = true))
                },
            )

            RememberNavigation(
                backStack = backStack,
            )

            AuthNavigation(
                backStack,
                signInWithGoogle = {
                    signInWithGoogle()
                    navigateBackAfterSignIn(backStack = backStack)
                },
                signInWithFacebook = {
                    signInWithFacebook()
                    navigateBackAfterSignIn(backStack = backStack)
                },
                signInWithMail = { email, password ->
                    signInWithMail(email, password)
                    navigateBackAfterSignIn(backStack = backStack)
                },
                signUpWithMail = { email, password, name ->
                    signUpWithMail(email, password, name)
                    navigateBackAfterSignIn(backStack = backStack)
                },
            )

            ProfileNavigation(
                backStack = backStack,
                onNavigateToDataAccessRationaleActivity = onNavigateToDataAccessRationaleActivity,
            )
        }
    )

    // Deeplinks not yet supported in Navigation 3
    /*
    deeplinkIntent?.let {
        navController.handleDeepLink(intent = it)
    }
     */
}

private fun navigateBackAfterSignIn(backStack: NavBackStack) {
    val index = backStack.indexOf(SignInChooser)
    if (index != -1) {
        backStack.removeRange(index, backStack.size - 1)
    } else {
        backStack.removeAt(backStack.size - 1)
    }
}

@Preview
@Composable
fun MainContainerPreview() {
    MainContainer(
        signInWithGoogle = {},
        signInWithFacebook = {},
        signInWithMail = { _: String, _: String -> },
        signUpWithMail = { _: String, _: String, _: String -> },
        onClickedProfile = { },
        openAuthentication = false,
        authenticationOpened = { },
        openProfile = false,
        profileOpened = { },
        onAuthenticateAndOpenPlan = { },
        openPlan = false,
        onNavigateToDataAccessRationaleActivity = { },
    )
}
