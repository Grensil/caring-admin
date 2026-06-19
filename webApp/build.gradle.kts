import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "caring-admin-web.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    port = 3000
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        wasmJsMain {
            dependencies {
                implementation(projects.admin)
                implementation(compose.runtime)
                implementation(compose.ui)
            }
        }
    }
}
