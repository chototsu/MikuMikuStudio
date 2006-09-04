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
import com.jme.math.Matrix4f;
import com.jme.renderer.*;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.*;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * GLSL motion blur pass.
 *
 * @author Rikard Herlitz (MrCoder) - initial implementation
 */
public class MotionBlurRenderPass extends Pass {
	private static final long serialVersionUID = 1L;

	private TextureRenderer tRenderer;
	private Texture mainTexture;

	private AlphaState alphaObj;
	private CullState cullObj;
	private TextureState tsObj;
	private float blurStrength = -0.000035f;

	private GLSLShaderObjectsState motionBlurShader;

	private boolean freeze = false;
	private boolean supported = true;
	private boolean useCurrentScene = false;

	private class DynamicObject {
		public Spatial spatial;
		public Matrix4f modelMatrix = new Matrix4f();
		public Matrix4f modelViewMatrix = new Matrix4f();
		public Matrix4f modelViewProjectionMatrix = new Matrix4f();
	}

	private ArrayList<DynamicObject> dynamicObjects = new ArrayList<DynamicObject>();

	private Matrix4f tmpMatrix = new Matrix4f();
	private Matrix4f projectionMatrix = new Matrix4f();

	private float tpf = 0.0f;
	private Camera cam;

	public void addMotionBlurSpatial( Spatial spatial ) {
		DynamicObject dynamicObject = new DynamicObject();
		dynamicObject.spatial = spatial;
		dynamicObjects.add( dynamicObject );
	}

