package jmetest.stress.graphbrowser;

import com.jme.math.Vector3f;

/**
 * Provide the layout for a graph.
 * @see GraphBrowser
 */
public interface GraphLayouter {
    /**
     * Query the coordinates for a node.
     * @param node graph node of interest
     * @return position for that node
     */
    Vector3f getCoordinates( Object node );
}
