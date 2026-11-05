plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.hilt)
    alias(libs.plugins.compose.compiler)
}


android {
    namespace = "remix.myplayer"

    compileSdk = 36

    defaultConfig {
        applicationId = "remix.myplayer"
        minSdk = 21
        targetSdk = 36

        versionCode = 21201
        versionName = "2.1.2.1"

        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "LASTFM_API_KEY", "\"\"")
        buildConfigField("String", "GOOGLE_PLAY_LICENSE_KEY", "\"\"")
        buildConfigField("String", "BUGLY_APP_ID", "\"\"")
        buildConfigField("String", "FLAVOR", "\"foss\"")
        buildConfigField("boolean", "ENABLE_UPDATE", "false")
    }

    androidResources {
        localeFilters += listOf(
            "en",
            "ja",
            "ja-rJP",
            "zh",
            "zh-rCN",
            "zh-rHK",
            "zh-rTW"
        )
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("releaseConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
            isDebuggable = true
            isMinifyEnabled = false
        }

        release {
            signingConfig = signingConfigs.getByName("releaseConfig")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += listOf("MissingTranslation", "InvalidPackage")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    dependenciesInfo {
        includeInApk = false
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines)
//    implementation(libs.kotlinx.serialization)

    implementation(libs.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.media)
    implementation(libs.androidx.media3.exoplayer)
    implementation(files("libs/lib-decoder-ffmpeg-release.aar"))
    implementation(libs.palette.ktx)

    implementation(libs.material)

    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.glide.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    implementation(libs.image.cropper)
    implementation(libs.xxpermissions)
    implementation(libs.sardine.android) {
        exclude(group = "xpp3", module = "xpp3")
    }
    implementation(libs.smbj)
    implementation(libs.timber)

    // debugImplementation(libs.leakcanary)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.nav)
    implementation(libs.androidx.hilt.navi.compose)
    implementation(libs.reorderable)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    implementation(libs.androidx.profileinstaller)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16.1")
}
