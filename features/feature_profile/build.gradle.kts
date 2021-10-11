import com.ferelin.Base
import com.ferelin.Dependencies
import com.ferelin.Projects

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = Base.currentSDK

    defaultConfig {
        minSdk = Base.minSDK
    }
    buildFeatures.apply {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(project(Projects.core))

    implementation(Dependencies.glide)
    kapt(Dependencies.glideCompilerKapt)

    implementation(Dependencies.dagger)
    kapt(Dependencies.daggerCompilerKapt)
}