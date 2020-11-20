plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}
val kotlin_version by extra("1.4.10")
android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "com.github.karczews.androidauto.automotive.testapp1"
        minSdkVersion(28)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation("androidx.lifecycle:lifecycle-service:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.core:core-ktx:1.5.0-alpha05")
    implementation("com.google.android.libraries.car:car-app:1.0.0-beta.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.appcompat:appcompat:1.2.0")
}
