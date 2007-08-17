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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.effects.ProjectedTextureUtil;
import com.jmex.model.converters.MilkToJme;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.ProceduralSplatTextureGenerator;

/**
 * <code>TestProjectedTexture</code>
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestProjectedTexture extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestProjectedTexture.class.getName());
    
	private TerrainPage terrain;

	private Texture projectedTexture1;
	private Node projectorModel1;
	private Vector3f projectorAim1 = new Vector3f();

	private Texture projectedTexture2;
	private Box projectorModel2;
	private Vector3f projectorAim2 = new Vector3f();

	public static void main( String[] args ) {
		TestProjectedTexture app = new TestProjectedTexture();
		app.setDialogBehaviour( ALWAYS_SHOW_PROPS_DIALOG );
		app.start();
	}

	protected void simpleUpdate() {
		//make a funny projector animation
		projectorModel1.getLocalTranslation().set( FastMath.sin( timer.getTimeInSeconds() ) * (FastMath.sin( timer.getTimeInSeconds() * 0.7f ) * 15.0f + 20.0f),
												  FastMath.sin( timer.getTimeInSeconds() * 0.5f ) * 10.0f + 20.0f,
												  FastMath.cos( timer.getTimeInSeconds() * 1.2f ) * (FastMath.sin( timer.getTimeInSeconds() ) * 15.0f + 20.0f) );
		projectorAim1.set( FastMath.sin( timer.getTimeInSeconds() * 0.8f - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() * 0.5f - FastMath.PI ) * 10.0f + 10.0f),
						  0.0f,
						  FastMath.cos( timer.getTimeInSeconds() * 0.6f - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() * 0.3f - FastMath.PI ) * 10.0f + 10.0f) );
		projectorModel1.lookAt( projectorAim1, Vector3f.UNIT_Y );
		projectorModel1.updateGeometricState( 0.0f, true );

		//update texture matrix
		ProjectedTextureUtil.updateProjectedTexture( projectedTexture1, 30.0f, 1.5f, 1.0f, 1000.0f, projectorModel1.getLocalTranslation(), projectorAim1, Vector3f.UNIT_Y );

		//make a second funny projector animation
		projectorModel2.getLocalTranslation().set( FastMath.sin( timer.getTimeInSeconds() - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() * 1.1f ) * 15.0f + 20.0f),
												  FastMath.sin( timer.getTimeInSeconds() * 0.7f - FastMath.PI ) * 10.0f + 30.0f,
												  FastMath.cos( timer.getTimeInSeconds() * 0.4f - FastMath.PI ) * (FastMath.sin( timer.getTimeInSeconds() ) * 15.0f + 20.0f) );
		projectorAim2.set( FastMath.sin( timer.getTimeInSeconds() * 0.4f ) * (FastMath.sin( timer.getTimeInSeconds() * 0.7f ) * 10.0f + 10.0f),
						  0.0f,
						  FastMath.cos( timer.getTimeInSeconds() * 0.3f ) * (FastMath.sin( timer.getTimeInSeconds() * 0.8f ) * 10.0f + 10.0f) );
		projectorModel2.lookAt( projectorAim2, Vector3f.UNIT_Y );
		projectorModel2.updateGeometricState( 0.0f, true );

		//update texture matrix
		ProjectedTextureUtil.updateProjectedTexture( projectedTexture2, 20.0f, 1.0f, 1.0f, 1000.0f, projectorModel2.getLocalTranslation(), projectorAim2, Vector3f.UNIT_Y );
	}

	protected void simpleInitGame() {
		try {
            try {
                ResourceLocatorTool.addResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        new SimpleResourceLocator(TestProjectedTexture.class
                                .getClassLoader().getResource(
                                        "jmetest/data/model/msascii/")));
            } catch (URISyntaxException e1) {
                logger.log(Level.WARNING, "unable to setup texture directory.", e1);
            }

            display.setTitle( "Projected Texture Test" );

			cam.getLocation().set( new Vector3f( 50, 50, 0 ) );
			cam.lookAt( new Vector3f(), Vector3f.UNIT_Y );

			CullState cs = display.getRenderer().createCullState();
			cs.setCullMode( CullState.CS_BACK );
			cs.setEnabled( true );

			//load projector model
			MilkToJme converter2 = new MilkToJme();
			URL MSFile2 = TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/model/msascii/camera.ms3d" );
			ByteArrayOutputStream BO2 = new ByteArrayOutputStream();

			try {
				converter2.convert( MSFile2.openStream(), BO2 );
			} catch( IOException e ) {
				logger.info( "IO problem writting the file!!!" );
				logger.info( e.getMessage() );
				System.exit( 0 );
			}
			projectorModel1 = null;
			try {
				projectorModel1 = (Node) BinaryImporter.getInstance().load( new ByteArrayInputStream( BO2.toByteArray() ) );
			} catch( IOException e ) {
				logger.info( "darn exceptions:" + e.getMessage() );
			}
			rootNode.attachChild( projectorModel1 );

			projectorModel2 = new Box( "Projector2", new Vector3f(), 1, 1, 2 );
			projectorModel2.setModelBound( new BoundingBox() );
			projectorModel2.updateModelBound();
			TextureState ts = display.getRenderer().createTextureState();
			Texture t0 = TextureManager.loadTexture(
					TestProjectedTexture.class.getClassLoader().getResource(
							"jmetest/data/texture/Detail.jpg" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			t0.setWrap( Texture.WM_WRAP_S_WRAP_T );
			ts.setTexture( t0, 0 );
			projectorModel2.setRenderState( ts );
			rootNode.attachChild( projectorModel2 );

			//create terrain
			URL grayScale = TestProjectedTexture.class.getClassLoader().getResource( "jmetest/data/texture/terrain.png" );
			ImageBasedHeightMap heightMap = new ImageBasedHeightMap( new javax.swing.ImageIcon( grayScale ).getImage() );
			Vector3f terrainScale = new Vector3f( .5f, .05f, .5f );
			terrain = new TerrainPage( "image icon", 33, (heightMap.getSize()) + 1, terrainScale, heightMap.getHeightMap(), false );
			terrain.setDetailTexture( 1, 16 );
			terrain.setModelBound( new BoundingBox() );
			terrain.updateModelBound();
			terrain.setLocalTranslation( new Vector3f( 0, 0, 0 ) );
			rootNode.attachChild( terrain );
			rootNode.setRenderState( cs );

			ProceduralSplatTextureGenerator pst = new ProceduralSplatTextureGenerator( heightMap );
			pst.addTexture( new javax.swing.ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/grassb.png" ) ), -128, 0, 128 );
			pst.addTexture( new javax.swing.ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/dirt.jpg" ) ), 0, 128, 255 );
			pst.addTexture( new javax.swing.ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/highest.jpg" ) ), 128, 255, 384 );

			pst.addSplatTexture( new javax.swing.ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/terrainTex.png" ) ), new javax.swing.ImageIcon( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/water.png" ) ) );
			pst.createTexture( 512 );

			ts = display.getRenderer().createTextureState();
			ts.setEnabled( true );
			Texture t1 = TextureManager.loadTexture( pst.getImageIcon().getImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true );
			ts.setTexture( t1, 0 );

			t1.setApply( Texture.AM_COMBINE );
			t1.setCombineFuncRGB( Texture.ACF_MODULATE );
			t1.setCombineSrc0RGB( Texture.ACS_TEXTURE );
			t1.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
			t1.setCombineSrc1RGB( Texture.ACS_PRIMARY_COLOR );
			t1.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
			t1.setCombineScaleRGB( 1.0f );

			//create a texture to use for projection
			projectedTexture1 = TextureManager.loadTexture( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.png" ), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR );
			ts.setTexture( projectedTexture1, 1 );

			//this is were we set the texture up for projection
			ProjectedTextureUtil.setupProjectedTexture( projectedTexture1, Texture.WM_BCLAMP_S_BCLAMP_T, Texture.ACF_ADD );

			//create another texture to use for projection
			projectedTexture2 = TextureManager.loadTexture( TestProjectedTexture.class.getClassLoader().getResource(
					"jmetest/data/texture/halo.jpg" ), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR );
			ts.setTexture( projectedTexture2, 2 );

			//this is were we set the texture up for projection
			ProjectedTextureUtil.setupProjectedTexture( projectedTexture2, Texture.WM_BCLAMP_S_BCLAMP_T, Texture.ACF_ADD );

			terrain.setRenderState( ts );

			Box dummyBox = new Box( "Dummybox", new Vector3f(), 2, 12, 3 );
			dummyBox.setModelBound( new BoundingBox() );
			dummyBox.updateModelBound();
			ts = display.getRenderer().createTextureState();
			t0 = TextureManager.loadTexture(
					TestProjectedTexture.class.getClassLoader().getResource(
							"jmetest/data/texture/wall.jpg" ),
					Texture.MM_LINEAR_LINEAR,
					Texture.FM_LINEAR );
			t0.setWrap( Texture.WM_WRAP_S_WRAP_T );
			ts.setTexture( t0, 0 );
			ts.setTexture( projectedTexture1, 1 );
			ts.setTexture( projectedTexture2, 2 );
			dummyBox.setRenderState( ts );
			rootNode.attachChild( dummyBox );

			terrain.lock();

			rootNode.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_OPAQUE );
			fpsNode.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_OPAQUE );
		} catch( Exception e ) {
			logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
		}
	}
}
