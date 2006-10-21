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

package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureKey;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.Converters.MilkToJme;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.ProceduralSplatTextureGenerator;
import jmetest.renderer.loader.TestMilkJmeWrite;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;

/**
 * <code>TestProjectedTexture</code>
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestProjectedTexture extends SimpleGame {
	TerrainPage terrain;
	Texture projectedTexture;

	Node projectorModel;
	Vector3f projectorAim = new Vector3f();

	Matrix4f textureMatrix = new Matrix4f();
	Matrix4f lightProjectionMatrix = new Matrix4f();
	Matrix4f lightViewMatrix = new Matrix4f();
	Matrix4f biasMatrix = new Matrix4f(
			0.5f, 0.0f, 0.0f, 0.0f,
			0.0f, 0.5f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.5f, 0.0f,
			0.5f, 0.5f, 0.5f, 1.0f
	); //bias from [-1, 1] to [0, 1]

	public static void main( String[] args ) {
		TestProjectedTexture app = new TestProjectedTexture();
		app.setDialogBehaviour( FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG );
		app.start();
	}

	protected void simpleUpdate() {
		//make a funny projector animation
		projectorModel.getLocalTranslation().set( FastMath.sin( timer.getTimeInSeconds() ) * (FastMath.sin( timer.getTimeInSeconds() * 0.7f ) * 15.0f + 20.0f),
												  FastMath.sin( timer.getTimeInSeconds() * 0.5f ) * 10.0f + 20.0f,
												  FastMath.cos( timer.getTimeInSeconds() * 1.2f ) * (FastMath.sin( timer.getTimeInSeconds() ) * 15.0f + 20.0f) );
		projectorAim.set( FastMath.sin( timer.getTimeInSeconds() * 0.8f - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() * 0.5f - FastMath.PI ) * 10.0f + 10.0f),
						  0.0f,
						  FastMath.cos( timer.getTimeInSeconds() * 0.6f - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() * 0.3f - FastMath.PI ) * 10.0f + 10.0f) );
		projectorModel.lookAt( projectorAim, Vector3f.UNIT_Y );
		projectorModel.updateGeometricState( 0.0f, true );

		//create texture matrix
		matrixPerspective( 30.0f, 1.0f, 1.0f, 1000.0f, lightProjectionMatrix );
		matrixLookAt( projectorModel.getLocalTranslation(), projectorAim, lightViewMatrix );
		textureMatrix.set( lightViewMatrix.mult( lightProjectionMatrix ).multLocal( biasMatrix ) );
	}

	protected void simpleInitGame() {
		try {
			display.setTitle( "Projected Texture Test" );

			cam.getLocation().set( new Vector3f( 50, 50, 0 ) );
			cam.lookAt( new Vector3f(), Vector3f.UNIT_Y );

			CullState cs = display.getRenderer().createCullState();
			cs.setCullMode( CullState.CS_BACK );
			cs.setEnabled( true );

			//load projector model
			MilkToJme converter2 = new MilkToJme();
			URL MSFile2 = TestMilkJmeWrite.class.getClassLoader().getResource(
					"jmetest/data/model/msascii/camera.ms3d" );
			ByteArrayOutputStream BO2 = new ByteArrayOutputStream();

			try {
				converter2.convert( MSFile2.openStream(), BO2 );
			} catch( IOException e ) {
				System.out.println( "IO problem writting the file!!!" );
				System.out.println( e.getMessage() );
				System.exit( 0 );
			}
			URL TEXdir2 = TestMilkJmeWrite.class.getClassLoader().getResource(
					"jmetest/data/model/msascii/" );
			projectorModel = null;
			try {
				TextureKey.setOverridingLocation( TEXdir2 );
				projectorModel = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( BO2.toByteArray() ) );
			} catch( IOException e ) {
				System.out.println( "darn exceptions:" + e.getMessage() );
			}
			rootNode.attachChild( projectorModel );

			//create terrain
			URL grayScale = TestProjectedTexture.class.getClassLoader().getResource( "jmetest/data/texture/terrain.png" );
			ImageBasedHeightMap heightMap = new ImageBasedHeightMap( new ImageIcon( grayScale ).getImage() );
			Vector3f terrainScale = new Vector3f( 5, 0.25f, 5 );
			terrain = new TerrainPage( "image icon", 33, (heightMap.getSize()) + 1, new Vector3f( .5f, .05f, .5f ), heightMap.getHeightMap(), false );
			terrain.setDetailTexture( 1, 16 );
			terrain.setModelBound( new BoundingBox() );
			terrain.updateModelBound();
			terrain.setLocalTranslation( new Vector3f( 0, 0, 0 ) );
			rootNode.attachChild( terrain );
			rootNode.setRenderState( cs );

			ProceduralSplatTextureGenerator pst = new ProceduralSplatTextureGenerator( heightMap );
			pst.addTexture( new ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/grassb.png" ) ), -128, 0, 128 );
			pst.addTexture( new ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/dirt.jpg" ) ), 0, 128, 255 );
			pst.addTexture( new ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/highest.jpg" ) ), 128, 255, 384 );

			pst.addSplatTexture( new ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/terrainTex.png" ) ), new ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/water.png" ) ) );
			pst.createTexture( 512 );

			TextureState ts = display.getRenderer().createTextureState();
			ts.setEnabled( true );
			Texture t1 = TextureManager.loadTexture( pst.getImageIcon().getImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true );
			ts.setTexture( t1, 0 );

			projectedTexture = TextureManager.loadTexture( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.png" ), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR );
			ts.setTexture( projectedTexture, 1 );

			//this is were we set the texture up for projection
			projectedTexture.setMatrix( textureMatrix );
			projectedTexture.setEnvironmentalMapMode( Texture.EM_EYE_LINEAR );
			projectedTexture.setWrap( Texture.WM_BCLAMP_S_BCLAMP_T );

			//make the projected texture blend with the terrain texture
			t1.setApply( Texture.AM_COMBINE );
			t1.setCombineFuncRGB( Texture.ACF_MODULATE );
			t1.setCombineSrc0RGB( Texture.ACS_TEXTURE );
			t1.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
			t1.setCombineSrc1RGB( Texture.ACS_PRIMARY_COLOR );
			t1.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
			t1.setCombineScaleRGB( 1.0f );

			projectedTexture.setApply( Texture.AM_COMBINE );
			projectedTexture.setCombineFuncRGB( Texture.ACF_ADD );
			projectedTexture.setCombineSrc0RGB( Texture.ACS_TEXTURE );
			projectedTexture.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
			projectedTexture.setCombineSrc1RGB( Texture.ACS_PREVIOUS );
			projectedTexture.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
			projectedTexture.setCombineScaleRGB( 1.0f );

			rootNode.setRenderState( ts );

			rootNode.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_OPAQUE );
			fpsNode.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_OPAQUE );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	//UTILS
	private static final FloatBuffer tmp_FloatBuffer = org.lwjgl.BufferUtils.createFloatBuffer( 16 );
	private Vector3f localDir = new Vector3f();
	private Vector3f localLeft = new Vector3f();
	private Vector3f localUp = new Vector3f();
	private Vector3f tmpVec = new Vector3f();

	private void matrixLookAt( Vector3f location, Vector3f at, Matrix4f result ) {
		localDir.set( at ).subtractLocal( location ).normalizeLocal();
		localDir.cross( Vector3f.UNIT_Y, localLeft );
		localLeft.cross( localDir, localUp );

		GL11.glPushMatrix();

		// set view matrix
		GL11.glMatrixMode( GL11.GL_MODELVIEW );
		GL11.glLoadIdentity();
		GLU.gluLookAt(
				location.x,
				location.y,
				location.z,
				at.x,
				at.y,
				at.z,
				localUp.x,
				localUp.y,
				localUp.z );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
	}

	private void matrixPerspective( float fovY, float aspect, float near, float far, Matrix4f result ) {
		GL11.glPushMatrix();

		// set view matrix
		GL11.glMatrixMode( GL11.GL_MODELVIEW );
		GL11.glLoadIdentity();
		GLU.gluPerspective( fovY, aspect, near, far );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
	}

	private void matrixProjection( float fovY, float aspect, float near, float far, Matrix4f result ) {
		float h = FastMath.tan( fovY * FastMath.DEG_TO_RAD ) * near * .5f;
		float w = h * aspect;
		float frustumLeft = -w;
		float frustumRight = w;
		float frustumBottom = -h;
		float frustumTop = h;
		float frustumNear = near;
		float frustumFar = far;

		GL11.glPushMatrix();

		GL11.glMatrixMode( GL11.GL_PROJECTION );
		GL11.glLoadIdentity();
		GL11.glFrustum(
				frustumLeft,
				frustumRight,
				frustumBottom,
				frustumTop,
				frustumNear,
				frustumFar );

		if( result != null ) {
			tmp_FloatBuffer.rewind();
			GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, tmp_FloatBuffer );
			tmp_FloatBuffer.rewind();
			result.readFloatBuffer( tmp_FloatBuffer );
		}

		GL11.glPopMatrix();
	}
}
