// The "shared" extension: the Morphe extension library (Utils, Logger, ...) that patches built with
// sharedExtensionPatch() merge into the app. It is also where the application context hook points.
android {
    namespace = "app.waylo.extension.shared"

    buildTypes.getByName("release") {
        proguardFiles(rootProject.file("extensions/proguard-rules.pro"))
    }
}

dependencies {
    implementation(libs.morphe.extensions.library)
}
