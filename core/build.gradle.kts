apply {
    from("$rootDir/build-common-ui.gradle.kts")
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.core"
}

dependencies {
}
