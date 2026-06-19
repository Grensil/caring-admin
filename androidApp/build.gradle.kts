plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

android {
    namespace = "incar.mobile.caring.admin.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "incar.mobile.caring.admin.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    sourceSets {
        androidMain {
            dependencies {
                implementation(projects.admin)
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
            }
        }
    }
}
