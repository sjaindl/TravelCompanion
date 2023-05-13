import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
    }

    dataBinding {
        enable = true
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0-alpha02"
    }

    // REMOVES ERROR Cannot inline bytecode built with JVM target 1.8 into bytecode that is being built with JVM target 1.6
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    // Needed for journeyapps.barcodescanner.camera JVM desugaring
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.sjaindl.travelcompanion"
}

//https://proandroiddev.com/dependencies-versions-in-gradle-kotlin-dsl-a8db15cedee2
apply(from = "../versions.gradle.kts")
val kotlinVersion: String by extra
val coroutineVersion: String by extra
val coreVersion: String by extra
val roomVersion: String by extra
val appcompatVersion: String by extra
val materialVersion: String by extra
val navigationVersion: String by extra
val constraintLayoutVersion: String by extra
val lifecycleExtensionsVersion: String by extra
val lifecycleVersion: String by extra
val coreTestingVersion: String by extra
val recyclerViewVersion: String by extra
val retrofitVersion: String by extra
val gsonVersion: String by extra
val okhttpVersion: String by extra
val timberVersion: String by extra
val leakCanaryVersion: String by extra
val coilVersion: String by extra
val jUnitVersion: String by extra
val testRunnerVersion: String by extra
val espressoVersion: String by extra
val mockitoVersion: String by extra
val mockitoKotlinVersion: String by extra
val firebaseBomVersion: String by extra
val slf4jVersion: String by extra
val logbackVersion: String by extra
val kotlinxDatetimeVersion: String by extra
val picassoVersion: String by extra
val mapsVersion: String by extra
val googleMapsUtilsVersion: String by extra
val ktorSerializationVersion: String by extra
val kotlinxSerializationVersion: String by extra
val accompanistVersion: String by extra
val composeNavigationVersion: String by extra
val pagingVersion: String by extra
val pagingComposeVersion: String by extra
val datastoreVersion: String by extra
val firebaseUIVersion: String by extra
val facebookVersion: String by extra

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
    kapt("androidx.room:room-compiler:$roomVersion")
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

    // https://github.com/tony19/logback-android
    implementation(libs.slf4j.api)
    implementation(libs.logback.android)

    // https://github.com/Kotlin/kotlinx-datetime
    implementation(libs.kotlinx.datetime)

    // https://github.com/square/picasso
    implementation(libs.picasso)

    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)

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
