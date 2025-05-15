@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinter) apply true
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.serialization) apply false
}
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            if (project.findProperty("composeCompilerReports") == "true") {
                freeCompilerArgs +=
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" +
                            "${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                    )
            }
            if (project.findProperty("composeCompilerMetrics") == "true") {
                freeCompilerArgs +=
                    listOf(
                        "-P",
                        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" +
                            "${project.layout.buildDirectory.asFile.get().absolutePath}/compose_compiler"
                    )
            }
        }
    }
}

true
