plugins {
    id("com.android.library")
    kotlin("multiplatform")

    alias(libs.plugins.kotlin.serialization)
    id("com.squareup.sqldelight")

    id("dev.icerock.mobile.multiplatform-resources")
    // id("dev.icerock.mobile.multiplatform.ios-framework")
    // id("com.goncalossilva.resources") version "0.2.5"

    // https://medium.com/21buttons-tech/mocking-kotlin-classes-with-mockito-the-fast-way-631824edd5ba
    id("kotlin-allopen")

    //kotlin("native.cocoapods")
    // id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
}

sqldelight {
    database("TravelCompanionDatabase") {
        packageName = "com.sjaindl.travelcompanion.sqldelight"
    }
}

kotlin {
    androidTarget()

    jvmToolchain(17)

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            export(libs.resources)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)

                // https://github.com/google/gson
                implementation(libs.gson)

                // https://github.com/JakeWharton/timber
                implementation(libs.timber)

                // https://github.com/cashapp/sqldelight
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.sqldelight.runtime)

                // https://github.com/Kotlin/kotlinx-datetime
                implementation(libs.kotlinx.datetime)

                // https://github.com/Kotlin/kotlinx.serialization
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.properties)

                // https://github.com/icerockdev/moko-resources
                implementation(libs.resources)

                // https://github.com/square/okio
                implementation(libs.okio)

                // https://github.com/kosi-libs/Kodein
                implementation(libs.kodein)

                implementation(libs.kermit)

                // https://arkivanov.github.io/Decompose/getting-started/installation/
                //implementation("com.arkivanov.decompose:decompose:0.5.1")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation(libs.okio.fakefilesystem)
                implementation(libs.resources.test)
                implementation(libs.resources.goncalossilva)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.ktor.client.gson)

                implementation(libs.kotlinx.coroutines.android)

                implementation(libs.android.driver)
                implementation(libs.resources.compose)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
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
                implementation(libs.ktor.client.ios)
                implementation(libs.sqldelight.native.driver)
                implementation(libs.resources)
            }
        }

        val iosTest by creating {
            dependsOn(commonTest)
        }
    }
}

android {
    compileSdk = 35
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 26
    }
    namespace = "com.sjaindl.travelcompanion.shared"
}

dependencies {
    commonMainApi(libs.resources)
}

allOpen {
    annotation("com.sjaindl.travelcompanion.util.Mockable")
}

// https://github.com/icerockdev/moko-resources
multiplatformResources {
    this.resourcesPackage = "com.sjaindl.travelcompanion"
}
