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

    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")

    // https://developer.android.com/jetpack/androidx/releases/core
    implementation("androidx.core:core-ktx:$coreVersion")

    // https://developer.android.com/jetpack/androidx/releases/room
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation("androidx.appcompat:appcompat:$appcompatVersion")

    // https://material.io/develop/android/docs/getting-started/
    implementation("com.google.android.material:material:$materialVersion")

    // https://developer.android.com/jetpack/androidx/releases/navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    //https://developer.android.com/jetpack/androidx/releases/constraintlayout
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")

    // https://developer.android.com/jetpack/androidx/releases/lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleExtensionsVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    // activity, fragment
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    testImplementation("androidx.arch.core:core-testing:$coreTestingVersion")

    // https://developer.android.com/jetpack/androidx/releases/recyclerview
    implementation("androidx.recyclerview:recyclerview:$recyclerViewVersion")

    // https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // https://github.com/google/gson
    implementation("com.google.code.gson:gson:$gsonVersion")

    //https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // https://github.com/JakeWharton/timber
    implementation("com.jakewharton.timber:timber:$timberVersion")

    // https://github.com/Kotlin/kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:$kotlinxSerializationVersion")

    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-svg:$coilVersion")

    // https://github.com/square/leakcanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion")

    testImplementation("junit:junit:$jUnitVersion")

    androidTestImplementation("androidx.test:runner:$testRunnerVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.arch.core:core-testing:$coreTestingVersion")
    androidTestImplementation("org.mockito:mockito-core:$mockitoVersion")
    androidTestImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")
    androidTestImplementation("org.mockito:mockito-android:2.24.5")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion") {
        // conflicts with mockito due to direct inclusion of byte buddy
        // exclude group : "org.jetbrains.kotlinx", module: "kotlinx-coroutines-debug"
    }

    androidTestImplementation("com.android.support:support-annotations:28.0.0")
    androidTestImplementation("com.android.support.test:runner:1.0.2")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // https://github.com/tony19/logback-android
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("com.github.tony19:logback-android:$logbackVersion")

    // https://github.com/Kotlin/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // https://github.com/square/picasso
    implementation("com.squareup.picasso:picasso:$picassoVersion")

    implementation("com.google.android.gms:play-services-maps:$mapsVersion")
    implementation("com.google.maps.android:android-maps-utils:$googleMapsUtilsVersion")

    implementation("com.google.android.libraries.places:places:3.0.0")

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
    implementation("androidx.activity:activity-compose:1.6.1")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation("androidx.navigation:navigation-compose:$composeNavigationVersion")

    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-webview:$accompanistVersion")

    implementation("androidx.paging:paging-runtime:$pagingVersion")
    // without Android dependencies for tests
    testImplementation("androidx.paging:paging-common:$pagingVersion")
    implementation("androidx.paging:paging-compose:$pagingComposeVersion")

    implementation("androidx.datastore:datastore-preferences:$datastoreVersion")

    // FirebaseUI for Cloud Firestore
    implementation("com.firebaseui:firebase-ui-firestore:$firebaseUIVersion")

    // FirebaseUI for Cloud Storage
    implementation("com.firebaseui:firebase-ui-storage:$firebaseUIVersion")

    // FirebaseUI for Firebase Auth
    implementation("com.firebaseui:firebase-ui-auth:$firebaseUIVersion")
    // Required for Facebook login: https://github.com/facebook/facebook-android-sdk/blob/master/CHANGELOG.md
    implementation("com.facebook.android:facebook-login:$facebookVersion")
}
