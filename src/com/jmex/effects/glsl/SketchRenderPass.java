package com.jmex.effects.glsl;

import com.jme.image.Texture;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.*;
import com.jme.system.DisplaySystem;

/**
 * GLSL sketch effect pass.
 * - Render supplied source to a texture
 * - Render normals and depth to texture
 * - Apply sobel filter to find changes is normal and depth
 * - (Blur if wanted, slow)
 * - Overwrite or blend with first pass
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class SketchRenderPass extends Pass {
	private TextureRenderer tRendererDepth;
	private Texture textureDepth;

	private Quad fullScreenQuad;

	private GLSLShaderObjectsState sobelShader;
	private GLSLShaderObjectsState normShader;

	private float normalMult = 0.1f;
	private float depthMult = 6.0f;
	private float off = 0.002f;
	private boolean supported = true;

	protected RenderState[] preStates = new RenderState[RenderState.RS_MAX_STATE];

	/**
	 * Reset sketch parameters to default
	 */
	public void resetParameters() {
		normalMult = 0.1f;
		depthMult = 6.0f;
		off = 0.002f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		tRendererDepth.cleanup();
	}
	
	public boolean isSupported() {
		return supported;
	}

	public SketchRenderPass(Camera cam, int renderScale) {
		DisplaySystem display = DisplaySystem.getDisplaySystem();

		resetParameters();

		//Create texture renderers and rendertextures for depth (only needed for some nvidia cards, like mine)
		tRendererDepth = display.createTextureRenderer(
				display.getWidth() / renderScale, display.getHeight() / renderScale, false, true, false, false,
				TextureRenderer.RENDER_TEXTURE_2D, 0);
		tRendererDepth.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
		tRendererDepth.setCamera(cam);

		textureDepth = new Texture();
		textureDepth.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		textureDepth.setFilter(Texture.FM_LINEAR);
		tRendererDepth.setupTexture(textureDepth);

		//Create extract normals and depth shader
		normShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!normShader.isSupported()) {
			supported = false;
		} else {
			normShader.load(SketchRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/sketch_norm.vert"),
					SketchRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/sketch_norm.frag"));
			normShader.setEnabled(true);
			normShader.setUniform("nearClip", cam.getFrustumNear());
			normShader.setUniform("diffClip", cam.getFrustumFar() - cam.getFrustumNear());
		}

		//Create sobel shader
		sobelShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!sobelShader.isSupported()) {
			supported = false;
		} else {
			sobelShader.load(SketchRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/sketch_sobel.vert"),
					SketchRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/sketch_sobel.frag"));
			sobelShader.setEnabled(true);
		}

		//Create fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth(), display.getHeight());
		fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.getLocalScale().set(1, 1, 1);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullMode(Spatial.CULL_NEVER);
		fullScreenQuad.setTextureCombineMode(TextureState.REPLACE);
		fullScreenQuad.setLightCombineMode(LightState.OFF);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		fullScreenQuad.setRenderState(ts);

		fullScreenQuad.updateRenderState();
		fullScreenQuad.updateGeometricState(0.0f, true);

		//setup shader for extracting normals and depth
		noTexture = display.getRenderer().createTextureState();
		noTexture.setEnabled(false);
		noLights = display.getRenderer().createLightState();
		noLights.setEnabled(false);
		noMaterials = display.getRenderer().createMaterialState();
		noMaterials.setEnabled(false);
	}

	protected static TextureState noTexture;
	protected static LightState noLights;
	protected static MaterialState noMaterials;

	public void doRender(Renderer r) {
		if(spatials.size() != 1) return;

		tRendererDepth.updateCamera();

		//Render scene to normals and depth
		saveEnforcedStates();
		Spatial.enforceState(noTexture);
		Spatial.enforceState(noLights);
		Spatial.enforceState(noMaterials);
		Spatial.enforceState(normShader);
		tRendererDepth.render((Spatial) spatials.get(0), textureDepth);
		replaceEnforcedStates();

		TextureState ts = (TextureState) fullScreenQuad.getRenderState(RenderState.RS_TEXTURE);

		//Apply sobel as final render
		sobelShader.clearUniforms();
		sobelShader.setUniform("depth", 0);
		sobelShader.setUniform("normalMult", getNormalMult());
		sobelShader.setUniform("depthMult", getDepthMult());
		sobelShader.setUniform("off", getOff());

		ts.setTexture(textureDepth, 0);
		fullScreenQuad.setRenderState(sobelShader);
		fullScreenQuad.updateRenderState();
		fullScreenQuad.onDraw(r);
	}

	/**
	 * saves any states enforced by the user for replacement at the end of the
	 * pass.
	 */
	protected void saveEnforcedStates() {
		for(int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			preStates[x] = Spatial.enforcedStateList[x];
		}
	}

	/**
	 * replaces any states enforced by the user at the end of the pass.
	 */
	protected void replaceEnforcedStates() {
		for(int x = RenderState.RS_MAX_STATE; --x >= 0;) {
			Spatial.enforcedStateList[x] = preStates[x];
		}
	}

	public float getNormalMult() {
		return normalMult;
	}

	public void setNormalMult(float normalMult) {
		this.normalMult = normalMult;
	}

	public float getDepthMult() {
		return depthMult;
	}

	public void setDepthMult(float depthMult) {
		this.depthMult = depthMult;
	}

	public float getOff() {
		return off;
	}

	public void setOff(float off) {
		this.off = off;
	}
}
