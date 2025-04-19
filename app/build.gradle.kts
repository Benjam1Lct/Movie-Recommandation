plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"

}

android {
    namespace = "com.example.applisae"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.applisae"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.transport.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.coil) // Pour charger les images
    implementation("io.ktor:ktor-client-core:2.0.3")
    implementation ("io.ktor:ktor-client-android:2.3.6")
    implementation ("io.ktor:ktor-client-okhttp-jvm:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.0.3")
    implementation ("io.ktor:ktor-client-logging:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation("com.google.android.material:material:1.10.0")


}