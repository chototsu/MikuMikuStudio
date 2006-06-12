package com.jmex.font2d;

import java.util.Hashtable;

import com.jme.image.Texture;
import com.jme.scene.SceneElement;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.font3d.TextFactory;

public class Font2D implements TextFactory {
    private static final String DEFAULT_FONT = "com/jme/app/defaultfont.tga";
    private static Hashtable<String, TextureState> cachedFontTextureStates = new Hashtable<String, TextureState>();
    private String fontBitmapFile = DEFAULT_FONT;
    private TextureState fontTextureState;

    /**
     * Creates the texture state if not created before.
     * 
     * @return texture state for the default font
     */
    public static TextureState getFontTextureState(String fontFile) {
        TextureState cached = cachedFontTextureStates.get(fontFile);
        if (cached == null) {
            cached = DisplaySystem.getDisplaySystem().getRenderer()
                    .createTextureState();
            cached.setTexture(TextureManager.loadTexture(Text.class
                    .getClassLoader().getResource(fontFile), Texture.MM_LINEAR,
                    Texture.FM_LINEAR));
            cached.setEnabled(true);
            cachedFontTextureStates.put(fontFile, cached);
        }
        return cached;
    }

    /**
     * @return the texture state used by this font.
     */
    public TextureState getFontTextureState() {
        return fontTextureState;
    }

    /**
     * @return the bitmap used by this fonts texture state.
     */
    public String getFontBitmapFile() {
        return fontBitmapFile;
    }

    public Font2D() {
        this(DEFAULT_FONT);
    }

    public Font2D(String fontBitmapFile) {
        this.fontBitmapFile = fontBitmapFile;
        fontTextureState = getFontTextureState(this.fontBitmapFile);
    }

    public Text2D createText(String text, float size, int flags) {
        Text2D textObj = new Text2D(this, text, size, flags);
        textObj.setCullMode(SceneElement.CULL_NEVER);
        textObj.setRenderState(fontTextureState);
        textObj.setRenderState(getFontAlpha());
        textObj.setTextureCombineMode(TextureState.REPLACE);
        textObj.setLightCombineMode(LightState.OFF);
        return textObj;
    }

    /*
     * @return an alpha states for allowing 'black' to be transparent
     */
    private static AlphaState getFontAlpha() {
        AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer()
                .createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        return as1;
    }

}
