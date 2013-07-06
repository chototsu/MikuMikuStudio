package projectkyoto.mmd.sdk;

import org.openide.util.NbPreferences;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class MikuMikuDanceSupport {

    public static final String KEY_GLSL_SKINNING = "glslSkinningEnabled";

    public static boolean isGlslSkinningEnabled() {
        return NbPreferences.forModule(MikuMikuDanceSupport.class).getBoolean(
                KEY_GLSL_SKINNING, true);
    }

    public static void setGlslSkinningEnabled(boolean glslSkinningEnabled) {
        NbPreferences.forModule(MikuMikuDanceSupport.class).putBoolean(
                KEY_GLSL_SKINNING, glslSkinningEnabled);
    }
}
