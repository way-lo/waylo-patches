package app.waylo.patches.googlenews

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.resourcePatch
import org.w3c.dom.Element

private const val MICROG_PACKAGE = "app.revanced.android.gms"

private val COMPATIBILITY_GOOGLE_NEWS = Compatibility(
    name = "Google News",
    packageName = "com.google.android.apps.magazines",
    apkFileType = ApkFileType.APK,
    appIconColor = 0x4285F4,
    targets = listOf(
        // Newest first, so Morphe suggests it.
        AppTarget(
            version = "5.163.0.947799485"
        ),
        // The patch only edits the manifest, so other versions are expected to work.
        AppTarget(
            version = null,
            isExperimental = true
        )
    )
)

@Suppress("unused")
val microGQueriesPatch = resourcePatch(
    name = "Declare MicroG in manifest queries",
    description = "Lets Google News see MicroG on Android 11 and later. Without this, package visibility " +
            "hides MicroG from the app, the account provider cannot be found, and sign-in ends in " +
            "\"This account already exists on your device\". Use it together with the GmsCore support " +
            "patch from another patch source.",
    default = true
) {
    compatibleWith(COMPATIBILITY_GOOGLE_NEWS)

    execute {
        document("AndroidManifest.xml").use { doc ->
            val manifest = doc.getElementsByTagName("manifest").item(0)
                ?: throw PatchException("AndroidManifest.xml has no <manifest> element")

            val queries = doc.getElementsByTagName("queries").item(0)
                ?: doc.createElement("queries").also { created ->
                    // <queries> must be a direct child of <manifest>; put it before <application>.
                    val application = doc.getElementsByTagName("application").item(0)
                    manifest.insertBefore(created, application)
                }

            val packages = queries.childNodes
            val alreadyDeclared = (0 until packages.length).any { index ->
                val node = packages.item(index) as? Element ?: return@any false
                node.tagName == "package" && node.getAttribute("android:name") == MICROG_PACKAGE
            }
            if (alreadyDeclared) return@use

            val microG = doc.createElement("package")
            microG.setAttribute("android:name", MICROG_PACKAGE)
            queries.appendChild(microG)
        }
    }
}
