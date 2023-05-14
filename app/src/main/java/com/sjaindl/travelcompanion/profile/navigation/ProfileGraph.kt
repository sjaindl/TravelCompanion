package com.sjaindl.travelcompanion.profile.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sjaindl.travelcompanion.navigation.DestinationItem
import com.sjaindl.travelcompanion.profile.PersonalInfoScreen
import com.sjaindl.travelcompanion.profile.ProfileScreen

private const val profileRoute = "profile"
private const val personalInfoRoute = "personalInfo"


const val profileNavigation = "profileNavigation"

private val profileHome by lazy {
    ProfileHome()
}

private val personalInfo by lazy {
    PersonalInfo()
}

data class ProfileHome(
    override var route: String = profileRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

data class PersonalInfo(
    override var route: String = personalInfoRoute,
    override var arguments: List<NamedNavArgument> = emptyList(),
    override var routeWithArgs: String = route,
) : DestinationItem {
    override fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}

fun NavGraphBuilder.profileGraph(navController: NavController, onClose: () -> Unit = { }) {
    navigation(startDestination = profileHome.route, route = profileNavigation) {
        composable(
            route = profileHome.route,
            arguments = emptyList(),
        ) {
            ProfileScreen(
                onClose = onClose,
                goToPersonalInfo = {
                    navController.navigate(personalInfo.route)
                })
        }

        composable(
            route = personalInfo.route,
            arguments = emptyList(),
        ) {
            PersonalInfoScreen()
        }
    }
}
