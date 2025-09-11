plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "com.ariabagas.storiaapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ariabagas.storiaapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //CHECK MANIFEST FOR G-MAP KEY
        buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://story-api.dicoding.dev/v1/\"")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // --- AndroidX Core & Compatibility ---
    implementation(libs.androidx.core.ktx)          // Kotlin extensions for Android core
    implementation(libs.androidx.appcompat)         // Backward compatibility support
    implementation(libs.androidx.constraintlayout)  // Constraint-based layout system
    implementation(libs.androidx.activity)          // Activity APIs & back-press dispatcher
    coreLibraryDesugaring(libs.desugar.jdk.libs)    // Use newer Java APIs on older devices

    // --- UI Components ---
    implementation(libs.recyclerview)               // RecyclerView for list/grid
    implementation(libs.material)                   // Material Design components
    implementation(libs.glide)                      // Image loading & caching
    implementation(libs.androidx.paging.runtime)    // Pagination for RecyclerView
    implementation(libs.androidx.exifinterface)     // Image metadata (EXIF)
    implementation(libs.androidx.camera.camera2)    // CameraX - Camera2 support
    implementation(libs.camera.lifecycle)           // CameraX lifecycle-aware
    implementation(libs.camera.view)                // CameraX preview/view
    implementation(libs.swiperefresh)               // Pull-to-refresh
    implementation(libs.androidx.core.splashscreen) // Splash

    // --- Room (Local Database) ---
    implementation(libs.room.runtime)               // Room runtime
    ksp(libs.room.compiler)                         // Annotation processor (KSP)
    androidTestImplementation(libs.room.testing)    // Room testing utilities
    implementation(libs.androidx.room.ktx)          // Kotlin extensions for Room
    implementation(libs.androidx.room.paging)       // Room Paging

    // --- Networking (Retrofit + Moshi + OkHttp) ---
    implementation(libs.retrofit)                   // Retrofit HTTP client
    implementation(libs.converter.moshi)            // Moshi converter for Retrofit
    implementation(libs.moshi)                      // Core Moshi (JSON parsing)
    implementation(libs.moshi.kotlin)               // Moshi Kotlin adapter
    ksp(libs.moshi.kotlin.codegen)                  // Codegen for Moshi
    implementation(libs.logging.interceptor)        // OkHttp logging interceptor

    // --- Coroutines (Async / Background) ---
    implementation(libs.kotlinx.coroutines.core)    // Core coroutines
    implementation(libs.kotlinx.coroutines.android) // Android-specific coroutines

    // --- Lifecycle / Architecture Components ---
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData & lifecycle extensions

    // --- Dependency Injection ---
    implementation(libs.koin.android)               // Koin for DI

    // --- DataStore (Preferences / Proto) ---
    implementation(libs.androidx.datastore.preferences) // Preferences DataStore

    // --- G Maps ---
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // --- Unit testing (local JVM) ---
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)

    // --- Instrumentation tests (Android device/emulator) ---
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.espresso.idling.resource)
    androidTestImplementation (libs.androidx.espresso.intents)
    androidTestImplementation (libs.androidx.espresso.contrib)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}


