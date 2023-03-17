buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    apply(from = "versions.gradle.kts")
    val kotlinVersion: String by extra
    val navigationVersion: String by extra
    val sqlDelightVersion: String by extra
    val resourcesGeneratorVersion: String by extra

    dependencies {
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.4")

        // https://github.com/cashapp/sqldelight
        classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")

        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")

        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")

        // https://github.com/icerockdev/moko-resources
        classpath("dev.icerock.moko:resources-generator:$resourcesGeneratorVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
