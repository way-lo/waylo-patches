/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.Option
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.stringOption
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction10t
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21t
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val relayProSpoofClientPatch = bytecodePatch(
    name = "Spoof client",
    description = "Restores functionality of the app by using custom client ID.",
    default = true
) {
    val clientIdOption: Option<String> = stringOption(
        "client-id",
        null,
        null,
        "OAuth client ID",
        "The Reddit OAuth client ID. Refer to Patcheddit documentation for more " +
                "information on what to put here.",
        true,
        validator = { value ->
            if (value.isNullOrBlank()) {
                return@stringOption false
            }
            if (!value.matches(Regex("^[\\w-]{22}$"))) {
                return@stringOption false
            }
            true
        }
    )
    val redirectUriOption: Option<String> = stringOption(
        "redirect-uri",
        "redreader://rr_oauth_redir",
        null,
        "Redirect URI",
        "The Reddit OAuth redirect URI. Refer to Patcheddit documentation for more " +
                "information on what to put here. Default value is RedReader's redirect URI.",
        true,
    )
    val userAgentOption: Option<String> = stringOption(
        "user-agent",
        "org.quantumbadger.redreader/1.25.1",
        null,
        "User agent",
        "The app's user agent. Refer to Patcheddit documentation for more information " +
                "on what to put here. Default value is RedReader's user agent.",
        true
    )

    compatibleWith(*RelayProCompatible)

    dependsOn(relayProExtensionPatch)

    val clientId by clientIdOption
    val redirectUri by redirectUriOption
    val userAgent by userAgentOption

    execute {
        // region Patch redirect URI.
        redirectUriFingerprint.matchAll().forEach { match ->
            val redirectUriIndex = match.stringMatches.last().index
            match.method.apply {
                val redirectUriRegister = getInstruction<OneRegisterInstruction>(redirectUriIndex).registerA

                match.method.replaceInstruction(
                    redirectUriIndex,
                    "const-string v$redirectUriRegister, \"$redirectUri\"",
                )
            }
        }
        // endregion

        // region Patch client id.
        clientIdFingerprint.matchAll().forEach { match ->
            val clientIdIndex = match.stringMatches.first().index
            match.method.apply {
                val clientIdRegister = getInstruction<OneRegisterInstruction>(clientIdIndex).registerA

                match.method.replaceInstruction(
                    clientIdIndex,
                    "const-string v$clientIdRegister, \"$clientId\"",
                )
            }
        }
        // endregion

        // region Patch user agent.
        networkModuleUserAgentFingerprint.apply {
            val invokeDirectIndex = instructionMatches.first().index
            val invokeDirectRegister = method.getInstruction<OneRegisterInstruction>(invokeDirectIndex).registerA

            val userAgentField = classDef.fields.first { field ->
                field.type == "Ljava/lang/String;"
            }

            method.addInstructions(invokeDirectIndex, """
                const-string v$invokeDirectRegister, "$userAgent"
                sput-object v$invokeDirectRegister, ${userAgentField.definingClass}->${userAgentField.name}:Ljava/lang/String;
            """)
        }
        // endregion

        // region Patch miscellaneous.

        // Do not load remote config which disables OAuth login remotely.
        setRemoteConfigFingerprint.method.addInstructions(0, "return-void")

        // Prevent OAuth login being disabled remotely.
        val checkIsOAuthRequestIndex = redditCheckDisableAPIFingerprint.instructionMatches.first().index

        redditCheckDisableAPIFingerprint.method.apply {
            val returnNextChain = getInstruction<BuilderInstruction21t>(checkIsOAuthRequestIndex).target
            replaceInstruction(checkIsOAuthRequestIndex, BuilderInstruction10t(Opcode.GOTO, returnNextChain))
        }

        // endregion
    }
}
