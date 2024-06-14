package com.sjaindl.travelcompanion.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
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
@OptIn(ExperimentalMetricApi::class)
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    companion object {
        const val PACKAGE_NAME = "com.sjaindl.travelcompanion"
    }

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.DEFAULT)

    private fun startup(
        compilationMode: CompilationMode,
        iterations: Int = 10,
        startupMode: StartupMode = StartupMode.COLD,
    ) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(
            StartupTimingMetric(),

            TraceSectionMetric(
                sectionName = "JIT Compiling %",
                mode = TraceSectionMetric.Mode.Sum,
            ),
            TraceSectionMetric(sectionName = "UserIconContainer"),
            TraceSectionMetric(sectionName = "LoadProfileBitmap"),

            //PowerMetric(PowerMetric.Power(mapOf(PowerCategory.CPU to PowerCategoryDisplayLevel.TOTAL))),
            //PowerMetric(PowerMetric.Battery()),
            //PowerMetric(PowerMetric.Energy()),
        ),
        compilationMode = compilationMode,
        iterations = iterations,
        startupMode = startupMode,
    ) {
        pressHome()
        startActivityAndWait()

        device.wait(Until.hasObject(By.res("fullyDrawn")), 10_000)
    }
}
