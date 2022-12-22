val kotlin_version: String by extra
plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.21"
    id("com.squareup.sqldelight")

    // https://medium.com/21buttons-tech/mocking-kotlin-classes-with-mockito-the-fast-way-631824edd5ba
    id("kotlin-allopen")

    //kotlin("native.cocoapods")
    //id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

apply(from = "../versions.gradle.kts")
val ktorVersion: String by extra
val ktorSerializationVersion: String by extra
val coroutineVersion: String by extra
val gsonVersion: String by extra
val timberVersion: String by extra
val sqlDelightVersion: String by extra
val kotlinxDatetimeVersion: String by extra
val kotlinxSerializationVersion: String by extra
val jUnitVersion: String by extra
val firebaseVersion: String by extra

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
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-auth:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                // https://github.com/google/gson
                implementation("com.google.code.gson:gson:$gsonVersion")

                // https://github.com/JakeWharton/timber
                implementation("com.jakewharton.timber:timber:$timberVersion")

                // https://github.com/cashapp/sqldelight
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")

                // https://github.com/Kotlin/kotlinx-datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

                // https://github.com/Kotlin/kotlinx.serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

                // https://github.com/GitLiveApp/firebase-kotlin-sdk
                implementation("dev.gitlive:firebase-auth:$firebaseVersion")
                implementation("dev.gitlive:firebase-config:$firebaseVersion")
                implementation("dev.gitlive:firebase-firestore:$firebaseVersion")

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

                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:$jUnitVersion")
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
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }

        val iosTest by creating {
            dependsOn(commonTest)
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    namespace = "com.sjaindl.travelcompanion"
}

allOpen {
    annotation("com.sjaindl.travelcompanion.util.Mockable")
}
