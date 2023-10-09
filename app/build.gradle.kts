

plugins {
    id("com.android.application")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rtvplus"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.rtvplus"
        minSdk = 24
        targetSdk = 33
        versionCode = 12
        versionName = "1.2.7"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }


}

dependencies {

    //implementation(dir, 'libs', include: ['*.jar'])
    //implementation(fileTree("libs"))

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.2")
    //responsive screen size
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    //Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //retrofit, gson
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.0.0-beta4")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    //Image Slider
    implementation ("com.github.smarteist:autoimageslider:1.4.0")
    // shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")


    implementation("com.synnapps:carouselview:0.1.5")

   // implementation project(path: ':carouselview')
    //implementation (project(path, ":carouselview"))

    //implementation project(path: ':carouselview')

    implementation(project(":carouselview"))

    //implementation ("com.github.jama5262:CarouselView:1.2.2")


    // lottie animation
    implementation ("com.airbnb.android:lottie:5.2.0")

    //Google Sign In
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //responsive screen size
    implementation ("com.intuit.sdp:sdp-android:1.1.0")
    implementation ("com.intuit.ssp:ssp-android:1.1.0")
    //Hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")

    //retrofit, gson
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.0.0-beta4")

    //ViewModel , livedata
    val lifecycle_version = "2.5.1"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    //Image Slider
    implementation ("com.github.smarteist:autoimageslider:1.4.0")

    // shimmer
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    implementation("com.synnapps:carouselview:0.1.5")

//    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
//    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1")
//    implementation ("com.google.android.exoplayer:exoplayer-smoothstreaming:r2.4.0")
//    implementation ("com.google.android.exoplayer:exoplayer-core:2.19.1")

    implementation ("androidx.media3:media3-exoplayer:1.1.1")
    implementation ("androidx.media3:media3-exoplayer-dash:1.1.1")
    implementation ("androidx.media3:media3-ui:1.1.1")
    implementation ("androidx.media3:media3-exoplayer-hls:1.1.1")

    
    implementation(project(":carouselview"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}

