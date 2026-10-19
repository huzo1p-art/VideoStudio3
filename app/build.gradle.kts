plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.videostudio"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.videostudio"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.media3:media3-transformer:1.2.1")
    implementation("androidx.media3:media3-common:1.2.1")
}
