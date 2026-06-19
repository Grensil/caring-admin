import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

// 포트 3000이 사용 중이면 프로세스를 종료한다
tasks.register("killPort3000", Exec::class) {
    commandLine("sh", "-c", "lsof -ti:3000 | xargs kill -9 2>/dev/null || true")
}

tasks.matching { it.name == "wasmJsBrowserDevelopmentRun" }.configureEach {
    dependsOn("killPort3000")
}

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
