import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    id(libs.plugins.safeArgs.get().pluginId)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}


android {
    namespace = "com.example.androidtbc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidtbc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"${localProperties["BASE_URL"] ?: "https://api.themoviedb.org/3/"}\"")
    }

    buildTypes {
        debug{
            buildConfigField("String", "API_KEY", "\"${localProperties["MOVIE_DB_API_KEY"] ?: ""}\"")
        }
        release {
            buildConfigField("String", "API_KEY", "\"${localProperties["MOVIE_DB_API_KEY"] ?: ""}\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.firebase.firestore.v2500)

    implementation(libs.logging.interceptor)

    implementation(libs.play.services.auth.v2070)
    implementation(libs.googleid)
    // Google Auth

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.viewpager2)
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth)
    ksp(libs.hilt.android.compiler)


    implementation(libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    //noinspection GradleDependency
    implementation (libs.androidx.datastore)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation (libs.squareup.retrofit)
    implementation(libs.okhttp)
    implementation (libs.retrofit.serialization)
    implementation(libs.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.glide)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}


buildscript {
    repositories {
        google()
    }
}
