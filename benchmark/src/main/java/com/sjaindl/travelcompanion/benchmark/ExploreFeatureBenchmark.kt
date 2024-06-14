package com.sjaindl.travelcompanion.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class ExploreFeatureBenchmark {
    companion object {
        const val PACKAGE_NAME = "com.sjaindl.travelcompanion"
    }

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun goToExplore() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
            FrameTimingMetric(),

            TraceSectionMetric(
                sectionName = "ExploreScreenContent",
                mode = TraceSectionMetric.Mode.Sum
            ),
            TraceSectionMetric(
                sectionName = "GoogleMap",
                mode = TraceSectionMetric.Mode.Sum
            ),
            TraceSectionMetric(sectionName = "onMapLoaded"),
        ),
        compilationMode = CompilationMode.Full(),
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.text("Explore")), 10000)
        }
    ) {
        device.findObject(By.text("Explore")).click()
        device.wait(Until.hasObject(By.text("Search for Place")), 10000)
    }
}
