package com.sjaindl.travelcompanion.extensions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.net.toUri
import timber.log.Timber

fun Context.isGoogleEarthInstalled(): Boolean {
    return try {
        packageManager.getPackageInfo("com.google.earth", 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        Timber.e(e.localizedMessage ?: e.toString())
        false
    }
}

fun Context.launchInGoogleEarth(latitude: Double, longitude: Double) {
    val uri = "geo:$latitude,$longitude".toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.earth")
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "Google Earth not installed", Toast.LENGTH_SHORT).show()
    }
}
