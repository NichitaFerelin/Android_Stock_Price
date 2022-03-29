import com.ferelin.Deps

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
  id("com.github.ben-manes.versions")
  id("com.google.firebase.crashlytics")
}

android {
  compileSdk = Deps.currentSDK

  defaultConfig {
    applicationId = "com.ferelin.stockprice"
    minSdk = Deps.minSDK
    targetSdk = Deps.currentSDK
    versionCode = 12
    versionName = "4.2.0"
  }

  buildFeatures.apply {
    compose = true
    buildConfig = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  composeOptions {
    kotlinCompilerExtensionVersion = Deps.composeVersion
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"true\")")
    }

    debug {
      buildConfigField("boolean", "RELEASE", "Boolean.parseBoolean(\"false\")")
    }
  }
}

dependencies {
  implementation(project(":core"))
  implementation(project(":core:ui"))
  implementation(project(":core:domain"))
  implementation(project(":core:data"))
  implementation(project(":features:about"))
  implementation(project(":features:authentication"))
  implementation(project(":features:search"))
  implementation(project(":features:settings"))
  implementation(project(":features:splash"))
  implementation(project(":features:stocks"))

  implementation(platform(Deps.firebasePlatform))
  implementation(Deps.firebaseCrashlyticsKtx)

  implementation(Deps.dagger)
  kapt(Deps.daggerCompilerKapt)
}