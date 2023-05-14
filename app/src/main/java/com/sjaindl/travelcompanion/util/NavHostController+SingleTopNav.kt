package com.sjaindl.travelcompanion.util

import androidx.navigation.NavHostController

fun NavHostController.navigateSingleTopTo(route: String, popToRoute: String? = graph.startDestinationRoute) =
    this.navigate(route) {
        popToRoute?.let {
            popUpTo(it) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
