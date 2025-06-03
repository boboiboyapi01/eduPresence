plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.edupresence"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.edupresence"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // TensorFlow Lite config
//        aaptOptions {
//            noCompress.add("tflite")
//        }
    }

    buildTypes {
        getByName("release") {
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
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat.v170)
    implementation(libs.material.v1120)
    implementation(libs.androidx.constraintlayout)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // CameraX
//    val cameraxVersion = "1.3.0-beta01"
    implementation(libs.androidx.camera.core.v130beta01)
    implementation(libs.androidx.camera.camera2.v130beta01)
    implementation(libs.androidx.camera.lifecycle.v130beta01)
    implementation(libs.androidx.camera.view.v130beta01)

    // Face Detection
    implementation(libs.face.detection.v1617)
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.2")


    // TensorFlow Lite
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.support.v040)
    implementation(libs.tensorflow.lite.v2161)

    // Location
    implementation(libs.play.services.location)

    // Excel Export
    implementation(libs.poi)
    implementation("org.apache.poi:poi-ooxml:5.2.3") {
        exclude(group = "org.apache.xmlbeans", module = "xmlbeans")
    }
    implementation(libs.xmlbeans)
    implementation(libs.commons.io)

    // Coroutines
    implementation(libs.kotlinx.coroutines.play.services)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core)
}