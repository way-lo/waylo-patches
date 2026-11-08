android {
    namespace = "app.waylo.extension.relaypro"

    buildTypes.getByName("release") {
        proguardFiles(rootProject.file("extensions/proguard-rules.pro"))
    }
}

dependencies {
    compileOnly(libs.morphe.extensions.library)
    compileOnly(libs.annotation)
}
