package com.sjaindl.travelcompanion.auth.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sjaindl.travelcompanion.navigation.DestinationItem

internal const val mailArg = "mail"

const val mailSignInHomeRoute = "mailSignInHome"
private const val mailSignInRoute = "mailSignIn"
private const val mailSignUpRoute = "mailSignUp"

val mailSignInArgs = listOf(navArgument(mailArg) {
    type = NavType.StringType
    // nullable = true
})

val mailSignUpArgs = listOf(navArgument(mailArg) {
    type = NavType.StringType
    // nullable = true
})

val mailSignInHome by lazy {
    MailSignInHome()
}

val mailSignIn by lazy {
    MailSignIn()
}

val mailSignUp by lazy {
    MailSignUp()
}

data class MailSignInHome(
    override var route: String = mailSignInHomeRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class MailSignIn(
    override var route: String = mailSignInRoute,
    override var arguments: List<NamedNavArgument> = mailSignInArgs,
    override var routeWithArgs: String = "$route/{$mailArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val email = arguments.firstOrNull() as? String ?: return route

        return "$route/$email"
    }
}

data class MailSignUp(
    override var route: String = mailSignUpRoute,
    override var arguments: List<NamedNavArgument> = mailSignUpArgs,
    override var routeWithArgs: String = "$route/{$mailArg}",
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        val email = arguments.firstOrNull() as? String ?: return route

        return "$route/$email"
    }
}
