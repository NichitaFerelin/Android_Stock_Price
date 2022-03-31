package com.ferelin

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Libs {


  const val androidCore = "androidx.core:core-ktx:1.7.0"
  const val timber = "com.jakewharton.timber:timber:5.0.1"
  const val browser = "androidx.browser:browser:1.4.0"
  const val documentFile = "androidx.documentfile:documentfile:1.0.1"
  const val material = "com.google.android.material:material:1.5.0"
  const val dataStorePreferences = "androidx.datastore:datastore-preferences:1.0.0"

  object Project {
    const val minSDK = 21
    const val currentSDK = 31
    const val codeVersion = 12
    const val codeVersionName = "4.2.0"
  }

  object Plugins {
    const val gradle = "com.android.tools.build:gradle:7.1.2"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
    const val google = "com.google.gms:google-services:4.3.10"
    const val firebase = "com.google.firebase:firebase-crashlytics-gradle:2.8.1"
  }

  object Kotlin {
    const val version = "1.6.10"
    const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
  }

  object Coroutines {
    const val version = "1.6.0"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
  }

  object Compose {
    const val version = "1.1.1"
    const val ui = "androidx.compose.ui:ui:$version"
    const val util = "androidx.compose.ui:ui-util:$version"
    const val material = "androidx.compose.material:material:$version"
    const val materialIcons = "androidx.compose.material:material-icons-core:$version"
    const val tooling = "androidx.compose.ui:ui-tooling:$version"
    const val runtime = "androidx.compose.runtime:runtime:$version"
    const val activity = "androidx.activity:activity-compose:$version"
    const val animations = "androidx.compose.animation:animation:$version"
    const val navigation = "androidx.navigation:navigation-compose:2.4.1"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0"
    const val glide = "com.github.skydoves:landscapist-glide:1.5.0"
  }

  object Accompanist {
    const val version = "0.23.1"
    const val insets = "com.google.accompanist:accompanist-insets:$version"
    const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:$version"
    const val pager = "com.google.accompanist:accompanist-pager:$version"
    const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$version"
    const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
  }

  object Dagger {
    const val version = "2.41"
    const val core = "com.google.dagger:dagger:$version"
    const val compilerKapt = "com.google.dagger:dagger-compiler:$version"
  }

  object Room {
    const val version = "2.4.2"
    const val core = "androidx.room:room-runtime:$version"
    const val ktx = "androidx.room:room-ktx:$version"
    const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
    const val compilerKapt = "androidx.room:room-compiler:$version"
  }

  object Retrofit {
    const val version = "2.9.0"
    const val core = "com.squareup.retrofit2:retrofit:$version"
    const val converter = "com.squareup.retrofit2:converter-moshi:$version"
  }

  object OkHttp {
    const val version = "4.9.1"
    const val core = "com.squareup.okhttp3:okhttp:$version"
    const val interceptor = "com.squareup.okhttp3:logging-interceptor:$version"
  }

  object Firebase {
    const val platform = "com.google.firebase:firebase-bom:29.3.0"
    const val authenticationKtx = "com.google.firebase:firebase-auth-ktx"
    const val databaseKtx = "com.google.firebase:firebase-database-ktx"
    const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx"
    const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx"
  }

  object Moshi {
    const val version = "1.13.0"
    const val core = "com.squareup.moshi:moshi-kotlin:$version"
    const val compilerKapt = "com.squareup.moshi:moshi-kotlin-codegen:$version"
  }

  object CommonTest {
    const val coreVersion = "1.4.0"
    const val espressoVersion = "3.4.0"
    const val coreKtx = "androidx.test:core-ktx:$coreVersion"
    const val junitKtx = "androidx.test.ext:junit-ktx:1.1.3"
    const val runner = "androidx.test:runner:$coreVersion"
    const val espressoCore = "androidx.test.espresso:espresso-core:$espressoVersion"
    const val espressoContrib = "androidx.test.espresso:espresso-contrib:$espressoVersion"
    const val uiAutomator = "androidx.test.uiautomator:uiautomator:2.2.0"
    const val robolectric = "org.robolectric:robolectric:4.6.1"
    const val mockito = "org.mockito:mockito-core:4.0.0"
  }
}