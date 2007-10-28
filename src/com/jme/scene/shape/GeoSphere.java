package com.jme.scene.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.geom.BufferUtils;

/**
 * GeoSphere - generate a polygon mesh approximating a sphere by
 * recursive subdivision. First approximation is an octahedron;
 * each level of refinement increases the number of polygons by
 * a factor of 4.
 * <p>todo: texture coordinates could be nicer</p>
 * <p/>
 * Shared vertices are not retained, so numerical errors may produce
 * cracks between polygons at high subdivision levels.
 * <p/>
 * Initial idea and text from C-Sourcecode by Jon Leech 3/24/89
 * Translated to Java and tuned for jME by Irrisor
 */

public class GeoSphere extends TriMesh {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxlevels;
    private boolean useIkosa = true;

    /**
     * @param name      name of the spatial
     * @param ikosa     true to start with an 20 triangles, false to start with 8 triangles
     * @param maxlevels an integer >= 1 setting the recursion level
     * @see jmetest.shape.TestGeoSphere
     */
    public GeoSphere( String name, boolean ikosa, int maxlevels ) {
        super( name );
        this.maxlevels = maxlevels;
        this.useIkosa = ikosa;
        setGeometry();
    }

    /**
     * Default ctor for restoring.   
     */
    public GeoSphere()
    {
    }

    /**
     * TODO: radius is always 1
     * @return 1
     */
    public float getRadius() {
        return 1;
    }

    static class Triangle {
        int[] pt = new int[3];   /* Vertices of triangle */

        public Triangle() {
        }

        public Triangle( int pt0, int pt1, int pt2 ) {
            pt[0] = pt0;
            pt[1] = pt1;
            pt[2] = pt2;
        }
    }

