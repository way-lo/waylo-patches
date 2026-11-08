/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patches.all.misc.extension.activityOnCreateExtensionHook
import app.morphe.patches.all.misc.extension.sharedExtensionPatch

// Merges extensions/shared.mpe (Morphe extension library) and extensions/relaypro.mpe into the app,
// and hands the application context to the extension from RelayApplication.onCreate.
internal val relayProExtensionPatch = sharedExtensionPatch(
    "relaypro",
    activityOnCreateExtensionHook("Lreddit/news/RelayApplication;"),
)
