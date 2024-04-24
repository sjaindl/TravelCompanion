package com.sjaindl.travelcompanion.navigation

import androidx.navigation.NamedNavArgument

interface DestinationItem {
    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val routeWithArgs: String
        get() = ""

    fun routeWithSetArguments(vararg arguments: Any): String {
        return route
    }
}
