val kotlin_version: String by extra
plugins {
    id("com.android.library")
    kotlin("multiplatform")
    //kotlin("plugin.serialization") version "1.6.10"
    // id("com.squareup.sqldelight")

    //kotlin("native.cocoapods")
    //id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

version = "1.0"

apply(from = "../versions.gradle.kts")
val ktorVersion: String by extra
val ktorSerializationVersion: String by extra
val coroutineVersion: String by extra
val gsonVersion: String by extra
val timberVersion: String by extra
val sqlDelightVersion: String by extra
val kotlinxDatetimeVersion: String by extra

/*
sqldelight {
    database("TravelCompanionDatabase") {
        packageName = "com.sjaindl.travelcompanion.sqldelight"
    }
}
 */

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")

                // https://github.com/google/gson
                implementation("com.google.code.gson:gson:$gsonVersion")

                // https://github.com/JakeWharton/timber
                implementation("com.jakewharton.timber:timber:$timberVersion")

                // https://github.com/cashapp/sqldelight
                //implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                //implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                // https://github.com/Kotlin/kotlinx-datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

                // https://arkivanov.github.io/Decompose/getting-started/installation/
                //implementation("com.arkivanov.decompose:decompose:0.5.1")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
                implementation("io.ktor:ktor-client-gson:$ktorVersion")

                //implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
                //implementation("io.ktor:ktor-client-serialization-native:$ktorSerializationVersion")

                //implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 33
    }
    namespace = "com.sjaindl.travelcompanion"
}

allprojects {
    configurations.all {
        resolutionStrategy {
            // Fix for Uncaught Kotlin exception: kotlin.Error: Ktor native HttpClient requires kotlinx.coroutines version with `native-mt` suffix (like `1.3.9-native-mt`). Consider checking the dependencies.
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
        }
    }
}