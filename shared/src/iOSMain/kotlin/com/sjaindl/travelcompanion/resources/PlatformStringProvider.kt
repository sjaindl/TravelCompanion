package com.sjaindl.travelcompanion.resources

import dev.icerock.moko.resources.desc.StringDesc

actual class PlatformStringProvider {
    actual fun platformString(desc: StringDesc): String {
        return desc.localized()
    }
}
