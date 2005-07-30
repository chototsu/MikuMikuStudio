package jmetest.stress.graphbrowser;

import jmetest.stress.graphbrowser.simple.SimpleGraphLayouter;
import jmetest.stress.graphbrowser.simple.SimpleGraphAccessor;

/**
 * This is a stress test with following charactersitics:
 * very high number of fast rendering geoms (boxes and lines), without changes to position, flat tree
 * <br>
 * Many Boxes and lines make up some graph...
 * @author Irrisor
 * @created 08.07.2005, 16:19:38
 */
public class TestGraphBrowser {
    /**
     * start the test.
     * @param args command line arguments (not used)
     */
    public static void main( String[] args ) {
        SimpleGraphAccessor accessor = new SimpleGraphAccessor();
        GraphLayouter layouter = new SimpleGraphLayouter( accessor );
        new GraphBrowser( accessor, layouter ).start();
    }
}
