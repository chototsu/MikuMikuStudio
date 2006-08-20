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

package com.jmex.effects.water;

import java.util.logging.Level;

import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.Util;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.pass.Pass;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;

/**
 * <code>WaterRenderPass</code>
 * Water effect pass.
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class WaterRenderPass extends Pass {
	private Camera cam;
	private LightState lightState;
	private float tpf;

	private TextureRenderer tRenderer;
	private Texture textureReflect;
	private Texture textureRefract;
	private Texture textureDepth;

	private Node renderNode;
	private Node skyBox;

	private GLSLShaderObjectsState waterShader;
	private CullState cullBackFace;
	private TextureState ts;
	private AlphaState as1;
	private ClipState clipState;

	private Plane waterPlane;
	private Vector3f tangent;
	private Vector3f binormal;
	private float clipBias;
	private ColorRGBA waterColorStart;
	private ColorRGBA waterColorEnd;
	private float heightFalloffStart;
	private float heightFalloffSpeed;
	private float waterMaxAmplitude;
	private float speedReflection;
	private float speedRefraction;

	private boolean aboveWater;
	private float normalTranslation = 0.0f;
	private float refractionTranslation = 0.0f;
	private boolean supported = true;
	private boolean useProjectedShader = false;
	private boolean useRefraction = false;

	private final String simpleShaderStr = "com/jmex/effects/water/data/flatwatershader";
	private final String simpleShaderRefractionStr = "com/jmex/effects/water/data/flatwatershader_refraction";
	private final String projectedShaderStr = "com/jmex/effects/water/data/projectedwatershader";
	private final String projectedShaderRefractionStr = "com/jmex/effects/water/data/projectedwatershader_refraction";
	private String currentShaderStr;

	public void resetParameters() {
		waterPlane = new Plane( new Vector3f( 0.0f, 1.0f, 0.0f ), 0.0f );
		tangent = new Vector3f( 1.0f, 0.0f, 0.0f );
		binormal = new Vector3f( 0.0f, 0.0f, 1.0f );

		waterMaxAmplitude = 0.0f;
		clipBias = 0.0f;
		waterColorStart = new ColorRGBA( 0.0f, 0.0f, 0.1f, 1.0f );
		waterColorEnd = new ColorRGBA( 0.0f, 0.3f, 0.1f, 1.0f );
		heightFalloffStart = 300.0f;
		heightFalloffSpeed = 500.0f;
		speedReflection = 0.1f;
		speedRefraction = -0.05f;
	}

	/**
	 * Release pbuffers in TextureRenderer's. Preferably called from user cleanup method.
	 */
	public void cleanup() {
		if( isSupported() )
			tRenderer.cleanup();
	}

	public boolean isSupported() {
		return supported;
	}

	/**
	 * Creates a new WaterRenderPass
	 *
	 * @param cam				main rendercam to use for reflection settings etc
	 * @param renderScale		how many times smaller the reflection/refraction textures should be compared to the main display
	 * @param useProjectedShader true - use the projected setup for variable height water meshes, false - use the flast shader setup
	 * @param useRefraction	  enable/disable rendering of refraction textures
	 */
	public WaterRenderPass( Camera cam, int renderScale, boolean useProjectedShader, boolean useRefraction ) {
		this.cam = cam;
		this.useProjectedShader = useProjectedShader;
		this.useRefraction = useRefraction;

		if( useRefraction && useProjectedShader && TextureState.getNumberOfFragmentUnits() < 6 ||
			useRefraction && TextureState.getNumberOfFragmentUnits() < 5 ) {
			useRefraction = false;
			LoggingSystem.getLogger().log( Level.INFO, "Not enough textureunits, falling back to non refraction water" );
		}

		resetParameters();

		DisplaySystem display = DisplaySystem.getDisplaySystem();

		waterShader = display.getRenderer().createGLSLShaderObjectsState();

		if( !waterShader.isSupported() ) {
			supported = false;
		}
		else {
		}

		cullBackFace = display.getRenderer().createCullState();
		cullBackFace.setEnabled( true );
		cullBackFace.setCullMode( CullState.CS_NONE );
		clipState = display.getRenderer().createClipState();
		if( isSupported() ) {
			tRenderer = display.createTextureRenderer(
					display.getWidth() / renderScale, display.getHeight() / renderScale, false, true, false, false,
					TextureRenderer.RENDER_TEXTURE_2D, 0 );

			if( tRenderer.isSupported() ) {
				tRenderer.setBackgroundColor( new ColorRGBA( 0.0f, 0.0f, 0.0f, 1.0f ) );
				tRenderer.getCamera().setFrustum( cam.getFrustumNear(), cam.getFrustumFar(), cam.getFrustumLeft(), cam.getFrustumRight(), cam.getFrustumTop(), cam.getFrustumBottom() );
				tRenderer.forceCopy( true );

				textureReflect = new Texture();
				textureReflect.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
				textureReflect.setFilter( Texture.FM_LINEAR );
				textureReflect.setScale( new Vector3f( -1.0f, 1.0f, 1.0f ) );
				textureReflect.setTranslation( new Vector3f( 1.0f, 0.0f, 0.0f ) );
				tRenderer.setupTexture( textureReflect );

				if( useRefraction ) {
					textureRefract = new Texture();
					textureRefract.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
					textureRefract.setFilter( Texture.FM_LINEAR );
					tRenderer.setupTexture( textureRefract );

					textureDepth = new Texture();
					textureDepth.setWrap( Texture.WM_ECLAMP_S_ECLAMP_T );
					textureDepth.setFilter( Texture.FM_NEAREST );
					textureDepth.setRTTSource( Texture.RTT_SOURCE_DEPTH );
					tRenderer.setupTexture( textureDepth );
				}

				ts = display.getRenderer().createTextureState();
				ts.setEnabled( true );

				Texture t1 = TextureManager.loadTexture(
//						WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/normalmap3.dds" ),
						WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/normalmap.png" ),
//						WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/fisk.png" ),
						Texture.MM_LINEAR_LINEAR,
						Texture.FM_LINEAR, com.jme.image.Image.GUESS_FORMAT_NO_S3TC, 1.0f, false
//						Texture.FM_NEAREST
				);
				ts.setTexture( t1, 0 );
				t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

				ts.setTexture( textureReflect, 1 );

				t1 = TextureManager.loadTexture(
						WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/dudvmap.png" ),
						Texture.MM_LINEAR_LINEAR,
						Texture.FM_LINEAR, com.jme.image.Image.GUESS_FORMAT_NO_S3TC, 1.0f, false
				);
				ts.setTexture( t1, 2 );
				t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

				if( useRefraction ) {
					ts.setTexture( textureRefract, 3 );
					ts.setTexture( textureDepth, 4 );
				}

				if( useProjectedShader ) {
					t1 = TextureManager.loadTexture(
							WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/oceanfoam.png" ),
							Texture.MM_LINEAR_LINEAR,
							Texture.FM_LINEAR );
					if( useRefraction ) {
						ts.setTexture( t1, 5 );
					}
					else {
						ts.setTexture( t1, 3 );
					}
					t1.setWrap( Texture.WM_WRAP_S_WRAP_T );
				}

				clipState.setEnabled( true );
				clipState.setEnableClipPlane( ClipState.CLIP_PLANE0, true );

				if( useProjectedShader ) {
					if( useRefraction ) {
						currentShaderStr = projectedShaderRefractionStr;
					}
					else {
						currentShaderStr = projectedShaderStr;
					}
				}
				else {
					if( useRefraction ) {
						currentShaderStr = simpleShaderRefractionStr;
					}
					else {
						currentShaderStr = simpleShaderStr;
					}
				}

				waterShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
								  WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );
				waterShader.setEnabled( true );
			}
			else {
				supported = false;
			}
		}

		if( !isSupported() ) {
			ts = display.getRenderer().createTextureState();
			ts.setEnabled( true );

			Texture t1 = TextureManager.loadTexture(
					WaterRenderPass.class.getClassLoader().getResource( "com/jmex/effects/water/data/water2.png" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			ts.setTexture( t1, 0 );
			t1.setWrap( Texture.WM_WRAP_S_WRAP_T );

			as1 = display.getRenderer().createAlphaState();
			as1.setBlendEnabled( true );
			as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
			as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
			as1.setEnabled( true );
		}
	}

	@Override
	protected void doUpdate( float tpf ) {
		super.doUpdate( tpf );
		this.tpf = tpf;
	}


	public void doRender( Renderer r ) {
		normalTranslation += speedReflection * tpf;
		refractionTranslation += speedRefraction * tpf;

		float camWaterDist = waterPlane.pseudoDistance( cam.getLocation() );
		aboveWater = camWaterDist >= 0;

		if( isSupported() ) {
			waterShader.clearUniforms();
			waterShader.setUniform( "tangent", tangent.x, tangent.y, tangent.z );
			waterShader.setUniform( "binormal", binormal.x, binormal.y, binormal.z );

			waterShader.setUniform( "normalMap", 0 );
			waterShader.setUniform( "reflection", 1 );
			waterShader.setUniform( "dudvMap", 2 );
			if( useRefraction ) {
				waterShader.setUniform( "refraction", 3 );
				waterShader.setUniform( "depthMap", 4 );
			}

			waterShader.setUniform( "waterColor", waterColorStart.r, waterColorStart.g, waterColorStart.b, waterColorStart.a );
			waterShader.setUniform( "waterColorEnd", waterColorEnd.r, waterColorEnd.g, waterColorEnd.b, waterColorEnd.a );
			waterShader.setUniform( "normalTranslation", normalTranslation );
			waterShader.setUniform( "refractionTranslation", refractionTranslation );
			waterShader.setUniform( "abovewater", aboveWater ? 1 : 0 );
			if( useProjectedShader ) {
				waterShader.setUniform( "cameraPos", cam.getLocation().x, cam.getLocation().y, cam.getLocation().z );
				if( useRefraction ) {
					waterShader.setUniform( "foamMap", 5 );
				}
				else {
					waterShader.setUniform( "foamMap", 3 );
				}
				waterShader.setUniform( "waterHeight", waterPlane.getConstant() );
				waterShader.setUniform( "amplitude", waterMaxAmplitude );
				waterShader.setUniform( "heightFalloffStart", heightFalloffStart );
				waterShader.setUniform( "heightFalloffSpeed", heightFalloffSpeed );
			}
			waterShader.apply();

			float heightTotal = clipBias + waterMaxAmplitude - waterPlane.getConstant();
			Vector3f normal = waterPlane.getNormal();
			clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, normal.x, normal.y, normal.z, heightTotal );
			clipState.setEnabled( true );

			renderReflection();

			clipState.setClipPlaneEquation( ClipState.CLIP_PLANE0, -normal.x, -normal.y, -normal.z, -heightTotal );

			if( useRefraction && aboveWater ) {
				renderRefraction();
			}

			clipState.setEnabled( false );
		}
		else {
			ts.getTexture().setTranslation( new Vector3f( 0, normalTranslation, 0 ) );
		}
	}

	public void reloadShader() {
		GLSLShaderObjectsState testShader = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
		try {
			testShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
							 WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );
			testShader.apply();
			Util.checkGLError();
		} catch( OpenGLException e ) {
			e.printStackTrace();
			return;
		}

		waterShader.load( WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".vert" ),
						  WaterRenderPass.class.getClassLoader().getResource( currentShaderStr + ".frag" ) );

		LoggingSystem.getLogger().log( Level.INFO, "Shader reloaded..." );
	}

	public void setWaterEffectOnSpatial( Spatial spatial ) {
		spatial.setRenderState( cullBackFace );
		if( isSupported() ) {
			spatial.setRenderQueueMode( Renderer.QUEUE_SKIP );
			spatial.setRenderState( waterShader );
			spatial.setRenderState( ts );
		}
		else {
			spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
			spatial.setLightCombineMode( LightState.OFF );
			spatial.setRenderState( ts );
			spatial.setRenderState( as1 );
		}
		spatial.updateRenderState();
	}

	//temporary vectors for mem opt.
	private Vector3f tmpLocation = new Vector3f();
	private Vector3f camReflectPos = new Vector3f();
	private Vector3f camReflectDir = new Vector3f();
	private Vector3f camReflectUp = new Vector3f();
	private Vector3f camReflectLeft = new Vector3f();
	private Vector3f camLocation = new Vector3f();

	private void renderReflection() {
		if( aboveWater ) {
			camLocation.set( cam.getLocation() );
			float planeDistance = waterPlane.pseudoDistance( camLocation );
			camReflectPos.set( camLocation.subtractLocal( waterPlane.getNormal().mult( planeDistance * 2.0f ) ) );

			camLocation.set( cam.getLocation() ).addLocal( cam.getDirection() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
			camReflectDir.set( camLocation.subtractLocal( waterPlane.getNormal().mult( planeDistance * 2.0f ) ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camLocation.set( cam.getLocation() ).addLocal( cam.getUp() );
			planeDistance = waterPlane.pseudoDistance( camLocation );
			camReflectUp.set( camLocation.subtractLocal( waterPlane.getNormal().mult( planeDistance * 2.0f ) ) ).subtractLocal( camReflectPos ).normalizeLocal();

			camReflectLeft.set( camReflectDir ).crossLocal( camReflectUp ).normalizeLocal();

			tRenderer.getCamera().getLocation().set( camReflectPos );
			tRenderer.getCamera().getDirection().set( camReflectDir );
			tRenderer.getCamera().getUp().set( camReflectUp );
			tRenderer.getCamera().getLeft().set( camReflectLeft );
		}
		else {
			tRenderer.getCamera().getLocation().set( cam.getLocation() );
			tRenderer.getCamera().getDirection().set( cam.getDirection() );
			tRenderer.getCamera().getUp().set( cam.getUp() );
			tRenderer.getCamera().getLeft().set( cam.getLeft() );
		}

		tRenderer.updateCamera();

		tmpLocation.set( skyBox.getLocalTranslation() );
		skyBox.getLocalTranslation().set( tRenderer.getCamera().getLocation() );
		skyBox.updateWorldData( 0.0f );

		tRenderer.render( renderNode, textureReflect );

		skyBox.getLocalTranslation().set( tmpLocation );
		skyBox.updateWorldData( 0.0f );
	}

	private void renderRefraction() {
		tRenderer.getCamera().getLocation().set( cam.getLocation() );
		tRenderer.getCamera().getDirection().set( cam.getDirection() );
		tRenderer.getCamera().getUp().set( cam.getUp() );
		tRenderer.getCamera().getLeft().set( cam.getLeft() );

		tRenderer.updateCamera();

		int cullMode = skyBox.getCullMode();
		skyBox.setCullMode( SceneElement.CULL_ALWAYS );

		tRenderer.render( renderNode, textureRefract, textureDepth );

		skyBox.setCullMode( cullMode );
	}

	public void setReflectedScene( Node renderNode ) {
		this.renderNode = renderNode;
		renderNode.setRenderState( clipState );
		renderNode.updateRenderState();
	}

	public void setSkybox( Node skyBox ) {
		ClipState skyboxClipState = DisplaySystem.getDisplaySystem().getRenderer().createClipState();
		skyboxClipState.setEnabled( false );
		skyBox.setRenderState( skyboxClipState );
		skyBox.updateRenderState();

		this.skyBox = skyBox;
	}

	private void generateTangentVectors() {
		waterPlane.getNormal().normalizeLocal();

		float ax = waterPlane.getNormal().x * FastMath.rand.nextFloat();
		float by = waterPlane.getNormal().y * FastMath.rand.nextFloat();
		float cz = waterPlane.getNormal().z * FastMath.rand.nextFloat();
		float d = waterPlane.getConstant();

		float x = -by - cz - d;
		float y = -ax - cz - d;
		float z = -ax - by - d;

		tangent = new Vector3f( x, y, z );
		tangent.normalizeLocal();

		binormal = tangent.cross( waterPlane.getNormal() );
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam( Camera cam ) {
		this.cam = cam;
	}

	public ColorRGBA getWaterColorStart() {
		return waterColorStart;
	}

	public void setWaterColorStart( ColorRGBA waterColorStart ) {
		this.waterColorStart = waterColorStart;
	}

	public ColorRGBA getWaterColorEnd() {
		return waterColorEnd;
	}

	public void setWaterColorEnd( ColorRGBA waterColorEnd ) {
		this.waterColorEnd = waterColorEnd;
	}

	public float getHeightFalloffStart() {
		return heightFalloffStart;
	}

	public void setHeightFalloffStart( float heightFalloffStart ) {
		this.heightFalloffStart = heightFalloffStart;
	}

	public float getHeightFalloffSpeed() {
		return heightFalloffSpeed;
	}

	public void setHeightFalloffSpeed( float heightFalloffSpeed ) {
		this.heightFalloffSpeed = heightFalloffSpeed;
	}

	public float getWaterHeight() {
		return waterPlane.getConstant();
	}

	public void setWaterHeight( float waterHeight ) {
		this.waterPlane.setConstant( waterHeight );
	}

	public Vector3f getNormal() {
		return waterPlane.getNormal();
	}

	public void setNormal( Vector3f normal ) {
		waterPlane.setNormal( normal );
	}

	public float getSpeedReflection() {
		return speedReflection;
	}

	public void setSpeedReflection( float speedReflection ) {
		this.speedReflection = speedReflection;
	}

	public float getSpeedRefraction() {
		return speedRefraction;
	}

	public void setSpeedRefraction( float speedRefraction ) {
		this.speedRefraction = speedRefraction;
	}

	public float getWaterMaxAmplitude() {
		return waterMaxAmplitude;
	}

	public void setWaterMaxAmplitude( float waterMaxAmplitude ) {
		this.waterMaxAmplitude = waterMaxAmplitude;
	}

	public float getClipBias() {
		return clipBias;
	}

	public void setClipBias( float clipBias ) {
		this.clipBias = clipBias;
	}

	public Plane getWaterPlane() {
		return waterPlane;
	}

	public void setWaterPlane( Plane waterPlane ) {
		this.waterPlane = waterPlane;
	}

	public Vector3f getTangent() {
		return tangent;
	}

	public void setTangent( Vector3f tangent ) {
		this.tangent = tangent;
	}

	public Vector3f getBinormal() {
		return binormal;
	}

	public void setBinormal( Vector3f binormal ) {
		this.binormal = binormal;
	}

	public Texture getTextureReflect() {
		return textureReflect;
	}

	public Texture getTextureRefract() {
		return textureRefract;
	}

	public Texture getTextureDepth() {
		return textureDepth;
	}
}
