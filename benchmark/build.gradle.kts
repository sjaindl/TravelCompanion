@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    // id("androidx.baselineprofile")
}

android {
    namespace = "com.sjaindl.travelcompanion.benchmark"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,DEBUGGABLE,UNLOCKED"

        testInstrumentationRunnerArguments["androidx.benchmark.fullTracing.enable"] = "true"
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = false
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    namespace = "com.sjaindl.travelcompanion.benchmark"
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.uiautomator)
    implementation(libs.benchmark.macro.junit4)
    implementation(libs.androidx.rules)

    implementation(libs.androidx.tracing.perfetto)
    implementation(libs.androidx.tracing.perfetto.binary)

    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.rules)
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}

// This is the plugin configuration. Everything is optional. Defaults are in the
// comments. In this example, you use the GMD added earlier and disable
// connected devices.
/*
BASELINEPROFILE {

    // THIS SPECIFIES THE MANAGED DEVICES TO USE THAT YOU RUN THE TESTS ON. THE
    // DEFAULT IS NONE.
    MANAGEDDEVICES += "PIXEL6API31"

    // THIS ENABLES USING CONNECTED DEVICES TO GENERATE PROFILES. THE DEFAULT IS
    // TRUE. WHEN USING CONNECTED DEVICES, THEY MUST BE ROOTED OR API 33 AND
    // HIGHER.
    USECONNECTEDDEVICES = FALSE

    SAVEINSRC = TRUE
}
 */
