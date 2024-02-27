plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.john.smartalert"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.john.smartalert"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation ("androidx.camera:camera-core:1.4.0-alpha04")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha04")
    // If you want to additionally use the CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha04")
    // If you want to additionally use the CameraX VideoCapture library
    implementation ("androidx.camera:camera-video:1.4.0-alpha04")
    // If you want to additionally use the CameraX View class
    implementation ("androidx.camera:camera-view:1.4.0-alpha04")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation ("androidx.camera:camera-mlkit-vision:1.4.0-alpha04")
    // If you want to additionally use the CameraX Extensions library
    implementation ("androidx.camera:camera-extensions:1.4.0-alpha04")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-tasks:18.1.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}