package jmetest.bounds;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;

/**
 * 
 */
public class BoundsTest extends junit.framework.TestCase {
    public void testMergeBox() {
        BoundingBox b1 = new BoundingBox( new Vector3f( 0, 0, -15 ), 10, 10, 0 );
        BoundingBox b2 = new BoundingBox( new Vector3f( 0, 0, -5 ), 10, 10, 0 );
        BoundingBox result = new BoundingBox();
        b1.clone( result );
        result.merge( b2 );
        System.out.println( "merged" );
        System.out.println( b1 );
        System.out.println( "and" );
        System.out.println( b2 );
        System.out.println( "to" );
        System.out.println( result );
        assertEquals( "center x", 0, result.getCenter().x, FastMath.FLT_EPSILON );
        assertEquals( "center y", 0, result.getCenter().y, FastMath.FLT_EPSILON );
        assertEquals( "center z", -10, result.getCenter().z, FastMath.FLT_EPSILON );
        assertEquals( "extent x", 10, result.xExtent, FastMath.FLT_EPSILON );
        assertEquals( "extent y", 10, result.yExtent, FastMath.FLT_EPSILON );
        assertEquals( "extent z", 5, result.zExtent, FastMath.FLT_EPSILON );
    }

    public void testMergeSphereOBB() {
        BoundingSphere sphere = new BoundingSphere( 1, new Vector3f() );
        OrientedBoundingBox obb = new OrientedBoundingBox();
        obb.setCenter( new Vector3f( 1, 1, 0 ) );
        obb.setExtent( new Vector3f( 1, 1, 1 ) );

        BoundingSphere merged = (BoundingSphere) sphere.merge( obb );
        BoundingSphere merged2 = (BoundingSphere) merged.merge( obb );
        System.out.println( merged );
        System.out.println( merged2 );
        assertEquals( "center", merged.getCenter(), merged2.getCenter() );
        assertEquals( "radius", merged.getRadius(), merged2.getRadius(), FastMath.FLT_EPSILON );
    }
}