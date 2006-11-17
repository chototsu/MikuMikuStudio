package com.jme.scene.state.lwjgl.records;

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Represents a texture unit in opengl
 */
public class TextureUnitRecord {
    public boolean enabled = false;
    public Matrix4f texMatrix = new Matrix4f();
    public Vector3f texScale = new Vector3f();
    public int boundTexture = -1;
    public int envMode = -1;
    public float envRGBScale;
    public float envAlphaScale;
    public ColorRGBA blendColor;
    public boolean textureGenQ = false, textureGenR = false, textureGenS = false, textureGenT = false;
    public int textureGenQMode = -1, textureGenRMode = -1, textureGenSMode = -1, textureGenTMode = -1;
    public int rgbCombineFunc = -1, alphaCombineFunc = -1;
    public int combSrcRGB0 = -1, combSrcRGB1 = -1, combSrcRGB2 = -1;
    public int combOpRGB0 = -1, combOpRGB1 = -1, combOpRGB2 = -1;
    public int combSrcAlpha0 = -1, combSrcAlpha1 = -1, combSrcAlpha2 = -1;
    public int combOpAlpha0 = -1, combOpAlpha1 = -1, combOpAlpha2 = -1;
    public boolean identityMatrix = true;

    public TextureUnitRecord() {
        blendColor = new ColorRGBA(1,1,1,1);
    }
}
