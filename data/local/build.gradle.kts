apply {
    from("$rootDir/build-common.gradle")
}

plugins { id("com.android.library") }

android {
    namespace = "com.example.local"
}

dependencies {
    "implementation"(project(Modules.repo))
    "implementation"(Local.room_runtime)
    "implementation"(Local.room_ktx)
    "kapt"(Local.room_compiler)

    "implementation"(Remote.moshi)
    "kapt"(Remote.moshi_kotlin_code_generation)
}