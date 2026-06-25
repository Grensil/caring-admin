@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import java.io.File
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidLibrary)
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

fun generateBuildConfigContent() = """
    package incar.mobile.caring.admin
    actual object BuildConfig {
        actual val ENV: String get() = "$env"
        actual val BASE_URL: String get() = "$baseUrl"
        actual val APP_VERSION: String get() = "1.0"
    }
""".trimIndent()

fun registerBuildConfigTask(name: String, dirPath: String) = tasks.register(name) {
    val outputDir = layout.buildDirectory.dir(dirPath)
    outputs.dir(outputDir)
    inputs.property("env", env)
    inputs.property("baseUrl", baseUrl)
    doLast {
        val dir = outputDir.get().asFile.also { it.mkdirs() }
        File(dir, "BuildConfig.kt").writeText(generateBuildConfigContent())
    }
}

val generateBuildConfigJvm     = registerBuildConfigTask("generateBuildConfigJvm",     "generated/buildConfig/jvm")
val generateBuildConfigWasm    = registerBuildConfigTask("generateBuildConfigWasm",    "generated/buildConfig/wasm")
val generateBuildConfigAndroid = registerBuildConfigTask("generateBuildConfigAndroid", "generated/buildConfig/android")
val generateBuildConfigIos     = registerBuildConfigTask("generateBuildConfigIos",     "generated/buildConfig/ios")

android {
    namespace = "incar.mobile.caring.admin"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    wasmJs { browser() }
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.compose.runtime:runtime")
                implementation("org.jetbrains.compose.foundation:foundation")
                implementation("org.jetbrains.compose.material3:material3:1.9.0")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
                implementation("org.jetbrains.compose.ui:ui")
                implementation("org.jetbrains.compose.components:components-resources:1.10.2")
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.2")
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
                implementation("org.jetbrains.compose.ui:ui-tooling:1.10.2")
            }
        }
        wasmJsMain {
            kotlin.srcDir(generateBuildConfigWasm.map { layout.buildDirectory.dir("generated/buildConfig/wasm") })
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        androidMain {
            kotlin.srcDir(generateBuildConfigAndroid.map { layout.buildDirectory.dir("generated/buildConfig/android") })
            dependencies {
                implementation(libs.ktor.client.android)
                implementation(libs.koin.android)
            }
        }
    }
}

// Configure iOS source sets after evaluation (iosMain is created lazily by default hierarchy)
afterEvaluate {
    kotlin.sourceSets.findByName("iosMain")?.apply {
        kotlin.srcDir(generateBuildConfigIos.map { layout.buildDirectory.dir("generated/buildConfig/ios") })
        dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
