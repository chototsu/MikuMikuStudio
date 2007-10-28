/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * GLSL bloom effect pass. - Render supplied source to a texture - Extract
 * intensity - Blur intensity - Blend with first pass
 * 
 * @author Rikard Herlitz (MrCoder) - initial implementation
 * @author Joshua Slack - Enhancements and reworking to use a single
 *         texrenderer, ability to reuse existing back buffer, faster blur,
 *         throttling speed-up, etc.
 */
public class BloomRenderPass extends Pass {
    private static final long serialVersionUID = 1L;

    private float throttle = 1/50f; 
    private float sinceLast = 1; 
    
    private TextureRenderer tRenderer;
	private Texture mainTexture;
    private Texture secondTexture;
    private Texture screenTexture;

    private Quad fullScreenQuad;
	private TriangleBatch fullScreenQuadBatch;

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

	public static String shaderDirectory = "com/jmex/effects/glsl/data/";

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
        if (tRenderer != null)
            tRenderer.cleanup();
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
        tRenderer = display.createTextureRenderer(
                display.getWidth() / renderScale, 
                display.getHeight() / renderScale,
                TextureRenderer.RENDER_TEXTURE_2D);

		if (!tRenderer.isSupported()) {
			supported = false;
			return;
		}
		tRenderer.setMultipleTargets(true);
        tRenderer.setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
        tRenderer.setCamera(cam);

		mainTexture = new Texture();
		mainTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
		mainTexture.setFilter(Texture.FM_LINEAR);
        tRenderer.setupTexture(mainTexture);

        secondTexture = new Texture();
        secondTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
        secondTexture.setFilter(Texture.FM_LINEAR);
        tRenderer.setupTexture(secondTexture);

        screenTexture = new Texture();
        screenTexture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
        screenTexture.setFilter(Texture.FM_LINEAR);
        tRenderer.setupTexture(screenTexture);

		//Create extract intensity shader
		extractionShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!extractionShader.isSupported()) {
			supported = false;
			return;
		} else {
			extractionShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_extract.vert"),
					BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_extract.frag"));
			extractionShader.setEnabled(true);
            extractionShader.setUniform("RT", 0);
		}

		//Create blur shader
		blurShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!blurShader.isSupported()) {
			supported = false;
			return;
		} else {
			blurShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.vert"),
					BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_blur.frag"));
			blurShader.setEnabled(true);
            blurShader.setUniform("RT", 0);
		}

		//Create final shader(basic texturing)
		finalShader = display.getRenderer().createGLSLShaderObjectsState();
		if(!finalShader.isSupported()) {
			supported = false;
			return;
		} else {
			finalShader.load(BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_final.vert"),
					BloomRenderPass.class.getClassLoader().getResource(shaderDirectory + "bloom_final.frag"));
			finalShader.setEnabled(true);
		}

		//Create fullscreen quad
		fullScreenQuad = new Quad("FullScreenQuad", display.getWidth()/4, display.getHeight()/4);
        fullScreenQuadBatch = fullScreenQuad.getBatch(0);
		fullScreenQuad.getLocalRotation().set(0, 0, 0, 1);
		fullScreenQuad.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
		fullScreenQuad.getLocalScale().set(1, 1, 1);
		fullScreenQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		fullScreenQuad.setCullMode(SceneElement.CULL_NEVER);
		fullScreenQuad.setTextureCombineMode(TextureState.REPLACE);
		fullScreenQuad.setLightCombineMode(LightState.OFF);
        
		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
        fullScreenQuadBatch.setRenderState(ts);

		AlphaState as = display.getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_ONE);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setEnabled(true);
        fullScreenQuadBatch.setRenderState(as);

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
                child = spatials.get(i);
                if (child != null)
                    child.onDraw(r);
            }
        }

        public void onDraw( Renderer r ) {
            draw( r );
        }
    }

    private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();

    @Override
    protected void doUpdate(float tpf) {
        super.doUpdate(tpf);
        sinceLast += tpf;
    }
    
    public void doRender(Renderer r) {
        if (!useCurrentScene && spatials.size() == 0 ) {
            return;
        }

        AlphaState as = (AlphaState) fullScreenQuadBatch.states[RenderState.RS_ALPHA];

        if (sinceLast > throttle) {
            sinceLast = 0;

            as.setEnabled(false);
            TextureState ts = (TextureState) fullScreenQuadBatch.states[RenderState.RS_TEXTURE];
            
            // see if we should use the current scene to bloom, or only things added to the pass.
            if (useCurrentScene) {
                // grab backbuffer to texture
                tRenderer.copyToTexture(screenTexture, 
                        DisplaySystem.getDisplaySystem().getWidth(), 
                        DisplaySystem.getDisplaySystem().getHeight());
                ts.setTexture(screenTexture, 0);
            } else {
        		//Render scene to texture
                tRenderer.render( spatialsRenderNode , mainTexture);
                ts.setTexture(mainTexture, 0);
            }

    		//Extract intensity
    		extractionShader.setUniform("exposurePow", getExposurePow());
    		extractionShader.setUniform("exposureCutoff", getExposureCutoff());
    
            fullScreenQuadBatch.states[RenderState.RS_GLSL_SHADER_OBJECTS] = extractionShader;
            tRenderer.render(fullScreenQuad, secondTexture);
    
    		//Blur
    		blurShader.setUniform("sampleDist0", getBlurSize());
    		blurShader.setUniform("blurIntensityMultiplier",  getBlurIntensityMultiplier());
    
            ts.setTexture(secondTexture, 0);
            fullScreenQuadBatch.states[RenderState.RS_GLSL_SHADER_OBJECTS] = blurShader;
            tRenderer.render(fullScreenQuad, mainTexture);
    
    		//Extra blur passes
    		for(int i = 1; i < getNrBlurPasses(); i++) {
                if (i%2 == 1) {
                    ts.setTexture(mainTexture, 0);
                    tRenderer.render(fullScreenQuad, secondTexture);
                } else {
                    ts.setTexture(secondTexture, 0);
                    tRenderer.render(fullScreenQuad, mainTexture);
                }
    		}
            if (getNrBlurPasses()%2 == 1) {
                ts.setTexture(mainTexture, 0);
            } else {
                ts.setTexture(secondTexture, 0);
            }
        }

		//Final blend
		as.setEnabled(true);
        
        fullScreenQuadBatch.states[RenderState.RS_GLSL_SHADER_OBJECTS] = finalShader;
        r.draw(fullScreenQuadBatch);
	}

	/**
     * @return The throttle amount - or in other words, how much time in
     *         seconds must pass before the bloom effect is updated.
     */
    public float getThrottle() {
        return throttle;
    }

    /**
     * @param throttle
     *            The throttle amount - or in other words, how much time in
     *            seconds must pass before the bloom effect is updated.
     */
    public void setThrottle(float throttle) {
        this.throttle = throttle;
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
