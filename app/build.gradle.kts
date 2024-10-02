plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
    id("kotlin-kapt")
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
    // Библиотеки AndroidX
    implementation(libs.androidx.core.ktx) // Расширения для Core
    implementation(libs.androidx.lifecycle.runtime.ktx) // Жизненный цикл для Kotlin
    implementation(libs.androidx.activity.compose) // Поддержка Jetpack Compose для Activity
    implementation(platform(libs.androidx.compose.bom)) // BOM для Compose
    implementation(libs.androidx.ui) // Основные компоненты UI
    implementation(libs.androidx.ui.graphics) // Графические компоненты UI
    implementation(libs.androidx.ui.tooling.preview) // Инструменты для предварительного просмотра UI
    implementation(libs.androidx.material3) // Material Design 3
    implementation(libs.androidx.navigation.runtime.ktx) // Навигация с поддержкой Kotlin
    implementation(libs.androidx.navigation.compose) // Навигация для Jetpack Compose
    implementation(libs.androidx.material.icons.extended)

    // Retrofit для работы с REST API
    implementation(libs.retrofit2.converter.gson) // Конвертер Gson для Retrofit
    implementation(libs.retrofit) // Основная библиотека Retrofit
    implementation(libs.converter.gson) // Альтернативный способ добавления конвертера Gson
    implementation(libs.retrofit2.kotlinx.serialization.converter) // Конвертер для Kotlinx Serialization

    // Ktor для асинхронного HTTP-клиента
    implementation(libs.ktor.client.content.negotiation) // Поддержка контентной Negotiation в Ktor
    implementation(libs.ktor.client.json) // Поддержка JSON в Ktor
    implementation(libs.ktor.serialization.kotlinx.json) // Kotlinx Serialization для Ktor
    implementation(libs.ktor.client.core) // Основные компоненты Ktor клиента
    implementation(libs.ktor.client.serialization) // Опциональная поддержка сериализации JSON в Ktor

    // Room для работы с локальными базами данных
    implementation(libs.androidx.room.runtime) // Основная библиотека Room
    ksp(libs.androidx.room.compiler) // Компилятор аннотаций Room (KSP)
    kapt(libs.hilt.android.compiler) // Компилятор аннотаций Hilt (KAPT)
    implementation(libs.androidx.room.ktx) // Расширения Room для Kotlin

    // Datastore для хранения настроек и предпочтений
    implementation(libs.androidx.datastore.preferences) // Datastore с предпочтениями

    // Зависимости для тестирования
    testImplementation(libs.junit) // JUnit для юнит-тестов
    androidTestImplementation(libs.androidx.junit) // JUnit для инструментальных тестов Android
    androidTestImplementation(libs.androidx.espresso.core) // Espresso для UI-тестирования

    androidTestImplementation(platform(libs.androidx.compose.bom)) // BOM для Compose в тестах
    androidTestImplementation(libs.androidx.ui.test.junit4) // Тестирование UI компонентов Compose

    debugImplementation(libs.androidx.ui.tooling) // Инструменты отладки UI компонентов Compose
    debugImplementation(libs.androidx.ui.test.manifest) // Манифест для тестирования UI компонентов Compose

    implementation(kotlin("script-runtime")) // Зависимость для работы с Kotlin скриптами
}
