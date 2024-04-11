import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
// 위치는 여기입니다

android {
    namespace = "com.example.spda_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.spda_app"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    viewBinding { enable = true }
    dataBinding { enable = true }
}

dependencies {

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.3")
    implementation ("com.github.pedroSG94.RootEncoder:library:2.3.5")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
