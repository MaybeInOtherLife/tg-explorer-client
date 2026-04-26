import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    kotlin("plugin.serialization") version "2.3.20"
//    id("com.google.devtools.ksp") version "2.3.6"
//    alias(libs.plugins.androidxRoom)

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm()

    jvmToolchain(21)
    
//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation("com.arkivanov.decompose:decompose:3.5.0")
            implementation("com.arkivanov.decompose:extensions-compose:3.5.0")

            implementation(libs.kotlinx.serialization)

            //Room step1
//            implementation(libs.androidx.room.runtime)
//            implementation(libs.androidx.sqlite.bundled)

            implementation("com.squareup.okhttp3:okhttp:5.3.0")

            implementation(libs.coil.compose)
            implementation(libs.coil.okhttp)

            implementation("io.github.amirroid:jalalidate:1.0.2")

            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

            implementation("com.mikepenz:multiplatform-markdown-renderer:0.40.2")
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.1")

            implementation("androidx.datastore:datastore-preferences:1.1.1")
            implementation("androidx.datastore:datastore-preferences-core:1.1.1")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "io.github.maybeinotherlife.tg_scrapper_client"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.maybeinotherlife.tg_scrapper_client"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        debug {
            applicationIdSuffix = ".debug"
            resValue("string","app_name","TGS Debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
//room {
//    schemaDirectory("$projectDir/schemas")
//}
dependencies {
    debugImplementation(libs.compose.uiTooling)
    listOf(
        "kspAndroid",
        "kspJvm",
//        "kspIosSimulatorArm64",
//        "kspIosX64",
//        "kspIosArm64"
    ).forEach {
//        add(it, libs.androidx.room.compiler)
    }
}

compose.desktop {
    application {
        mainClass = "io.github.maybeinotherlife.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "io.github.maybeinotherlife"
            packageVersion = "1.0.0"
        }
    }
}
