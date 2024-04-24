package com.sjaindl.travelcompanion.util

import androidx.navigation.NavHostController

fun NavHostController.navigateSingleTopTo(route: String, popToRoute: String? = graph.startDestinationRoute) =
    navigate(route = route) {
        popToRoute?.let {
            popUpTo(route = it) {
                saveState = true
                inclusive = false
            }
        }
        launchSingleTop = true
        restoreState = true
    }
