package app.waylo.patches.podcastaddict

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object Constants {
    val COMPATIBILITY_PODCAST_ADDICT = Compatibility(
        name = "Podcast Addict",
        packageName = "com.bambuna.podcastaddict",
        apkFileType = ApkFileType.APK,
        appIconColor = 0xF4511E,
        targets = listOf(
            // Version the patch was developed and tested against (build 21719).
            AppTarget(
                version = "2026.7"
            ),
            // The fingerprints avoid obfuscated names, so later versions are expected to work.
            AppTarget(
                version = null,
                isExperimental = true
            )
        )
    )
}
