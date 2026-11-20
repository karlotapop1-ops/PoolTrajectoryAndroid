plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.pooltrajectory"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pooltrajectory"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1-prototype"
    }

    kotlinOptions { jvmTarget = "17" }
}
