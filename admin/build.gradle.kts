import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

val env = (project.findProperty("env") as? String) ?: "debug"

val baseUrl = when (env) {
    "release" -> localProps.getProperty("BASE_URL_PROD", "https://apis.car-ing.kr")
    "stage"   -> localProps.getProperty("BASE_URL_STAGE", "https://caring-web-flatform-stage.onrender.com")
    else      -> localProps.getProperty("BASE_URL_DEV", "http://localhost:8080")
}

// Generate actual BuildConfig for jvmMain
val generateBuildConfigJvm by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildConfig/jvm")
    outputs.dir(outputDir)
    inputs.property("env", env)
    inputs.property("baseUrl", baseUrl)
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        File(dir, "BuildConfig.kt").writeText("""
            package incar.mobile.caring.admin
            actual object BuildConfig {
                actual val ENV: String get() = "$env"
                actual val BASE_URL: String get() = "$baseUrl"
                actual val APP_VERSION: String get() = "1.0"
            }
        """.trimIndent())
    }
}

// Generate actual BuildConfig for wasmJsMain
val generateBuildConfigWasm by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildConfig/wasm")
    outputs.dir(outputDir)
    inputs.property("env", env)
    inputs.property("baseUrl", baseUrl)
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        File(dir, "BuildConfig.kt").writeText("""
            package incar.mobile.caring.admin
            actual object BuildConfig {
                actual val ENV: String get() = "$env"
                actual val BASE_URL: String get() = "$baseUrl"
                actual val APP_VERSION: String get() = "1.0"
            }
        """.trimIndent())
    }
}

kotlin {
    jvm()
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        jvmMain {
            kotlin.srcDir(generateBuildConfigJvm.map { layout.buildDirectory.dir("generated/buildConfig/jvm") })
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
        wasmJsMain {
            kotlin.srcDir(generateBuildConfigWasm.map { layout.buildDirectory.dir("generated/buildConfig/wasm") })
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
    }
}
