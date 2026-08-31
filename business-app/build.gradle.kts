plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // alias(libs.plugins.google.services) apply false  // Disabled until google-services.json is added
}

// Redirect root build directory to D: drive
layout.buildDirectory.set(file("D:/gradle_builds/business-app/root"))

