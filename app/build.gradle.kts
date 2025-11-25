plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mpt_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mpt_app"
        minSdk = 29
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
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth") // Dodaj Firebase Auth
    implementation("com.google.firebase:firebase-firestore") // Dodaj Firestore

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("com.google.android.material:material:1.6.0")

    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")

    implementation("androidx.core:core:1.9.0")

    implementation("com.google.android.material:material:1.8.0") // Upewnij się, że wersja jest aktualna


    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("androidx.work:work-runtime:2.8.0") // Sprawdź najnowszą wersję

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

//    implementation("com.prolificinteractive:material-calendarview:1.4.3")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
