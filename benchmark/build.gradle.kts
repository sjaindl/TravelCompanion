@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
    // id("androidx.baselineprofile")
}

android {
    namespace = "com.sjaindl.travelcompanion.benchmark"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
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
baselineProfile {

    // This specifies the managed devices to use that you run the tests on. The
    // default is none.
    managedDevices += "pixel6Api31"

    // This enables using connected devices to generate profiles. The default is
    // true. When using connected devices, they must be rooted or API 33 and
    // higher.
    useConnectedDevices = false

    saveInSrc = true
}
 */