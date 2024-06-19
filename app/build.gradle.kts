import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id ("org.jetbrains.kotlin.android")
}


val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
// 위치는 여기입니다

android {
    namespace = "com.example.spda_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spda_app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        defaultConfig {
            // 기본 구성에 빌드 설정 필드 추가
            buildConfigField("String", "rtsp_url", properties.getProperty("rtsp_url"))
            buildConfigField("String", "rtsp_user", properties.getProperty("rtsp_user"))
            buildConfigField("String", "rtsp_pass", properties.getProperty("rtsp_pass"))
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildFeatures.buildConfig = true
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

    viewBinding { enable = true }
    dataBinding { enable = true }
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.compose.ui:ui:1.6.5")

    implementation ("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation ("com.github.pedroSG94.RootEncoder:library:2.3.5")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("androidx.activity:activity:1.8.2")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
    implementation("com.google.mlkit:object-detection-custom:17.0.1")
    implementation("androidx.camera:camera-mlkit-vision:1.4.0-alpha04")
    implementation ("com.google.mlkit:image-labeling-custom:17.0.2")
    implementation ("org.tensorflow:tensorflow-lite:2.15.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.camera:camera-core:1.3.2")
    implementation ("androidx.camera:camera-camera2:1.3.2")
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    implementation ("androidx.camera:camera-video:1.3.2")
    implementation ("com.google.inject:guice")
    implementation ("androidx.camera:camera-view:1.3.2")
    implementation ("androidx.camera:camera-extensions:1.3.2")
}
