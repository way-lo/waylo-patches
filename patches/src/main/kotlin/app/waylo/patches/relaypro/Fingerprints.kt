/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.OpcodeFilter
import app.morphe.patcher.OpcodesFilter
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

// region Spoof client

internal val clientIdFingerprint = Fingerprint(
    strings = listOf("dj-xCIZQYiLbEg")
)

internal val redditCheckDisableAPIFingerprint = Fingerprint(
    strings = listOf("Reddit Disabled"),
    filters = listOf(
        OpcodeFilter(Opcode.IF_EQZ)
    )
)

internal val setRemoteConfigFingerprint = Fingerprint(
    parameters = listOf("Lcom/google/firebase/remoteconfig/FirebaseRemoteConfig;"),
    strings = listOf("reddit_oauth_url")
)

internal val redirectUriFingerprint = Fingerprint(
    strings = listOf("dbrady://relay")
)

// Lreddit/news/oauth/dagger/modules/NetworkModule;
internal val networkModuleUserAgentFingerprint = Fingerprint(
    accessFlags = listOf(AccessFlags.STATIC),
    returnType = "Lokhttp3/OkHttpClient;",
    filters = OpcodesFilter.opcodesToFilters(
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT
    )
)

// endregion

// region Fix /s/ links

internal val intentFilterActivityFingerprint = Fingerprint(
    definingClass = "Lreddit/news/IntentFilterActivity;",
    strings = listOf("path", "amp.reddit.com"),
    filters = listOf(
        string("path"),
        string("amp.reddit.com"),
        methodCall(name = "getIntent"),
        OpcodeFilter(Opcode.MOVE_RESULT_OBJECT),
        methodCall(name = "getDataString"),
        OpcodeFilter(Opcode.MOVE_RESULT_OBJECT)
    ),
)

internal val refreshTokenAccessFilter = fieldAccess(
    definingClass = "Lreddit/news/oauth/reddit/model/RedditAccessToken;",
    name = "refreshToken",
    opcode = Opcode.IPUT_OBJECT,
)

internal val refreshTokenAccessFingerprint = Fingerprint(
    filters = listOf(
        refreshTokenAccessFilter
    ),
    custom = { _, classDef ->
        !classDef.type.startsWith("Lapp/morphe/") && !classDef.type.startsWith("Lapp/waylo/")
    }
)

// endregion
