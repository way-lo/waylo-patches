/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

/**
 * Relay for Reddit Pro. The free version (`free.reddit.news`) is not covered by these patches.
 *
 * These are the Patcheddit v1.4.0 patches. Patcheddit v2.0 and later no longer work on Relay Pro.
 */
internal val RelayProCompatible = arrayOf(
    Compatibility(
        name = "Relay for Reddit Pro",
        packageName = "reddit.news",
        targets = listOf(AppTarget(version = "10.2.40"))
    )
)
