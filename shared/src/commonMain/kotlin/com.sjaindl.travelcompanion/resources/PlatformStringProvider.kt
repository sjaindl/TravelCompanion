package com.sjaindl.travelcompanion.resources

import dev.icerock.moko.resources.desc.StringDesc

expect class PlatformStringProvider {
    fun platformString(desc: StringDesc): String
}
