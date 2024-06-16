plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
}

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

        classpath(libs.hilt.gradlePlugin)
    }
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_metrics"
            )

            freeCompilerArgs.addAll(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_metrics"
            )
        }
    }
}

tasks.register(name = "clean", type = Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
