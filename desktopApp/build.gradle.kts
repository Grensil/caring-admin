import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

val env = (project.findProperty("env") as? String) ?: "debug"

val baseUrl = when (env) {
    "release" -> localProps.getProperty("BASE_URL_PROD", "https://apis.car-ing.kr")
    "stage"   -> localProps.getProperty("BASE_URL_STAGE", "https://caring-web-flatform-stage.onrender.com")
    else      -> localProps.getProperty("BASE_URL_DEV", "https://caring-web-flatform.onrender.com")
}

val kicaaBaseUrl = when (env) {
    "release" -> localProps.getProperty("KICAA_BASE_URL_PROD", "https://www.car-ing.kr")
    "stage"   -> localProps.getProperty("KICAA_BASE_URL_STAGE", "https://www.car-ing.kr")
    else      -> localProps.getProperty("KICAA_BASE_URL_DEV", "https://www.car-ing.kr")
}

val iamportImpKey    = localProps.getProperty("IAMPORT_IMP_KEY", "")
val iamportImpSecret = localProps.getProperty("IAMPORT_IMP_SECRET", "")

val generateBuildConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildConfig/kotlin")
    outputs.dir(outputDir)
    inputs.property("env", env)
    inputs.property("baseUrl", baseUrl)
    inputs.property("kicaaBaseUrl", kicaaBaseUrl)
    inputs.property("iamportImpKey", iamportImpKey)
    inputs.property("iamportImpSecret", iamportImpSecret)
    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        File(dir, "BuildConfig.kt").writeText(
            """
            package incar.mobile.caring.admin

            object BuildConfig {
                const val ENV = "$env"
                const val BASE_URL = "$baseUrl"
                const val KICAA_BASE_URL = "$kicaaBaseUrl"
                const val IAMPORT_IMP_KEY = "$iamportImpKey"
                const val IAMPORT_IMP_SECRET = "$iamportImpSecret"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    jvm()

    sourceSets {
        jvmMain {
            kotlin.srcDir(generateBuildConfig.map { layout.buildDirectory.dir("generated/buildConfig/kotlin") })
            dependencies {
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
