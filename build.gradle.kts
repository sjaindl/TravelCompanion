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

    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        //classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")

        // https://github.com/cashapp/sqldelight
        // classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
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
