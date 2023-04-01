buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    apply(from = "versions.gradle.kts")
    val gradleVersion: String by extra
    val kotlinVersion: String by extra
    val navigationVersion: String by extra
    val sqlDelightVersion: String by extra
    val resourcesGeneratorVersion: String by extra
    val crashlyticsVersion: String by extra
    val googleServicesVersion: String by extra
    val gradleSecretsVersion: String by extra

    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$crashlyticsVersion")

        // https://github.com/cashapp/sqldelight
        classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")

        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")

        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:$gradleSecretsVersion")

        // https://github.com/icerockdev/moko-resources
        classpath("dev.icerock.moko:resources-generator:$resourcesGeneratorVersion")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
