/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package jmetest.scalarfields;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

/**
 * Demo to show off the {@link ScalarFieldPolygonisator} for scalar fields.
 *
 * @author Daniel Gronau
 */
public class TestMetaballs extends SimpleGame {

    private Vector3f boxSize = new Vector3f( 5, 5, 5 );
    private TriMesh mesh = new TriMesh( "mesh" );
    private ScalarFieldPolygonisator poly;
    private MetaBallScalarField field;

    public static void main( String[] args ) {
        TestMetaballs app = new TestMetaballs();
        app.setConfigShowMode( ConfigShowMode.AlwaysShow );
        app.start();
    }

    @Override
    protected void simpleInitGame() {
        float maxWeight = 10f;
        float maxSpeed = 0.1f;
        field = new MetaBallScalarField(
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 1, 0, 0, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 1, 1, 0, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 0, 1, 0, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 0, 1, 1, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 0, 0, 1, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 1, 0, 1, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 1, 1, 1, 1 ) ),
                MetaBallScalarField.MetaBall.getRandomBall( boxSize, maxWeight, maxSpeed, new ColorRGBA( 0, 0, 0, 1 ) ) );
        poly = new ScalarFieldPolygonisator( boxSize.mult( 2 ), 0.8f, field );
        mesh.setRenderState( createTextureState( "cloud_land.jpg" ) );
        rootNode.attachChild( mesh );

        // light up the scene a little (attention works for SimpleGame only)
        this.lightState.get( 0 ).setAmbient( ColorRGBA.white );
        this.lightState.get( 0 ).setDiffuse( ColorRGBA.white );
        final MaterialState state = display.getRenderer().createMaterialState();
        state.setColorMaterial( MaterialState.ColorMaterial.Diffuse );
        state.setShininess( 100 );
        state.setAmbient( new ColorRGBA( 0.4f, 0.4f, 0.4f, 1 ) );
        mesh.setRenderState( state );
    }

    @Override
    protected void simpleUpdate() {
        field.updateBallLocations( boxSize );
        poly.calculate( mesh, 1f );
    }

    private TextureState createTextureState( String texture ) {

        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator( getClass().getResource(
                            "/jmetest/data/texture/" ) ) );
        } catch ( Exception e ) {
            System.err.println( "Unable to access texture directory." );
            e.printStackTrace();
        }

        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        Texture t = TextureManager.loadTexture( texture, Texture.MinificationFilter.BilinearNoMipMaps,
                Texture.MagnificationFilter.Bilinear, ts.getMaxAnisotropic(), false );
        t.setWrap( Texture.WrapMode.Repeat );
//        t.setEnvironmentalMapMode( Texture.EnvironmentalMapMode.ReflectionMap );
        ts.setTexture( t );
        return ts;
    }
}