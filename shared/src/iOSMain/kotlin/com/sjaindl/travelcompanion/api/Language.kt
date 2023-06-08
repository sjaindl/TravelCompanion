package com.sjaindl.travelcompanion.api

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun languageTag(): String {
    return NSLocale.currentLocale.languageCode
}
