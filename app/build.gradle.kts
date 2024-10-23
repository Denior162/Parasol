plugins {
    alias(libs.plugins.android.application) // Android application plugin
    alias(libs.plugins.jetbrains.kotlin.android) // Kotlin Android plugin
    alias(libs.plugins.compose.compiler) // Jetpack Compose compiler plugin
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10" // Kotlin serialization plugin
    id("com.google.devtools.ksp") version "2.0.20-1.0.24" // Kotlin Symbol Processing (KSP) plugin
    id("kotlin-kapt") // Kotlin Annotation Processing Tool (KAPT)
    id("com.google.dagger.hilt.android") // Dagger Hilt for dependency injection
}

android {
    namespace = "com.example.parasol"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.parasol"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX Libraries
    implementation(libs.androidx.core.ktx) // Core KTX extensions
    implementation(libs.androidx.activity.compose) // Jetpack Compose support for Activity
    implementation(platform(libs.androidx.compose.bom)) // BOM for Compose libraries
    implementation(libs.androidx.ui) // Core UI components for Jetpack Compose
    implementation(libs.androidx.ui.graphics) // Graphics components for Jetpack Compose UI
    implementation(libs.androidx.ui.tooling.preview) // Preview tooling for Jetpack Compose UI
    implementation(libs.androidx.material3) // Material Design 3 components
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle extensions for Kotlin
    implementation(libs.androidx.navigation.runtime.ktx) // Navigation component with Kotlin support
    implementation(libs.androidx.navigation.compose) // Navigation for Jetpack Compose
    implementation(libs.material3)

    // Retrofit for REST API interaction
    implementation(libs.retrofit) // Core Retrofit library
    implementation(libs.retrofit2.converter.gson) // Gson converter for Retrofit
    implementation(libs.retrofit2.kotlinx.serialization.converter) // Converter for Kotlinx Serialization
    implementation(libs.converter.gson) // Alternative way to add Gson converter (if needed)
    implementation(libs.kotlinx.serialization.json)

    // Ktor for asynchronous HTTP client operations
    implementation(libs.ktor.client.core) // Core components of Ktor client
    implementation(libs.ktor.client.json) // JSON support in Ktor client
    implementation(libs.ktor.client.content.negotiation) // Content negotiation support in Ktor client
    implementation(libs.ktor.client.serialization) // Optional JSON serialization support in Ktor
    implementation(libs.ktor.client.okhttp) // OkHttp support for Ktor client
    implementation(libs.ktor.serialization.kotlinx.json) // Kotlinx Serialization support in Ktor client

    // Room for local database management
    implementation(libs.androidx.room.runtime)
    implementation(libs.play.services.location) // Core Room library for database operations
    ksp(libs.androidx.room.compiler) // Room annotation processor (KSP)
    implementation(libs.androidx.room.ktx) // Kotlin extensions for Room

    // Datastore for storing preferences and settings
    implementation(libs.androidx.datastore.preferences) // Preferences DataStore

    // Dependency Injection with Hilt
    implementation(libs.hilt.android) // Hilt Android dependency injection library
    kapt(libs.hilt.android.compiler) // Hilt compiler for annotation processing
    implementation(libs.androidx.hilt.navigation.compose)

    // Testing dependencies
    testImplementation(libs.junit) // JUnit for unit testing
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM for Compose in tests
    androidTestImplementation(libs.androidx.junit) // JUnit for Android instrumentation tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
    androidTestImplementation(libs.androidx.ui.test.junit4) // UI testing tools for Jetpack Compose

    debugImplementation(libs.androidx.ui.tooling) // Debugging tools for Jetpack Compose UI components
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest file for UI testing

    implementation(kotlin("script-runtime"))  // Dependency to work with Kotlin scripts
}

