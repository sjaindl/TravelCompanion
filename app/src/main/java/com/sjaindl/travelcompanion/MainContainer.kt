package com.sjaindl.travelcompanion

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sjaindl.travelcompanion.navigation.TCNavHost
import com.sjaindl.travelcompanion.navigation.TC_HOME_ROUTE

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
) {
    val navController = rememberNavController()

    TCNavHost(
        navController = navController,
        modifier = Modifier
            .fillMaxSize(),
        signInWithGoogle = {
            signInWithGoogle()
            navController.popBackStack(route = TC_HOME_ROUTE, inclusive = false)
        },
        signInWithFacebook = {
            signInWithFacebook()
            navController.popBackStack(route = TC_HOME_ROUTE, inclusive = false)
        },
        signInWithMail = { email, password ->
            signInWithMail(email, password)
            navController.popBackStack(route = TC_HOME_ROUTE, inclusive = false)
        },
        signUpWithMail = { email, password, name ->
            signUpWithMail(email, password, name)
            navController.popBackStack(route = TC_HOME_ROUTE, inclusive = false)
        },
        onClickedProfile = onClickedProfile,
        openAuthentication = openAuthentication,
        authenticationOpened = authenticationOpened,
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

    deeplinkIntent?.let {
        navController.handleDeepLink(intent = it)
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
    )
}
