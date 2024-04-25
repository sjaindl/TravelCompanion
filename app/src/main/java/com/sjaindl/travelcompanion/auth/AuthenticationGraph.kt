package com.sjaindl.travelcompanion.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem

private const val MAIL_ARG = "mail"

private object SignInChooser : DestinationItem {
    override var route = "signInChooser"
}

private object MailSignInHome : DestinationItem {
    override var route = "mailSignInHome"
}

private fun NavController.navigateToMailSignInHome(navOptions: NavOptions? = null) {
    this.navigate(route = MailSignInHome.route, navOptions = navOptions)
}

private object MailSignIn : DestinationItem {
    override var route = "mailSignIn"
    override var arguments = listOf(navArgument(MAIL_ARG) {
        type = NavType.StringType
    })
    override var routeWithArgs: String = "$route/{$MAIL_ARG}"
}

private fun NavController.navigateToMailSignIn(email: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${MailSignIn.route}/$email", navOptions = navOptions)
}

private object MailSignUp : DestinationItem {
    override var route = "mailSignUp"
    override var arguments = listOf(navArgument(MAIL_ARG) {
        type = NavType.StringType
    })
    override var routeWithArgs: String = "$route/{$MAIL_ARG}"
}

private fun NavController.navigateToMailSignUp(email: String, navOptions: NavOptions? = null) {
    this.navigate(route = "${MailSignUp.route}/$email", navOptions = navOptions)
}

const val AUTHENTICATION_NAVIGATION = "authenticationHome"

fun NavGraphBuilder.authenticationGraph(
    navController: NavController,
    signInWithGoogle: () -> Unit,
    signInWithFacebook: () -> Unit,
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
) {
    navigation(startDestination = SignInChooser.route, route = AUTHENTICATION_NAVIGATION) {
        composable(
            route = SignInChooser.route,
        ) {
            SignInChooserScreen(
                signInWithGoogle = signInWithGoogle,
                signInWithFacebook = signInWithFacebook,
                signInWithMail = {
                    navController.navigateToMailSignInHome()
                },
            )
        }

        composable(
            route = MailSignInHome.route,
        ) {
            SignInWithMailHomeScreen(
                signIn = { email ->
                    navController.navigateToMailSignIn(email = email)
                },
                signUp = { email ->
                    navController.navigateToMailSignUp(email = email)
                },
            )
        }

        composable(
            route = MailSignIn.routeWithArgs,
            arguments = MailSignIn.arguments,
        ) { navBackStackEntry ->
            val email = navBackStackEntry.arguments?.getString(MAIL_ARG).orEmpty()

            SignInWithMailScreen(email) { _, password ->
                signInWithMail(email, password)
            }
        }

        composable(
            route = MailSignUp.routeWithArgs,
            arguments = MailSignUp.arguments,
        ) { navBackStackEntry ->
            val email = navBackStackEntry.arguments?.getString(MAIL_ARG).orEmpty()

            SignUpWithMailScreen(email) { _, password, name ->
                signUpWithMail(email, password, name)
            }
        }
    }
}
