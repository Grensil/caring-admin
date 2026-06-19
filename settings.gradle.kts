rootProject.name = "caring-admin"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(":shared:domain")
include(":shared:data")
include(":shared")
include(":admin")
include(":desktopApp")
include(":webApp")
include(":androidApp")
include(":iosApp")
