import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()
    sourceSets {
        jvmMain {
            dependencies {
                implementation(projects.admin)
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
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
