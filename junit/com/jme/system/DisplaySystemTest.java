package com.jme.system;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.jme.app.SimpleHeadlessApp;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import junit.framework.TestCase;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

/**
 * @author Irrisor
 */
public class DisplaySystemTest extends TestCase {

    private boolean pass;

    public void testGetScreenCoordinates() {
        if ( !checkLWJGL() ) {
            return;
        }
        pass = false;
        new SimpleHeadlessApp() {
            protected void simpleInitGame() {
                cam.setViewPort( -0.5f, 1, 0, 2 );
                cam.update();
                checkScreenCoordinates( new Vector3f( 2, 3, 4 ), cam );
                checkScreenCoordinates( new Vector3f( 100, 3, 4 ), cam );
                checkScreenCoordinates( new Vector3f( 20, 30, 400 ), cam );
                checkScreenCoordinates( new Vector3f( -20, 3, 4 ), cam );
                checkScreenCoordinates( new Vector3f( -50, 30, 4 ), cam );
                checkScreenCoordinates( new Vector3f( 3, 3, 400 ), cam );
                checkScreenCoordinates( new Vector3f( -7, 30, 4 ), cam );
                pass = true;
                finished = true;
            }

            protected void quit() {
                if ( display != null ) {
                    display.close();
                }
            }
        }.start();
        assertTrue( "test was run", pass );
    }

    private boolean checkLWJGL() {
        boolean lwjglOK = true;
        try {
            System.out.println( "checking lwjgl: " + Arrays.asList( Display.getAvailableDisplayModes() ) );
        } catch ( Throwable e ) {
            lwjglOK = false;
            System.err.println( "WARNING: no lwjgl available - test result unknown! (reporting success to let server pass)" );
        }
        return lwjglOK;
    }

    public void testGetWorldCoordinates() {
        if ( !checkLWJGL() ) {
            return;
        }
        pass = false;
        new SimpleHeadlessApp() {
            protected void simpleInitGame() {
                checkWorldCoordinates( new Vector3f( 2, 3, 4 ), cam );
                checkWorldCoordinates( new Vector3f( 100, 3, 4 ), cam );
                checkWorldCoordinates( new Vector3f( 20, 30, 400 ), cam );
                checkWorldCoordinates( new Vector3f( -20, 3, 4 ), cam );
                checkWorldCoordinates( new Vector3f( -50, 30, 4 ), cam );
                checkWorldCoordinates( new Vector3f( 3, 3, 400 ), cam );
                checkWorldCoordinates( new Vector3f( -7, 30, 4 ), cam );
                pass = true;
                finished = true;
            }

            protected void quit() {
                if ( display != null ) {
                    display.close();
                }
            }
        }.start();
        assertTrue( "test was run", pass );
    }

    public void testWorldScreenSigsaw() {
        if ( !checkLWJGL() ) {
            return;
        }
        pass = false;
        new SimpleHeadlessApp() {
            protected void simpleInitGame() {
                checkWorldScreen( new Vector3f( 2, 3, 4 ), cam );
                checkWorldScreen( new Vector3f( 100, 3, 4 ), cam );
                checkWorldScreen( new Vector3f( 20, 30, 400 ), cam );
                checkWorldScreen( new Vector3f( -20, 3, 4 ), cam );
                checkWorldScreen( new Vector3f( -50, 30, 4 ), cam );
                checkWorldScreen( new Vector3f( 3, 3, 400 ), cam );
                checkWorldScreen( new Vector3f( -7, 30, 4 ), cam );
                pass = true;
                finished = true;
            }

            protected void quit() {
                if ( display != null ) {
                    display.close();
                }
            }
        }.start();
        assertTrue( "test was run", pass );
    }

    private void checkScreenCoordinates( Vector3f worldPosition, Camera cam ) {
        Vector3f lwjglScreenCoordinates = getScreenCoordinates( worldPosition, null );
        Vector3f camScreenCoordinates = cam.getScreenCoordinates( worldPosition );
        assertEqualVectors( "lwjgl <-> cam screen", lwjglScreenCoordinates, camScreenCoordinates );
    }

