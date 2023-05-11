package com.sjaindl.travelcompanion.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface NamedDestinationItem : DestinationItem {
    var titleRes: Int
    var icon: ImageVector
}
