package jmetest.shape;

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.GeoSphere;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 *
 */
public class TestGeoSphere extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestGeoSphere.class
            .getName());
    
    private TextureState textureState;

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected void simpleInitGame() {
        createTextureState();

        createGeoSpheres( true, -3, -1 );
        createGeoSpheres( false, 0, -2 );

        final TriMesh sphere1 = new Sphere( "sphere1", 3, 4, 1 );
        logger.info( "Sphere triangles: " + sphere1.getTriangleCount() );
        sphere1.getLocalTranslation().set( -2.5f, 3, 0 );
        init( sphere1 );

        final TriMesh sphere2 = new Sphere( "sphere2", 4, 8, 1 );
        logger.info( "Sphere triangles: " + sphere2.getTriangleCount() );
        sphere2.getLocalTranslation().set( 0, 3, 0 );
        init( sphere2 );

        final TriMesh sphere3 = new Sphere( "sphere3", 8, 11, 1 );
        logger.info( "Sphere triangles: " + sphere3.getTriangleCount() );
        sphere3.getLocalTranslation().set( 2.5f, 3, 0 );
        init( sphere3 );

        final TriMesh sphere4 = new Sphere( "sphere4", 16, 18, 1 );
        logger.info( "Sphere triangles: " + sphere4.getTriangleCount() );
        sphere4.getLocalTranslation().set( 5f, 3, 0 );
        init( sphere4 );
    }

    private void createTextureState() {
        textureState = display.getRenderer().createTextureState();
        textureState.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                TestGeoSphere.class.getClassLoader().getResource(
                        "jmetest/data/texture/clouds.png"), Texture.MM_LINEAR,
                Texture.FM_LINEAR);
        textureState.setTexture(t1);
    }

    private void createGeoSpheres( boolean ikosa, float y, int offset ) {
        for ( int level = 1; level <= 4; level++ ) {
            final TriMesh geosphere = new GeoSphere( "geosphere", ikosa, level );
            logger.info( "Geosphere (" + ( ikosa ? "ikosa" : "octa" ) + ") triangles: " + geosphere.getTriangleCount() );
            geosphere.getLocalTranslation().set( ( level + offset ) * 2.5f, y, 0 );
            init( geosphere );
        }
    }

    private void init( TriMesh spatial ) {
        AlphaState alphaState = display.getRenderer().createAlphaState();
        alphaState.setEnabled( true );
        alphaState.setBlendEnabled( true );
        alphaState.setSrcFunction( AlphaState.SB_SRC_ALPHA );
        alphaState.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );

        spatial.addController( new RotatingController( spatial ) );
        rootNode.attachChild( spatial );
        spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        spatial.setRenderState( alphaState );

        MaterialState material = display.getRenderer().createMaterialState();
        material.setShininess( 128 );
        ColorRGBA color = new ColorRGBA( 0.7f, 0.7f, 0.7f, 1f );
        material.setDiffuse( color );
        material.setAmbient( color.mult( new ColorRGBA( 0.1f, 0.1f, 0.1f, 1 ) ) );
        spatial.setRenderState( material );

        spatial.setRenderState( textureState );
//        spatial.setRenderState( display.getRenderer().createWireframeState() );
    }

    public static void main( String[] args ) {
        new TestGeoSphere().start();
    }

    private static class RotatingController extends Controller {
        private Quaternion rot;
        private Vector3f axis;
        private final Spatial spatial;

        public RotatingController( Spatial spatial ) {
            this.spatial = spatial;
            rot = new Quaternion();
            axis = new Vector3f( 1, 0, 0 ).normalizeLocal();
        }

        public void update( float time ) {
            rot.fromAngleNormalAxis( 0.5f * time, axis );
            spatial.getLocalRotation().multLocal( rot );
        }
    }
}
