plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")

    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.myjournalappfinal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myjournalappfinal"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
       viewBinding=true
    }
}

dependencies {
    implementation(libs.firebase.firestore)
    val nav_version = "2.7.7"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-ai")
    implementation("com.airbnb.android:lottie:6.4.1")
    implementation ("com.google.code.gson:gson:2.11.0")
    // Core Navigation library for Fragments (Kotlin KTX)
    implementation("com.cloudinary:cloudinary-android:2.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    // For loading images from URLs (Cloudinary)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Firebase UI for Firestore (makes RecyclerViews simple)
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    // Navigation UI library for integrating with app bars, drawers, etc. (Kotlin KTX)
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // Navigation UI library for integrating with app bars, drawers, etc. (Kotlin KTX)
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

}