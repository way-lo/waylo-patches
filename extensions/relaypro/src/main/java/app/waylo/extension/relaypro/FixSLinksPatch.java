/*
 * Copyright 2026 wchill.
 * https://github.com/wchill/patcheddit
 *
 * Taken from Patcheddit v1.4.0 (Relay patches) and adapted for waylo-patches.
 * See the included NOTICE file for GPLv3 §7(b) and §7(c) terms that apply to this code.
 */

package app.waylo.extension.relaypro;

import android.app.Activity;

/**
 * @noinspection unused
 */
public class FixSLinksPatch extends BaseFixSLinksPatch {
    static {
        INSTANCE = new FixSLinksPatch();
    }

    @SuppressWarnings("unchecked")
    private FixSLinksPatch() {
        try {
            // Relay's own in-app browser activity. Looked up by name, so no stub class is needed at build time.
            webViewActivityClass = (Class<? extends Activity>) Class.forName("reddit.news.WebAndComments");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Relay's reddit.news.WebAndComments activity was not found", e);
        }
    }

    public static boolean patchResolveSLink(String link) {
        return INSTANCE.resolveSLink(link);
    }

    public static boolean patchResolveSLink(Activity activity) {
        String data = activity.getIntent().getDataString();

        if (data == null) {
            return false;
        }

        return INSTANCE.resolveSLink(data);
    }

    public static void patchSetAccessToken(String accessToken) {
        INSTANCE.setAccessToken(accessToken);
    }
}