    private void checkWorldCoordinates( Vector3f screenPosition, Camera cam ) {
        Vector3f lwjglWorldCoordinates = getWorldCoordinates( new Vector2f( screenPosition.x, screenPosition.y ),
                screenPosition.z, null );
        Vector3f camWorldCoordinates = cam.getWorldCoordinates( new Vector2f( screenPosition.x, screenPosition.y ),
                screenPosition.z );
        assertEqualVectors( "lwjgl <-> cam world", lwjglWorldCoordinates, camWorldCoordinates );
    }

    private void assertEqualVectors( String message, Vector3f expected, Vector3f actual ) {
        assertEquals( message + " (x)", expected.x, actual.x, FastMath.abs( expected.x ) * 0.00001f );
        assertEquals( message + " (y)", expected.y, actual.y, FastMath.abs( expected.y ) * 0.00001f );
        assertEquals( message + " (z)", expected.z, actual.z, FastMath.abs( expected.z ) * 0.00001f );
    }

    private void checkWorldScreen( Vector3f worldPosition, Camera cam ) {
        Vector3f screenPosition = cam.getScreenCoordinates( worldPosition );
        Vector3f worldCoordinatesBack = cam.getWorldCoordinates( new Vector2f( screenPosition.x, screenPosition.y ),
                screenPosition.z );
        assertEqualVectors( "world -> screen -> world", worldPosition, worldCoordinatesBack );
    }

    private FloatBuffer tmp_FloatBuffer = BufferUtils.createFloatBuffer( 16 );

    private IntBuffer tmp_IntBuffer = BufferUtils.createIntBuffer( 16 );

    public Vector3f getWorldCoordinates( Vector2f screenPosition, float zPos,
                                         Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }

        // Modelview matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
        float mvArray[][] = new float[4][4];
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                mvArray[x][y] = tmp_FloatBuffer.get();
            }
        }

        // Projection_matrix
        tmp_FloatBuffer.rewind();
        FloatBuffer prBuffer = tmp_FloatBuffer;
        GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, prBuffer );
        float prArray[][] = new float[4][4];
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                prArray[x][y] = prBuffer.get();
            }
        }

        // Viewport matrix
        tmp_IntBuffer.rewind();
        GL11.glGetInteger( GL11.GL_VIEWPORT, tmp_IntBuffer );

        // 3d coordinates
        int[] vpArray = new int[tmp_IntBuffer.capacity()];
        for ( int i = 0; i < vpArray.length; i++ ) {
            vpArray[i] = tmp_IntBuffer.get();
        }
        float[] result = new float[4];
        GLU.gluUnProject( screenPosition.x, screenPosition.y, zPos, mvArray,
                prArray, vpArray, result );

        return store.set( result[0], result[1], result[2] );
    }

    public Vector3f getScreenCoordinates( Vector3f worldPosition, Vector3f store ) {
        if ( store == null ) {
            store = new Vector3f();
        }

        // Modelview matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat( GL11.GL_MODELVIEW_MATRIX, tmp_FloatBuffer );
        float mvArray[][] = new float[4][4];
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                mvArray[x][y] = tmp_FloatBuffer.get();
            }
        }

        // Projection_matrix
        tmp_FloatBuffer.rewind();
        GL11.glGetFloat( GL11.GL_PROJECTION_MATRIX, tmp_FloatBuffer );
        float prArray[][] = new float[4][4];
        for ( int x = 0; x < 4; x++ ) {
            for ( int y = 0; y < 4; y++ ) {
                prArray[x][y] = tmp_FloatBuffer.get();
            }
        }

        // Viewport matrix
        tmp_IntBuffer.rewind();
        GL11.glGetInteger( GL11.GL_VIEWPORT, tmp_IntBuffer );
        int[] vpArray = new int[tmp_IntBuffer.capacity()];
        for ( int i = 0; i < vpArray.length; i++ ) {
            vpArray[i] = tmp_IntBuffer.get();
        }

        float[] result = new float[4];

        GLU.gluProject( worldPosition.x, worldPosition.y, worldPosition.z,
                mvArray, prArray, vpArray, result );

        return store.set( result[0], result[1], result[2] );
    }
}