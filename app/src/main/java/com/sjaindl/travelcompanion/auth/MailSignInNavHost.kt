package com.sjaindl.travelcompanion.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjaindl.travelcompanion.auth.navigation.mailArg
import com.sjaindl.travelcompanion.auth.navigation.mailSignIn
import com.sjaindl.travelcompanion.auth.navigation.mailSignInHome
import com.sjaindl.travelcompanion.auth.navigation.mailSignInHomeRoute
import com.sjaindl.travelcompanion.auth.navigation.mailSignUp

@Composable
fun MailSignInNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = mailSignInHomeRoute,
    signInWithMail: (email: String, password: String) -> Unit,
    signUpWithMail: (email: String, password: String, name: String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(
            route = mailSignInHome.route,
            arguments = emptyList(),
        ) {
            SignInWithMailHomeScreen(
                signIn = { email ->
                    navController.navigate(mailSignIn.routeWithSetArguments(email))
                },
                signUp = { email ->
                    navController.navigate(mailSignUp.routeWithSetArguments(email))
                },
            )
        }

        composable(
            route = mailSignIn.routeWithArgs,
            arguments = mailSignIn.arguments,
        ) { navBackStackEntry ->
            val email = navBackStackEntry.arguments?.getString(mailArg).orEmpty()

            SignInWithMailScreen(email) { _, password ->
                signInWithMail(email, password)
            }
        }

        composable(
            route = mailSignUp.routeWithArgs,
            arguments = mailSignUp.arguments,
        ) { navBackStackEntry ->
            val email = navBackStackEntry.arguments?.getString(mailArg).orEmpty()

            SignUpWithMailScreen(email) { _, password, name ->
                signUpWithMail(email, password, name)
            }
        }
    }
}
