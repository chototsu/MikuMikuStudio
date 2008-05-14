package com.jme.light;

import java.util.logging.Logger;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import junit.framework.TestCase;

/**
 * Tests for LightState relatet stuff.
 */
public class LightManagementTest extends TestCase {
    private static final Logger logger = Logger
            .getLogger(LightManagementTest.class.getName());
    
    /**
     * Test the LightStateCreator.quickSort.
     */
    public void testQuickSort() {
        LightManagement lightStateCreator = new LightManagement();

        // create nine lights
        SpotLight a = new SpotLight();
        lightStateCreator.addLight( a );
        SpotLight b = new SpotLight();
        lightStateCreator.addLight( b );
        SpotLight c = new SpotLight();
        lightStateCreator.addLight( c );
        SpotLight spot1 = new SpotLight();
        spot1.setAttenuate( true );
        spot1.setLocation( new Vector3f( 2, 0, 0 ) );
        spot1.setDirection( new Vector3f( 0, 0.5f, 0 ) );
        spot1.setLinear( 2 );
        lightStateCreator.addLight( spot1 );
        lightStateCreator.addLight( new DirectionalLight() );
        lightStateCreator.addLight( new SpotLight() );
        lightStateCreator.addLight( new SpotLight() );
        lightStateCreator.addLight( new DirectionalLight() );
        lightStateCreator.addLight( new PointLight() );

        for ( int i = 0; i < lightStateCreator.numberOfLights(); i++ ) {
            Light light = lightStateCreator.get( i );
            light.setEnabled( true );
        }

        assertEquals( "number of lights", 9, lightStateCreator.numberOfLights() );

        Node node = new Node( "test" );
        Sphere dummy = new Sphere( null, 5, 5, 1 );
        dummy.setModelBound( new BoundingSphere( 1, new Vector3f( 1, 0, 0 ) ) );
        node.attachChild( dummy );
        node.updateGeometricState( 0, true );
        lightStateCreator.sort( node );

        assertEquals( "number of lights", 9, lightStateCreator.numberOfLights() );

        float lastValue = Float.MAX_VALUE;
        int aIndex = -1;
        int bIndex = -1;
        int cIndex = -1;
        for ( int i = 0; i < lightStateCreator.numberOfLights(); i++ ) {
            Light light = lightStateCreator.get( i );
            float lightValue = lightStateCreator.getValueFor( light, node.getWorldBound() );
            assertTrue( "order wrong", lightValue <= lastValue );
            if ( light == a ) {
                aIndex = i;
            }
            if ( light == b ) {
                bIndex = i;
            }
            if ( light == c ) {
                cIndex = i;
            }
            lastValue = lightValue;
        }
        assertEquals( "b after a", aIndex + 1, bIndex );
        assertEquals( "c after b", bIndex + 1, cIndex );
    }
}
