/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.patches.relaypro

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.methodCall
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.registersUsed

private const val EXTENSION_MODIFY_WEBVIEW = "Lapp/waylo/extension/relaypro/ModifyWebViewPatch;"

@Suppress("unused")
val relayProModifyLoginWebViewPatch = bytecodePatch(
    name = "Modify login WebView",
    description = "Modify the WebView used for logging into reddit to prevent login issues",
    default = true
) {
    compatibleWith(*RelayProCompatible)

    dependsOn(relayProExtensionPatch)

    execute {
        Fingerprint(
            filters = listOf(
                methodCall(
                    definingClass = "Landroid/webkit/WebView;",
                    name = "loadUrl"
                )
            ),
            custom = { _, classDef ->
                !classDef.type.startsWith("Lapp/morphe/") && !classDef.type.startsWith("Lapp/waylo/")
            }
        ).matchAll().forEach { match ->
            val index = match.instructionMatches[0].index
            val instruction = match.instructionMatches[0].instruction
            val webviewReg = instruction.registersUsed[0]
            val urlReg = instruction.registersUsed[1]
            if (instruction.registersUsed.size == 2) {
                match.method.replaceInstruction(
                    index,
                    """
                        invoke-static { v$webviewReg, v$urlReg }, $EXTENSION_MODIFY_WEBVIEW->loadUrl(Landroid/webkit/WebView;Ljava/lang/String;)V
                    """
                )
            } else {
                val headersReg = instruction.registersUsed[2]
                match.method.replaceInstruction(
                    index,
                    """
                        invoke-static { v$webviewReg, v$urlReg, v$headersReg }, $EXTENSION_MODIFY_WEBVIEW->loadUrl(Landroid/webkit/WebView;Ljava/lang/String;Ljava/util/Map;)V
                    """
                )
            }
        }
    }
}
