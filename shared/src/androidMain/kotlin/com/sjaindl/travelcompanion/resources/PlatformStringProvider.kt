package com.sjaindl.travelcompanion.resources

import android.content.Context
import dev.icerock.moko.resources.desc.StringDesc

actual class PlatformStringProvider(private val context: Context) {
    actual fun platformString(desc: StringDesc): String {
        return desc.toString(context)
    }
}
