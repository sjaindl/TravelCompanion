package com.sjaindl.travelcompanion.benchmark

import android.Manifest
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is a startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.sjaindl.travelcompanion",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()

        val explore: UiObject2 = device.findObject(By.text("Explore"))
        explore.clickAndWait(Until.newWindow(), 3000)
    }
}
