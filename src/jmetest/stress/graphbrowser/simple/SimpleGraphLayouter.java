package jmetest.stress.graphbrowser.simple;

import jmetest.stress.graphbrowser.GraphLayouter;
import jmetest.stress.graphbrowser.GraphAccessor;

import java.util.Random;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;

/**
 * Very simple implementation of a {@link GraphLayouter}. For testing {@link jmetest.stress.graphbrowser.GraphBrowser}.
 */
public class SimpleGraphLayouter implements GraphLayouter {
    private Object lastNode;
    private Vector3f lastPos = new Vector3f();
    private Quaternion rotation0 = new Quaternion().fromAngleNormalAxis( 0.2f, new Vector3f( 1, 2, 0 ).normalizeLocal() );
    private Quaternion rotation1 = new Quaternion().fromAngleNormalAxis( -0.2f, new Vector3f( 1, 3, 0 ).normalizeLocal() );
    private Vector3f translation0 = new Vector3f( 10, 0, 0 );
    private Vector3f translation1 = new Vector3f( 0, 5, 0 );
//    private int frame;

    /**
     * Create a simple graph layouter.
     * @param accessor accompanying accessor
     */
    public SimpleGraphLayouter( SimpleGraphAccessor accessor ) {
        if ( accessor == null )
        {
            throw new NullPointerException( "SimpleGraphLayouter is for testing SimpleGraphAccessor, only" );
        }
    }

    /**
     * Query the coordinates for a node.
     * @param node graph node of interest
     * @return position for that node
     */
    public Vector3f getCoordinates( Object node ) {
        if ( lastNode != node )
        {
            lastNode = node;
            int nodeNum = ((Integer)node).intValue();
            lastPos.set( (nodeNum & 1) * 40, 0, 0 );
//            if ( nodeNum == 0 )
//            {
//                frame++;
//            }
            while ( nodeNum != 0 )
            {
                if ( (nodeNum & 1) == 0 )
                {
                    lastPos.addLocal( translation0 );
                    rotation0.multLocal( lastPos );
                }
                else
                {
                    lastPos.addLocal( translation1 );
                    rotation1.multLocal( lastPos );
                }
                nodeNum >>= 1;
            }
        }
        return lastPos;
    }
}
