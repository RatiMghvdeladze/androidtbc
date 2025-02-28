plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    id(libs.plugins.safeArgs.get().pluginId)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.protobuf") version "0.9.4"
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.secrets.gradle)
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
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://run.mocky.io/v3/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"https://run.mocky.io/v3/\"")
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
    implementation ("com.google.maps.android:android-maps-utils:2.3.0")

    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(libs.androidx.viewpager2)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.room.runtime)
    implementation (libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    //noinspection GradleDependency
    implementation (libs.androidx.datastore)
    implementation( libs.protobuf.javalite)
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
    ksp(libs.moshi.kotlin.codegen)
    testImplementation(libs.junit)
    implementation(libs.moshi.kotlin)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.0"
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}


buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:1.3.0")
    }
}

kapt {
    correctErrorTypes = true
}