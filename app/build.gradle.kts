import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")

    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    id("androidx.baselineprofile")

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.sjaindl.travelcompanion"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleMapsApiKey: String = gradleLocalProperties(rootDir).getProperty("googleMapsApiKey")
        manifestPlaceholders["googleMapsApiKey"] = googleMapsApiKey

        val facebookClientToken: String = gradleLocalProperties(rootDir).getProperty("facebookClientToken")
        manifestPlaceholders["facebookClientToken"] = facebookClientToken

        resourceConfigurations.addAll(listOf("en", "de"))
    }

    buildTypes {
        getByName("release") {

            isMinifyEnabled = false
            //proguardFiles = getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            isProfileable = true
        }
    }

    dataBinding {
        enable = true
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    namespace = "com.sjaindl.travelcompanion"
}

dependencies {
    // implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation(project(":shared"))

    implementation(libs.kotlin.reflect)

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // https://developer.android.com/jetpack/androidx/releases/core
    implementation(libs.core.ktx)

    // https://developer.android.com/jetpack/androidx/releases/room
    implementation(libs.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.profileinstaller)
    "baselineProfile"(project(mapOf("path" to ":baselineprofile")))
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation(libs.androidx.appcompat)

    // https://material.io/develop/android/docs/getting-started/
    implementation(libs.material)

    // https://developer.android.com/jetpack/androidx/releases/navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //https://developer.android.com/jetpack/androidx/releases/constraintlayout
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.constraintlayoutCompose)

    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    // activity, fragment
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.androidx.core.testing)

    // https://developer.android.com/jetpack/androidx/releases/recyclerview
    implementation(libs.androidx.recyclerview)

    implementation(libs.splashScreen)

    // https://github.com/square/retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // https://github.com/google/gson
    implementation(libs.gson)

    //https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation(libs.logging.interceptor)

    // https://github.com/JakeWharton/timber
    implementation(libs.timber)

    // https://github.com/Kotlin/kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.properties)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // https://github.com/square/leakcanary
    debugImplementation(libs.leakcanary.android)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.kotlinx.coroutines.test) {
        // conflicts with mockito due to direct inclusion of byte buddy
        // exclude group : "org.jetbrains.kotlinx", module: "kotlinx-coroutines-debug"
    }

    androidTestImplementation(libs.support.annotations)
    androidTestImplementation(libs.runner)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // https://github.com/Kotlin/kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // https://github.com/square/picasso
    implementation(libs.picasso)

    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)
    implementation(libs.maps.compose)

    implementation(libs.places)

    // Jetpack Compose:
    val composeBom = platform("androidx.compose:compose-bom:2022.12.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    // Android Studio Preview support

    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation(libs.androidx.compose.ui)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.webview)

    implementation(libs.androidx.paging.runtime)
    // without Android dependencies for tests
    testImplementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)

    implementation(libs.androidx.datastore.preferences)

    // FirebaseUI for Cloud FireStore
    implementation(libs.firebase.ui.firestore)

    // FirebaseUI for Cloud Storage
    implementation(libs.firebase.ui.storage)

    // FirebaseUI for Firebase Auth
    implementation(libs.firebase.ui.auth)
    // Required for Facebook login: https://github.com/facebook/facebook-android-sdk/blob/master/CHANGELOG.md
    implementation(libs.facebook.login)
}
