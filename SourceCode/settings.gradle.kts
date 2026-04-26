rootProject.name = "TGScrapperClient"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.myket.ir")
    }
}

dependencyResolutionManagement {
    repositories {
       maven("https://maven.myket.ir")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")