    private void setGeometry() {
        boolean useIkosa = this.useIkosa;
        int initialTriangleCount = useIkosa ? 20 : 8;
        int initialVertexCount = useIkosa ? 12 : 6;
        TriangleBatch batch = getBatch( 0 );

        // number of triangles = initialTriangleCount * 4^(maxlevels-1)
        int triangleQuantity = initialTriangleCount << ( ( maxlevels - 1 ) * 2 );
        batch.setTriangleQuantity( triangleQuantity );
        // number of vertBuf = (initialVertexCount + initialTriangleCount*4 + initialTriangleCount*4*4 + ...)
        //   = initialTriangleCount*(((4^maxlevels)-1)/(4-1)-1) + initialVertexCount
        int vertQuantity = initialTriangleCount * ( ( ( 1 << ( maxlevels * 2 ) ) - 1 ) / ( 4 - 1 ) - 1 ) + initialVertexCount;
        batch.setVertexCount( vertQuantity );

        FloatBuffer vertBuf = batch.getVertexBuffer();
        batch.setVertexBuffer( vertBuf = BufferUtils.createVector3Buffer( vertBuf, vertQuantity ) );
        batch.setNormalBuffer( BufferUtils.createVector3Buffer( batch.getNormalBuffer(), vertQuantity ) );
        batch.setTextureBuffer( BufferUtils.createVector3Buffer( batch.getTextureBuffer( 0 ), vertQuantity ), 0 );

        int pos = 0;

        Triangle[] old;
        if ( useIkosa ) {
            int[] indices = new int[]{
                    pos + 0, pos + 1, pos + 2,
                    pos + 0, pos + 2, pos + 3,
                    pos + 0, pos + 3, pos + 4,
                    pos + 0, pos + 4, pos + 5,
                    pos + 0, pos + 5, pos + 1,
                    pos + 1, pos + 10, pos + 6,
                    pos + 2, pos + 6, pos + 7,
                    pos + 3, pos + 7, pos + 8,
                    pos + 4, pos + 8, pos + 9,
                    pos + 5, pos + 9, pos + 10,
                    pos + 6, pos + 2, pos + 1,
                    pos + 7, pos + 3, pos + 2,
                    pos + 8, pos + 4, pos + 3,
                    pos + 9, pos + 5, pos + 4,
                    pos + 10, pos + 1, pos + 5,
                    pos + 11, pos + 7, pos + 6,
                    pos + 11, pos + 8, pos + 7,
                    pos + 11, pos + 9, pos + 8,
                    pos + 11, pos + 10, pos + 9,
                    pos + 11, pos + 6, pos + 10};
            float y = 0.4472f;
            float a = 0.8944f;
            float b = 0.2764f;
            float c = 0.7236f;
            float d = 0.8507f;
            float e = 0.5257f;
            pos++;
            put( new Vector3f( 0, 1, 0 ) );
            pos++;
            put( new Vector3f( a, y, 0 ) );
            pos++;
            put( new Vector3f( b, y, -d ) );
            pos++;
            put( new Vector3f( -c, y, -e ) );
            pos++;
            put( new Vector3f( -c, y, e ) );
            pos++;
            put( new Vector3f( b, y, d ) );
            pos++;
            put( new Vector3f( c, -y, -e ) );
            pos++;
            put( new Vector3f( -b, -y, -d ) );
            pos++;
            put( new Vector3f( -a, -y, 0 ) );
            pos++;
            put( new Vector3f( -b, -y, d ) );
            pos++;
            put( new Vector3f( c, -y, e ) );
            pos++;
            put( new Vector3f( 0, -1, 0 ) );
            Triangle[] ikosaedron = new Triangle[indices.length / 3];
            for ( int i = 0; i < ikosaedron.length; i++ ) {
                Triangle triangle = ikosaedron[i] = new Triangle();
                triangle.pt[0] = indices[i * 3 ];
                triangle.pt[1] = indices[i * 3 + 1];
                triangle.pt[2] = indices[i * 3 + 2];
            }

            old = ikosaedron;
        } else {
            /* Six equidistant points lying on the unit sphere */
            final Vector3f XPLUS = new Vector3f( 1, 0, 0 );   /*  X */
            final Vector3f XMIN = new Vector3f( -1, 0, 0 );   /* -X */
            final Vector3f YPLUS = new Vector3f( 0, 1, 0 );   /*  Y */
            final Vector3f YMIN = new Vector3f( 0, -1, 0 );   /* -Y */
            final Vector3f ZPLUS = new Vector3f( 0, 0, 1 );   /*  Z */
            final Vector3f ZMIN = new Vector3f( 0, 0, -1 );   /* -Z */

            int xplus = pos++;
            put( XPLUS );
            int xmin = pos++;
            put( XMIN );
            int yplus = pos++;
            put( YPLUS );
            int ymin = pos++;
            put( YMIN );
            int zplus = pos++;
            put( ZPLUS );
            int zmin = pos++;
            put( ZMIN );

            Triangle[] octahedron = new Triangle[]{
                    new Triangle( yplus, zplus, xplus ),
                    new Triangle( xmin, zplus, yplus ),
                    new Triangle( ymin, zplus, xmin ),
                    new Triangle( xplus, zplus, ymin ),
                    new Triangle( zmin, yplus, xplus ),
                    new Triangle( zmin, xmin, yplus ),
                    new Triangle( zmin, ymin, xmin ),
                    new Triangle( zmin, xplus, ymin )
            };

            old = octahedron;
        }

//        if ( CLOCKWISE )
//        /* Reverse order of points in each triangle */
//        for ( int i = 0; i < old.length; i++ ) {
//            int tmp;
//            tmp = old[i].pt[0];
//            old[i].pt[0] = old[i].pt[2];
//            old[i].pt[2] = tmp;
//        }

        Vector3f pt0 = new Vector3f();
        Vector3f pt1 = new Vector3f();
        Vector3f pt2 = new Vector3f();

        /* Subdivide each starting triangle (maxlevels - 1) times */
        for (
                int level = 1;
                level < maxlevels; level++ )

        {
            /* Allocate a next triangle[] */
            Triangle[] next = new Triangle[old.length * 4];
            for ( int i = 0; i < next.length; i++ ) {
                next[i] = new Triangle();
            }

            /* Subdivide each polygon in the old approximation and normalize
            *  the next points thus generated to lie on the surface of the unit
            *  sphere.
            * Each input triangle with vertBuf labelled [0,1,2] as shown
            *  below will be turned into four next triangles:
            *
            *         Make next points
            *             a = (0+2)/2
            *             b = (0+1)/2
            *             c = (1+2)/2
            *        1
            *       /\      Normalize a, b, c
            *      /  \
            *    b/____\ c	   Construct next triangles
            *    /\    /\          [0,b,a]
            *   /  \  /  \          [b,1,c]
            *  /____\/____\       [a,b,c]
            * 0	 a      2	    [a,c,2]
            */
            for ( int i = 0; i < old.length; i++ ) {
                int newi = i * 4;
                Triangle oldt = old[i],
                        newt = next[newi];

                BufferUtils.populateFromBuffer( pt0, vertBuf, oldt.pt[0] );
                BufferUtils.populateFromBuffer( pt1, vertBuf, oldt.pt[1] );
                BufferUtils.populateFromBuffer( pt2, vertBuf, oldt.pt[2] );
                Vector3f av = createMidpoint( pt0, pt2 ).normalizeLocal();
                Vector3f bv = createMidpoint( pt0, pt1 ).normalizeLocal();
                Vector3f cv = createMidpoint( pt1, pt2 ).normalizeLocal();
                int a = pos++;
                put( av );
                int b = pos++;
                put( bv );
                int c = pos++;
                put( cv );

                newt.pt[0] = oldt.pt[0];
                newt.pt[1] = b;
                newt.pt[2] = a;
                newt = next[++newi];

                newt.pt[0] = b;
                newt.pt[1] = oldt.pt[1];
                newt.pt[2] = c;
                newt = next[++newi];

                newt.pt[0] = a;
                newt.pt[1] = b;
                newt.pt[2] = c;
                newt = next[++newi];

                newt.pt[0] = a;
                newt.pt[1] = c;
                newt.pt[2] = oldt.pt[2];
            }

            /* Continue subdividing next triangles */
            old = next;
        }

        IntBuffer indexBuffer = BufferUtils.createIntBuffer( triangleQuantity * 3 );
        batch.setIndexBuffer( indexBuffer );

        for ( Triangle triangle : old ) {
            for ( int aPt : triangle.pt ) {
                indexBuffer.put( aPt );
            }
        }
    }

    private void put( Vector3f vec ) {
        TriangleBatch batch = getBatch( 0 );
        FloatBuffer vertBuf = batch.getVertexBuffer();
        vertBuf.put( vec.x );
        vertBuf.put( vec.y );
        vertBuf.put( vec.z );

        float length = vec.length();
        FloatBuffer normBuf = batch.getNormalBuffer();
        float xNorm = vec.x / length;
        normBuf.put( xNorm );
        float yNorm = vec.y / length;
        normBuf.put( yNorm );
        float zNorm = vec.z / length;
        normBuf.put( zNorm );

        FloatBuffer texBuf = batch.getTextureBuffer( 0 );
        texBuf.put( (FastMath.atan2( yNorm, xNorm )/(2*FastMath.PI)+1)%1 );
        texBuf.put( zNorm/2+0.5f );
    }

    /**
     * Compute the average of two vectors.
     * @param a first vector
     * @param b second vector
     * @return the average of two points
     */
    Vector3f createMidpoint( Vector3f a, Vector3f b ) {
        return new Vector3f( ( a.x + b.x ) * 0.5f, ( a.y + b.y ) * 0.5f, ( a.z + b.z ) * 0.5f );
    }
}
