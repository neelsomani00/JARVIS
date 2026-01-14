#!/bin/bash
set -e

echo "========================================"
echo "      JARVIS REPAIR SYSTEM STARTED      "
echo "========================================"

echo "[1/6] Stopping Gradle Daemons to clear locks..."
gradle --stop || true
rm -rf .gradle build app/build app/build.gradle settings.gradle build.gradle

echo "[2/6] rewriting settings.gradle..."
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

echo "[3/6] rewriting Root build.gradle..."
cat > build.gradle << 'EOF'
plugins {
    id 'com.android.application' version '8.5.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.24' apply false
}
task clean(type: Delete) {
    delete rootProject.buildDir
}
EOF

echo "[4/6] rewriting App build.gradle (Fixed Dependencies)..."
mkdir -p app
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
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
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
    
    packagingOptions {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    
    // Compose UI
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    // Feature Libraries
    implementation 'androidx.biometric:biometric:1.1.0'
    implementation 'com.google.ai.client.generativeai:generativeai:0.2.2'
    
    // Networking (Fixes AiOrchestrator error)
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    
    // SavedState (Fixes OverlayService error)
    implementation 'androidx.savedstate:savedstate-ktx:1.2.1'
}
EOF

echo "[5/6] Fixing Resources (Manifest, Strings, Themes)..."
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/xml

# strings.xml
cat > app/src/main/res/values/strings.xml << 'EOF'
<resources>
    <string name="app_name">JARVIS</string>
    <string name="accessibility_description">Jarvis Core Engine for Automation and Security</string>
</resources>
EOF

# themes.xml
cat > app/src/main/res/values/themes.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.Jarvis" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:colorPrimary">@android:color/holo_blue_dark</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
    </style>
</resources>
EOF

# accessibility_config.xml
cat > app/src/main/res/xml/accessibility_config.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"
    android:accessibilityEventTypes="typeAllMask"
    android:accessibilityFeedbackType="feedbackGeneric"
    android:accessibilityFlags="flagDefault"
    android:canRetrieveWindowContent="true"
    android:canRequestFilterKeyEvents="true"
    android:description="@string/accessibility_description" />
EOF

# AndroidManifest.xml (Using System Icons)
cat > app/src/main/AndroidManifest.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

    <application
        android:allowBackup="true"
        android:icon="@android:drawable/ic_menu_info_details"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.Jarvis">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Jarvis">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".services.OverlayService"
            android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.accessibilityservice.AccessibilityService" />
            </intent-filter>
            <meta-data
                android:name="android.accessibilityservice"
                android:resource="@xml/accessibility_config" />
        </service>
    </application>
</manifest>
EOF

echo "[6/6] STARTING CLEAN BUILD..."
# We use --no-daemon to prevent memory issues in Codespaces
gradle :app:assembleDebug --no-daemon

echo "========================================"
echo "       REPAIR & BUILD COMPLETE          "
echo "========================================"
