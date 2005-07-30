package jmetest.stress.graphbrowser.simple;

import jmetest.stress.graphbrowser.GraphAccessor;
import com.jme.math.FastMath;

/**
 * Very simple implementation of a {@link GraphAccessor}. For testing {@link jmetest.stress.graphbrowser.GraphBrowser}.
 */
public class SimpleGraphAccessor implements GraphAccessor {

    public int getNodeCount() {
        return 2048;
    }

    public Object getNode( int index ) {
        return new Integer( index );
    }

    public int getEdgeCount() {
        return getNodeCount();
    }

    public Object getEdge( int index ) {
        return new Integer( index );
    }

    public Object getEdgeSource( Object edge ) {
        return edge;
    }

    public Object getEdgeTarget( Object edge ) {
        final int edgeNum = ( (Integer) edge ).intValue();
        return new Integer( ( edgeNum + 1 )%getNodeCount() );
    }

    public boolean isNodeTerminal( Object node ) {
        return ( (Integer) node ).intValue() < 2;
    }

    public boolean isEdgePath( Object edge ) {
        return ( (Integer) edge ).intValue() == 0;
    }
}
