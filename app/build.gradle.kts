plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.sjaindl.travelcompanion"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /*
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
         */
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

/*
configurations.all {
    resolutionStrategy.force("org.antlr:antlr4-runtime:4.5.3")
    resolutionStrategy.force("org.antlr:antlr4-tool:4.5.3")
}
 */

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


    // https://github.com/tony19/logback-android
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("com.github.tony19:logback-android:$logbackVersion")

    // https://github.com/Kotlin/kotlinx-datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    // https://github.com/square/picasso
    implementation ("com.squareup.picasso:picasso:$picassoVersion")
}
