apply {
    from("$rootDir/build-common-ui.gradle.kts")
}

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.ui"
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUI))
}
