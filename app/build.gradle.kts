plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.aotmin"
    // compileSdk/targetSdk 35: present in every current SDK install and the
    // AGP 8.13 default. minSdk 24 keeps the desugaring surface small and
    // stable for R8/D8.
    compileSdk = 35
    // Pinned: stock Google build-tools ship an x86_64-only aapt2, which
    // cannot even start on ARM64 Linux. Use the native aarch64 build-tools
    // from https://github.com/sankarru/fixed (or any 37.0.0) on ARM64.
    // x86_64 machines can use the stock 37.0.0 from sdkmanager as-is.
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "com.example.aotmin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            // Stable AOT: R8 full mode (enabled in gradle.properties) +
            // D8 dexer + resource shrinking. The rules file keeps everything
            // reached via reflection/serialization/JNI boundaries.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        // Core-library desugaring stays OFF on purpose: minSdk 24 already
        // covers the java.time APIs this app uses, and enabling it pulls a
        // second copy of desugared classes through D8.
    }

    kotlin {
        jvmToolchain(21)
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/licenses/**",
                "META-INF/notice.txt",
                "META-INF/NOTICE*",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    // Installs src/main/baselineProfiles/baseline-prof.txt at install time
    // so dexopt AOT-compiles the hot startup path on-device.
    implementation(libs.profileinstaller)
}
