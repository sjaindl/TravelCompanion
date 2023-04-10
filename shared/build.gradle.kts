plugins {
    id("com.android.library")
    kotlin("multiplatform")

    kotlin("plugin.serialization") version "1.7.21"
    id("com.squareup.sqldelight")

    id("dev.icerock.mobile.multiplatform-resources")
    // id("dev.icerock.mobile.multiplatform.ios-framework")
    // id("com.goncalossilva.resources") version "0.2.5"

    // https://medium.com/21buttons-tech/mocking-kotlin-classes-with-mockito-the-fast-way-631824edd5ba
    id("kotlin-allopen")

    //kotlin("native.cocoapods")
    // id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
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
val resourcesGeneratorVersion: String by extra
val okioVersion: String by extra
val jUnitVersion: String by extra
val kotlinxResources: String by extra
val kodein: String by extra

sqldelight {
    database("TravelCompanionDatabase") {
        packageName = "com.sjaindl.travelcompanion.sqldelight"
    }
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            export("dev.icerock.moko:resources:$resourcesGeneratorVersion")
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

                // https://github.com/icerockdev/moko-resources
                implementation("dev.icerock.moko:resources:$resourcesGeneratorVersion")

                // https://github.com/square/okio
                implementation("com.squareup.okio:okio:$okioVersion")

                // https://github.com/kosi-libs/Kodein
                implementation("org.kodein.di:kodein-di:$kodein")

                // https://arkivanov.github.io/Decompose/getting-started/installation/
                //implementation("com.arkivanov.decompose:decompose:0.5.1")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("com.squareup.okio:okio-fakefilesystem:$okioVersion")
                implementation("dev.icerock.moko:resources-test:$resourcesGeneratorVersion")
                implementation("com.goncalossilva:resources:$kotlinxResources")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
                implementation("io.ktor:ktor-client-gson:$ktorVersion")

                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
                implementation("dev.icerock.moko:resources-compose:$resourcesGeneratorVersion")
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
                implementation("dev.icerock.moko:resources:$resourcesGeneratorVersion")
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

    // Needed for now as workaround for issue: https://github.com/icerockdev/moko-resources/issues/353
    sourceSets.getByName("main").res.srcDir(File(buildDir, "generated/moko/androidMain/res"))

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    namespace = "com.sjaindl.travelcompanion"
}

dependencies {
    commonMainApi("dev.icerock.moko:resources:$resourcesGeneratorVersion")
}


allOpen {
    annotation("com.sjaindl.travelcompanion.util.Mockable")
}

// https://github.com/icerockdev/moko-resources
multiplatformResources {
    multiplatformResourcesPackage = "com.sjaindl.travelcompanion"
    disableStaticFrameworkWarning = true
}