	/**
	 * Reset motionblur parameters to default
	 */
	public void resetParameters() {
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		super.cleanUp();
		if( tRenderer != null )
			tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new motionblur renderpass
	 *
	 * @param cam		 Camera used for rendering the motionblur source
	 */
	public MotionBlurRenderPass( Camera cam ) {
		this.cam = cam;
		DisplaySystem display = DisplaySystem.getDisplaySystem();

		resetParameters();

		//Create texture renderers and rendertextures(alternating between two not to overwrite pbuffers)
		tRenderer = display.createTextureRenderer(
				display.getWidth(), display.getHeight(), false, true, false, false,
				TextureRenderer.RENDER_TEXTURE_2D, 0 );
		tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
		tRenderer.setCamera( cam );
		tRenderer.forceCopy( true );

		mainTexture = new Texture();
		mainTexture.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
		mainTexture.setFilter( Texture.FM_LINEAR );
		tRenderer.setupTexture( mainTexture );

		//Create extract intensity shader
		motionBlurShader = display.getRenderer().createGLSLShaderObjectsState();
		if( !motionBlurShader.isSupported() ) {
			supported = false;
		}
		else {
			reloadShader();
		}

		tsObj = display.getRenderer().createTextureState();
		tsObj.setEnabled( true );
		tsObj.setTexture( mainTexture, 0 );

		cullObj = display.getRenderer().createCullState();
		cullObj.setEnabled( true );
		cullObj.setCullMode( CullState.CS_BACK );

		alphaObj = display.getRenderer().createAlphaState();
		alphaObj.setEnabled( true );
		alphaObj.setBlendEnabled( true );
		alphaObj.setSrcFunction( AlphaState.SB_SRC_ALPHA );
		alphaObj.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
	}

	public void reloadShader() {
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load( MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.vert" ),
							 MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.frag" ) );
			testShader.apply();
			Util.checkGLError();
		} catch( OpenGLException e ) {
			e.printStackTrace();
			return;
		}

		motionBlurShader.load( MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.vert" ),
							   MotionBlurRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/motionblur.frag" ) );

		motionBlurShader.clearUniforms();
		motionBlurShader.setUniform( "screenTexture", 0 );
		motionBlurShader.setUniform( "prevModelViewMatrix", new Matrix4f(), false );
		motionBlurShader.setUniform( "prevModelViewProjectionMatrix", new Matrix4f(), false );
		motionBlurShader.setUniform( "halfWinSize", DisplaySystem.getDisplaySystem().getWidth() * 0.5f, DisplaySystem.getDisplaySystem().getHeight() * 0.5f );
		motionBlurShader.setUniform( "blurStrength", blurStrength );
		motionBlurShader.apply();

		LoggingSystem.getLogger().log( Level.INFO, "Shader reloaded..." );
	}

	public Texture getMainTexture() {
		return mainTexture;
	}

	/**
	 * Helper class to get all spatials rendered in one TextureRenderer.render() call.
	 */
	private class SpatialsRenderNode extends Node {
		private static final long serialVersionUID = 7367501683137581101L;

		public void draw( Renderer r ) {
			Spatial child;
			for( int i = 0, cSize = spatials.size(); i < cSize; i++ ) {
				child = spatials.get( i );
				if( child != null )
					child.onDraw( r );
			}
		}

		public void onDraw( Renderer r ) {
			draw( r );
		}
	}

	private final SpatialsRenderNode spatialsRenderNode = new SpatialsRenderNode();

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		if ( !freeze ) {
			this.tpf = tpf;
		}
	}

	public void doRender( Renderer r ) {
		if( !useCurrentScene && spatials.size() == 0 ) {
			return;
		}

		// see if we should use the current scene to motionblur, or only things added to the pass.
		if( useCurrentScene ) {
			// grab backbuffer to texture
			tRenderer.copyBufferToTexture( mainTexture,
										   DisplaySystem.getDisplaySystem().getWidth(),
										   DisplaySystem.getDisplaySystem().getHeight(),
										   1 );
		}
		else {
			//Render scene to texture
			tRenderer.updateCamera();
			tRenderer.render( spatialsRenderNode, mainTexture );
		}

		projectionMatrix.set( ((AbstractCamera) cam).getProjectionMatrix() );
		for( int i = 0; i < dynamicObjects.size(); i++ ) {
			DynamicObject dynamicObject = dynamicObjects.get( i );
			Matrix4f modelMatrix = dynamicObject.modelMatrix;
			Matrix4f modelViewMatrix = dynamicObject.modelViewMatrix;
			Matrix4f modelViewProjectionMatrix = dynamicObject.modelViewProjectionMatrix;

			modelViewMatrix.set( modelMatrix );
			modelViewMatrix.multLocal( ((AbstractCamera) cam).getModelViewMatrix() );
			modelViewProjectionMatrix.set( modelViewMatrix ).multLocal( projectionMatrix );
		}

		Renderer.enforceState( motionBlurShader );
		Renderer.enforceState( tsObj );
		Renderer.enforceState( cullObj );

		for( int i = 0; i < dynamicObjects.size(); i++ ) {
			DynamicObject dynamicObject = dynamicObjects.get( i );

			motionBlurShader.setUniform( "prevModelViewMatrix", dynamicObject.modelViewMatrix, false );
			motionBlurShader.setUniform( "prevModelViewProjectionMatrix", dynamicObject.modelViewProjectionMatrix, false );
			motionBlurShader.setUniform( "blurStrength", blurStrength / tpf );
			motionBlurShader.apply();

			r.draw( dynamicObject.spatial );
			r.renderQueue();
		}

		Renderer.clearEnforcedState( RenderState.RS_GLSL_SHADER_OBJECTS );
		Renderer.clearEnforcedState( RenderState.RS_TEXTURE );
		Renderer.clearEnforcedState( RenderState.RS_CULL );

		if( !freeze ) {
			for( int i = 0; i < dynamicObjects.size(); i++ ) {
				DynamicObject dynamicObject = dynamicObjects.get( i );
				Matrix4f modelMatrix = dynamicObject.modelMatrix;
				Spatial spatial = dynamicObject.spatial;

				modelMatrix.loadIdentity();
				spatial.getWorldRotation().toRotationMatrix( tmpMatrix );
				modelMatrix.multLocal( tmpMatrix );
				modelMatrix.m00 *= spatial.getWorldScale().x;
				modelMatrix.m11 *= spatial.getWorldScale().y;
				modelMatrix.m22 *= spatial.getWorldScale().z;
				modelMatrix.setTranslation( spatial.getWorldTranslation() );
				modelMatrix.transpose();
			}
		}
	}

	public boolean useCurrentScene() {
		return useCurrentScene;
	}

	public void setUseCurrentScene( boolean useCurrentScene ) {
		this.useCurrentScene = useCurrentScene;
	}

	public boolean isFreeze() {
		return freeze;
	}

	public void setFreeze( boolean freeze ) {
		this.freeze = freeze;
	}

	public float getBlurStrength() {
		return blurStrength;
	}

	public void setBlurStrength( float blurStrength ) {
		this.blurStrength = blurStrength;
	}
}
