apply {
    from("$rootDir/build-common.gradle")
}

plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.repo"
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.domain))

    "implementation"(Remote.moshi)
    "kapt"(Remote.moshi_kotlin_code_generation)
}