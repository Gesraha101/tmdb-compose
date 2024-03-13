apply {
    from("$rootDir/build-common-ui.gradle.kts")
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.core"
}

dependencies {
}
