apply {
    from("$rootDir/build-common.gradle")
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.domain"
}

dependencies {
    "implementation"(project(Modules.core))
}