package jmetest.stress.graphbrowser;

/**
 * Interface for accessing data of a graph.
 */
public interface GraphAccessor {
    /**
     * Specifies number of nodes such that 0 &lt;= index &lt; number of nodes.
     * @return number of nodes in the graph.
     * @see #getNode(int)
     */
    int getNodeCount();

    /**
     * Query the object representing the nth node in the graph.
     * @param index index of the node (n).
     * @return node at index
     */
    Object getNode( int index );

    /**
     * Specifies number of edges such that 0 &lt;= index &lt; number of edges.
     * @return number of edges in the graph.
     * @see #getEdge(int)
     */
    int getEdgeCount();

    /**
     * Query the object representing the nth edge in the graph.
     * @param index index of the edge (n).
     * @return edge at index
     */
    Object getEdge( int index );

    /**
     * Query source node of an edge.
     * @param edge edge of interest
     * @return object representing a node in the graph where the edge starts
     */
    Object getEdgeSource( Object edge );

    /**
     * Query target node of an edge.
     * @param edge edge of interest
     * @return object representing a node in the graph where the edge ends
     */
    Object getEdgeTarget( Object edge );

    /**
     * @param node any node object
     * @return true if the node is a terminal node (should be highlighted)
     */
    boolean isNodeTerminal( Object node );

    /**
     * @param edge any edge object
     * @return true if the edge is part of the selected path in the graph (should be highlighted)
     */
    boolean isEdgePath( Object edge );
}
