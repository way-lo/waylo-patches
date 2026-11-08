/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.extension.relaypro;

public enum ResolveResult {
    // Let app handle rest of stuff
    CONTINUE,
    // Start app, to make it cache its access_token
    ACCESS_TOKEN_START,
    // Don't do anything - we started resolving
    DO_NOTHING
}
