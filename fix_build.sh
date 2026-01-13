#!/bin/bash
set -e

echo "1. Stopping old Gradle daemons..."
gradle --stop || true

echo "2. Fixing settings.gradle..."
cat > settings.gradle << 'EOF'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "JARVIS"
include ':app'
EOF

echo "3. Fixing Root build.gradle..."
cat > build.gradle << 'EOF'
plugins {
    id 'com.android.application' version '8.5.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.24' apply false
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
EOF

echo "4. Fixing App build.gradle..."
cat > app/build.gradle << 'EOF'
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.jarvis.core'
    compileSdk 34

    defaultConfig {
        applicationId "com.jarvis.core"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion '1.5.14'
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.biometric:biometric:1.1.0'
    implementation 'com.google.ai.client.generativeai:generativeai:0.2.2'
}
EOF

echo "5. Cleaning up Manifest..."
# Removes the deprecated 'package' attribute to fix the warning
sed -i 's/package="com.jarvis.core"//' app/src/main/AndroidManifest.xml

echo "6. Cleaning caches and Building..."
rm -rf .gradle build app/build
gradle :app:assembleDebug --no-daemon
