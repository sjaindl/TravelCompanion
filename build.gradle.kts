buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)

        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)

        // https://github.com/cashapp/sqldelight
        classpath(libs.gradle.plugin)

        classpath(libs.kotlin.allopen)

        classpath(libs.secrets.gradle.plugin)

        // https://github.com/icerockdev/moko-resources
        classpath(libs.resources.generator)
        classpath(libs.baseline.profile)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
