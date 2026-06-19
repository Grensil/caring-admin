import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(projects.shared)
            implementation(compose.desktop.currentOs)
            implementation(compose.material3)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.navigation.compose.cmp)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
    }
}

compose.desktop {
    application {
        mainClass = "incar.mobile.caring.admin.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "caring-admin"
            packageVersion = "1.0.0"
            macOS {
                bundleID = "incar.mobile.caring.admin"
                packageName = "Caring Admin"
            }
            windows {
                packageName = "Caring Admin"
            }
        }
    }
}
