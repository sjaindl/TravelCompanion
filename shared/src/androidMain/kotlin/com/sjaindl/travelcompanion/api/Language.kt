package com.sjaindl.travelcompanion.api

import java.util.*

actual fun languageTag(): String {
    return Locale.getDefault().toLanguageTag()
}
