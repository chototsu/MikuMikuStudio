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

package com.jmex.effects.water;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.*;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import java.nio.FloatBuffer;

/**
 * Simple "reflection only" version of the water effect pass.
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class SimpleWaterRenderPass extends Pass {
	private Camera cam;

	private TextureRenderer tRendererReflect;
	private Texture textureReflect;

	private float tpf;
	private Node renderNode;
	private Geometry waterQuad;
	private Node skyBox;

	private GLSLShaderObjectsState waterShader;

	private Vector3f waterColor;
	private Vector3f lightPosition;
	private float normalTranslation = 0.0f;
	private float refractionTranslation = 0.0f;
	private boolean supported = true;

	private float waterSize = 10000.0f;
	private float waterHeight = 0.0f;
	private float speedReflection = 0.3f;
	private float speedRefraction = 0.3f;
	private float scaleReflection = 0.3f;
	private float scaleRefraction = 0.3f;

//	private SimpleTerraView terraView;
	private boolean useLod;

	private ClipState clipState;

	/**
	 * Reset bloom parameters to default
	 */
	public void resetParameters() {
		waterColor = new Vector3f( 0.0f, 0.0f, 0.0f );
		lightPosition = new Vector3f( 1000, 100, 1000 );
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		if( isSupported() )
			tRendererReflect.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	public SimpleWaterRenderPass( Camera cam, int renderScale, boolean advanced ) {
		this.cam = cam;

		resetParameters();

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		waterShader = display.getRenderer().createGLSLShaderObjectsState();

		// Check is GLSL is supported on current hardware.
		if( !waterShader.isSupported() || !advanced ) {
			supported = false;
		}
		else {
			waterShader.load( SimpleWaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/simplewatershader.vert" ),
							  SimpleWaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/glsl/data/simplewatershader.frag" ) );
			waterShader.setEnabled( true );

			waterShader.clearUniforms();
			waterShader.setUniform( "normalMap", 0 );
			waterShader.setUniform( "reflection", 1 );
			waterShader.setUniform( "dudvMap", 2 );
			waterShader.setUniform( "waterColor", waterColor.x, waterColor.y, waterColor.z, 1.0f );
			waterShader.setUniform( "cameraPos", cam.getLocation().x, cam.getLocation().y, cam.getLocation().z, 1.0f );
//			waterShader.setUniform("lightPos", lightPosition.x, lightPosition.y, lightPosition.z, 1.0f);
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", 1 );
			waterShader.apply();
		}

		waterQuad = new Quad( "waterQuad", 1, 1 );
		waterQuad.copyTextureCoords( 0, 0, 1 );

		CullState cullBackFace = display.getRenderer().createCullState();
		cullBackFace.setEnabled( true );
		cullBackFace.setCullMode( CullState.CS_NONE );
		waterQuad.setRenderState( cullBackFace );

		clipState = display.getRenderer().createClipState();

		if( isSupported() ) {
			waterQuad.setRenderQueueMode( Renderer.QUEUE_SKIP );
			waterQuad.setRenderState( waterShader );

			tRendererReflect = display.createTextureRenderer(
					            display.getWidth() / renderScale, 
                                display.getHeight() / renderScale,
                                TextureRenderer.RENDER_TEXTURE_2D);
			tRendererReflect.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
//			tRendererReflect.getCamera().setFrustumPerspective(55.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 4000);
			tRendererReflect.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );

			textureReflect = new Texture();
			textureReflect.setWrap( Texture.WM_CLAMP_S_CLAMP_T );
			textureReflect.setFilter( Texture.FM_LINEAR );
			tRendererReflect.setupTexture( textureReflect );

			TextureState ts = display.getRenderer().createTextureState();
			ts.setEnabled( true );

			Texture t1 = TextureManager.loadTexture(
					SimpleWaterRenderPass.class.getClassLoader().getResource( "data/normalmap3.dds" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			ts.setTexture( t1, 0 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			ts.setTexture( textureReflect, 1 );

			t1 = TextureManager.loadTexture(
					SimpleWaterRenderPass.class.getClassLoader().getResource( "data/dudvmap.png" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			ts.setTexture( t1, 2 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			waterQuad.setRenderState( ts );
			waterQuad.updateRenderState();

			clipState.setEnabled( true );
			clipState.setEnableClipPlane( ClipState.CLIP_PLANE0, true );
		}
		else {
			waterQuad.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
			waterQuad.setLightCombineMode( LightState.OFF );

			TextureState ts = display.getRenderer().createTextureState();
			ts.setEnabled( true );

			Texture t1 = TextureManager.loadTexture(
					SimpleWaterRenderPass.class.getClassLoader().getResource( "data/water2.png" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			ts.setTexture( t1, 0 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			t1 = TextureManager.loadTexture(
					SimpleWaterRenderPass.class.getClassLoader().getResource( "data/water2.png" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			ts.setTexture( t1, 1 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			waterQuad.setRenderState( ts );

			AlphaState as1 = display.getRenderer().createAlphaState();
			as1.setBlendEnabled( true );
			as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
			as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
			as1.setEnabled( true );
			waterQuad.setRenderState( as1 );

			waterQuad.updateRenderState();

		}
	}

	public void doRender( Renderer r ) {
		DisplaySystem display = DisplaySystem.getDisplaySystem();

		normalTranslation += speedReflection * tpf;
		refractionTranslation -= speedRefraction * tpf;

		Vector3f transVec = new Vector3f( cam.getLocation().x, waterHeight, cam.getLocation().z );

		if( isSupported() ) {
			//refraction coords
			setTextureCoords( 0, transVec.x, -transVec.z, scaleRefraction );

			//normal coords
			setTextureCoords( 1, transVec.x, -transVec.z, scaleReflection );
		}
		else {
			//refraction coords
			setTextureCoords( 0, transVec.x, -transVec.z + refractionTranslation * 100.0f, scaleRefraction * 0.25f );

			//normal coords
			setTextureCoords( 1, transVec.x, -transVec.z + normalTranslation * 100.0f, scaleReflection * 0.25f );
		}

		//vertex coords
		setVertexCoords( transVec.x, transVec.y, transVec.z );

		if( isSupported() ) {
			waterShader.clearUniforms();
			waterShader.setUniform( "normalMap", 0 );
			waterShader.setUniform( "reflection", 1 );
			waterShader.setUniform( "dudvMap", 2 );
			waterShader.setUniform( "waterColor", waterColor.x, waterColor.y, waterColor.z, 1.0f );
			waterShader.setUniform( "cameraPos", cam.getLocation().x, cam.getLocation().y, cam.getLocation().z, 1.0f );
//			waterShader.setUniform("lightPos", lightPosition.x, lightPosition.y, lightPosition.z, 1.0f);
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", cam.getLocation().y > waterHeight ? 1 : 0 );
			waterShader.apply();

			if( cam.getLocation().y > waterHeight )
				clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, 0, 1, 0, -waterHeight + 0.5 );
			else {
				clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, 0, 1, 0, -waterHeight + 0.5 );
				ClipState skyClip = (ClipState) skyBox.getRenderState( RenderState.RS_CLIP );
				skyClip.setClipPlaneEquation( ClipState.CLIP_PLANE0, 0, 1, 0, -waterHeight + 0.5 );
			}

			renderReflection();

			if( cam.getLocation().y > waterHeight )
				clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, 0, 1, 0, -waterHeight + 0.5 );
			else
				clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, 0, -1, 0, waterHeight + 0.5 );
		}
	}

	Vector3f save = new Vector3f();
	final Vector3f invUp = new Vector3f( 0.0f, -1.0f, 0.0f );

	private void renderReflection() {
		if( cam.getLocation().y > waterHeight ) {
			tRendererReflect.getCamera().getLocation().set( cam.getLocation().x, -cam.getLocation().y + waterHeight * 2, cam.getLocation().z );
			tRendererReflect.getCamera().getDirection().set( cam.getDirection().x, -cam.getDirection().y, cam.getDirection().z );

			Vector3f dir = tRendererReflect.getCamera().getDirection();
			Vector3f newLeft = invUp.cross( dir ).normalizeLocal();
			Vector3f newUp = dir.cross( newLeft ).normalizeLocal();

			tRendererReflect.getCamera().getUp().set( newUp );
			tRendererReflect.getCamera().getLeft().set( newLeft );
		}
		else {
			tRendererReflect.getCamera().getLocation().set( cam.getLocation() );
			tRendererReflect.getCamera().getDirection().set( cam.getDirection() );
			tRendererReflect.getCamera().getUp().set( cam.getUp() );
			tRendererReflect.getCamera().getLeft().set( cam.getLeft() );
		}
		tRendererReflect.updateCamera();

		if( useLod ) {
//			terraView.setRenderingWater(true);
		}

		tRendererReflect.render( renderNode, textureReflect );

		if( useLod ) {
//			terraView.setRenderingWater(false);
		}
	}

	public Spatial getWaterSurface() {
		return waterQuad;
	}

	private void setVertexCoords( float x, float y, float z ) {
		FloatBuffer vertBuf = waterQuad.getVertexBuffer( 0 );
		vertBuf.clear();

		vertBuf.put( x - waterSize ).put( y ).put( z - waterSize );
		vertBuf.put( x - waterSize ).put( y ).put( z + waterSize );
		vertBuf.put( x + waterSize ).put( y ).put( z + waterSize );
		vertBuf.put( x + waterSize ).put( y ).put( z - waterSize );
	}

	private void setTextureCoords( int buffer, float x, float y, float textureScale ) {
		x *= textureScale * 0.5f;
		y *= textureScale * 0.5f;
		textureScale = waterSize * textureScale;
		FloatBuffer texBuf;
		texBuf = waterQuad.getTextureBuffer( 0, buffer );
		texBuf.clear();
		texBuf.put( x ).put( textureScale + y );
		texBuf.put( x ).put( y );
		texBuf.put( textureScale + x ).put( y );
		texBuf.put( textureScale + x ).put( textureScale + y );
	}

	public void setScene( Node renderNode ) {
		this.renderNode = renderNode;
		renderNode.setRenderState( clipState );
		renderNode.updateRenderState();
	}

	public void setSkybox( Node skyBox ) {
		this.skyBox = skyBox;
	}

	public float getWaterHeight() {
		return waterHeight;
	}

	public Vector3f getLightPosition() {
		return lightPosition;
	}

	public void setLightPosition( Vector3f lightPosition ) {
		this.lightPosition = lightPosition;
	}

	public Vector3f getWaterColor() {
		return waterColor;
	}

	public void setWaterColor( Vector3f waterColor ) {
		this.waterColor = waterColor;
	}

//	public void setTerraView(SimpleTerraView terraView) {
//		this.terraView = terraView;
//		this.useLod = true;
//	}

	public GLSLShaderObjectsState getWaterShader() {
		return waterShader;
	}
}
