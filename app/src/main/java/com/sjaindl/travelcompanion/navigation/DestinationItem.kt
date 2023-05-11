package com.sjaindl.travelcompanion.navigation

import androidx.navigation.NamedNavArgument

interface DestinationItem {
    var route: String
    var arguments: List<NamedNavArgument>
    val routeWithArgs: String

    fun routeWithSetArguments(vararg arguments: Any): String
}
