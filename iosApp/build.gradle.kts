plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    applyDefaultHierarchyTemplate()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "CaringAdminIos"
            isStatic = true
            export(projects.admin)
        }
    }
}

afterEvaluate {
    kotlin.sourceSets.findByName("iosMain")?.dependencies {
        api(projects.admin)
    }
}
