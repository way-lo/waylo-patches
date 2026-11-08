/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import app.morphe.util.findInstructionIndicesReversedOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

private const val EXTENSION_CLASS_DESCRIPTOR = "Lapp/waylo/extension/relaypro/FixSLinksPatch;"
private const val RESOLVE_S_LINK_METHOD = "patchResolveSLink(Ljava/lang/String;)Z"
private const val SET_ACCESS_TOKEN_METHOD = "patchSetAccessToken(Ljava/lang/String;)V"

@Suppress("unused")
val relayProFixSLinksPatch = bytecodePatch(
    name = "Fix /s/ links",
    default = true
) {
    compatibleWith(*RelayProCompatible)

    dependsOn(relayProExtensionPatch)

    execute {
        refreshTokenAccessFingerprint.matchAll().forEach { match ->
            match.method.findInstructionIndicesReversedOrThrow(refreshTokenAccessFilter).forEach { index ->
                // Now inject a call to our extension method to also set the access token there.
                val instruction = match.method.getInstruction(index)
                val register = (instruction as TwoRegisterInstruction).registerA
                match.method.addInstruction(
                    index,
                    """
                        invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->$SET_ACCESS_TOKEN_METHOD
                    """
                )
            }
        }

        // region Patch navigation handler.

        intentFilterActivityFingerprint.method.apply {
            val index = intentFilterActivityFingerprint.instructionMatches.last().index
            val register = getInstruction<OneRegisterInstruction>(index).registerA

            addInstructionsWithLabels(
                index + 1,
                """
                    invoke-static { v$register }, $EXTENSION_CLASS_DESCRIPTOR->$RESOLVE_S_LINK_METHOD
                    move-result v5
                    if-eqz v5, :continue
                    return-void
                """,
                ExternalLabel("continue", getInstruction(index + 1)),
            )
        }

        // endregion
    }
}
