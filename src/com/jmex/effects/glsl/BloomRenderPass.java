/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.effects.glsl;

import com.jme.image.Texture;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * GLSL bloom effect pass.
 * - Render supplied source to a texture
 * - Extract intensity
 * - Blur intensity
 * - Blend with first pass
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class BloomRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    private TextureRenderer tRendererFirst;
	private TextureRenderer tRendererSecond;
	private Texture textureFirst;
	private Texture textureSecond;

	private Quad fullScreenQuad;

	private GLSLShaderObjectsState extractionShader;
	private GLSLShaderObjectsState blurShader;
	private GLSLShaderObjectsState finalShader;

	private int nrBlurPasses;
	private float blurSize;
	private float blurIntensityMultiplier;
	private float exposurePow;
	private float exposureCutoff;
	private boolean supported = true;
    private boolean useCurrentScene = false;

	/**
	 * Reset bloom parameters to default
	 */
	public void resetParameters() {
		nrBlurPasses = 2;
		blurSize = 0.02f;
		blurIntensityMultiplier = 1.3f;
		exposurePow = 3.0f;
		exposureCutoff = 0.0f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
        super.cleanUp();
		tRendererFirst.cleanup();
		tRendererSecond.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}
	
	/**
	 * Creates a new bloom renderpass
	 *
	 * @param cam		 Camera used for rendering the bloomsource
	 * @param renderScale Scale of bloom texture
	 */
	public BloomRenderPass(Camera cam, int renderScale) {
		DisplaySystem display = DisplaySystem.getDisplaySystem();

		resetParameters();

		//Create texture renderers and rendertextures(alternating between two not to overwrite pbuffers)
		tRendererFirst = display.createTextureRenderer(
				display.getWidth() / renderScale, display.getHeight() / renderScale, false, true, false, false,
				TextureRenderer.RENDER_TEXTURE_2D, 0);
		tRendererFirst.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
		tRendererFirst.setCamera(cam);

		textureFirst = new Texture();
		textureFirst.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		textureFirst.setFilter(Texture.FM_LINEAR);
		tRendererFirst.setupTexture(textureFirst);

		tRendererSecond = display.createTextureRenderer(
				display.getWidth() / renderScale, display.getHeight() / renderScale, false, true, false, false,
				TextureRenderer.RENDER_TEXTURE_2D, 0);
		tRendererSecond.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
		tRendererSecond.setCamera(cam);

		textureSecond = new Texture();
		textureSecond.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		textureSecond.setFilter(Texture.FM_LINEAR);
		tRendererSecond.setupTexture(textureSecond);

		//Create extract intensity shader
		extractionShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!extractionShader.isSupported()) {
			supported = false;
		} else {
			extractionShader.load(BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_extract.vert"),
					BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_extract.frag"));
			extractionShader.setEnabled(true);
		}

		//Create blur shader
		blurShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!blurShader.isSupported()) {
			supported = false;
		} else {
			blurShader.load(BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_blur.vert"),
					BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_blur.frag"));
			blurShader.setEnabled(true);
		}

		//Create final shader(basic texturing)
		finalShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!finalShader.isSupported()) {
			supported = false;
		} else {
			finalShader.load(BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_final.vert"),
					BloomRenderPass.class.getClassLoader().getResource("com/jmex/effects/glsl/data/bloom_final.frag"));
			finalShader.setEnabled(true);
		}

		//Create fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth()/4, display.getHeight()/4);
		fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.getLocalScale().set(1, 1, 1);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullMode(SceneElement.CULL_NEVER);
		fullScreenQuad.setTextureCombineMode(TextureState.REPLACE);
		fullScreenQuad.setLightCombineMode(LightState.OFF);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		fullScreenQuad.setRenderState(ts);

		AlphaState as = display.getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_ONE);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setEnabled(true);
		fullScreenQuad.setRenderState(as);

		fullScreenQuad.updateRenderState();
		fullScreenQuad.updateGeometricState(0.0f, true);
	}

    /**
     * Helper class to get all spatials rendered in one TextureRenderer.render() call.
     */
    private class SpatialsRenderNode extends Node {
        private static final long serialVersionUID = 7367501683137581101L;
        public void draw( Renderer r ) {
            Spatial child;
            for (int i = 0, cSize = spatials.size(); i < cSize; i++) {
                child = (Spatial) spatials.get(i);
                if (child != null)
                    child.onDraw(r);
            }
        }

        public void onDraw( Renderer r ) {
            draw( r );
        }
    }

    private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();


    public void doRender(Renderer r) {
        if (!useCurrentScene && spatials.size() == 0 ) {
            return;
        }

		tRendererFirst.updateCamera();
		tRendererSecond.updateCamera();
        
        // see if we should use the current scene to bloom, or only things added to the pass.
        if (useCurrentScene) {
            // grab backbuffer to texture
            tRendererFirst.copyBufferToTexture(textureFirst, 
                    DisplaySystem.getDisplaySystem().getWidth(), 
                    DisplaySystem.getDisplaySystem().getHeight(), 
                    1);
        } else
    		//Render scene to texture
            tRendererFirst.render( spatialsRenderNode , textureFirst);

		TextureState ts = (TextureState) fullScreenQuad.getRenderState(RenderState.RS_TEXTURE);
		AlphaState as = (AlphaState) fullScreenQuad.getRenderState(RenderState.RS_ALPHA);
		as.setEnabled(false);

		//Extract intensity
		extractionShader.clearUniforms();
		extractionShader.setUniform("RT", 0);
		extractionShader.setUniform("exposurePow", getExposurePow());
		extractionShader.setUniform("exposureCutoff", getExposureCutoff());

		ts.setTexture(textureFirst, 0);
		fullScreenQuad.setRenderState(extractionShader);
		fullScreenQuad.updateRenderState();
		tRendererSecond.render(fullScreenQuad, textureSecond);

		//Blur
		blurShader.clearUniforms();
		blurShader.setUniform("RT", 0);
		blurShader.setUniform("sampleDist0", getBlurSize());
		blurShader.setUniform("blurIntensityMultiplier", getBlurIntensityMultiplier());

		ts.setTexture(textureSecond, 0);
		fullScreenQuad.setRenderState(blurShader);
		fullScreenQuad.updateRenderState();
		tRendererFirst.render(fullScreenQuad, textureFirst);

		//Extra blur passes
		for(int i = 0; i < getNrBlurPasses() - 1; i++) {
			ts.setTexture(textureFirst, 0);
			fullScreenQuad.updateRenderState();
			tRendererSecond.render(fullScreenQuad, textureSecond);

			ts.setTexture(textureSecond, 0);
			fullScreenQuad.updateRenderState();
			tRendererFirst.render(fullScreenQuad, textureFirst);
		}

		//Final blend
		ts.setTexture(textureFirst, 0);
		as.setEnabled(true);
		fullScreenQuad.setRenderState(finalShader);
		fullScreenQuad.updateRenderState();
		fullScreenQuad.onDraw(r);
	}

	public float getBlurSize() {
		return blurSize;
	}

	public void setBlurSize(float blurSize) {
		this.blurSize = blurSize;
	}

	public float getExposurePow() {
		return exposurePow;
	}

	public void setExposurePow(float exposurePow) {
		this.exposurePow = exposurePow;
	}

	public float getExposureCutoff() {
		return exposureCutoff;
	}

	public void setExposureCutoff(float exposureCutoff) {
		this.exposureCutoff = exposureCutoff;
	}

	public float getBlurIntensityMultiplier() {
		return blurIntensityMultiplier;
	}

	public void setBlurIntensityMultiplier(float blurIntensityMultiplier) {
		this.blurIntensityMultiplier = blurIntensityMultiplier;
	}

	public int getNrBlurPasses() {
		return nrBlurPasses;
	}

	public void setNrBlurPasses(int nrBlurPasses) {
		this.nrBlurPasses = nrBlurPasses;
	}

    public boolean useCurrentScene() {
        return useCurrentScene;
    }

    public void setUseCurrentScene(boolean useCurrentScene) {
        this.useCurrentScene = useCurrentScene;
    }
}
