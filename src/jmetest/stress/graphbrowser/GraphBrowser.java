package jmetest.stress.graphbrowser;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.AmbientLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.*;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;

import java.util.HashMap;
import java.util.Map;

import jmetest.stress.StressApp;

/**
 * Many Boxes and lines make up some graph. Graph data is specified by a {@link GraphAccessor} and
 * layout by a {@link GraphLayouter}.
 * @author Irrisor
 * @created 08.07.2005, 16:19:38
 */
public class GraphBrowser extends StressApp {

    /**
     * Map from graph node (Object) to visualization (Spatial)
     */
    Map nodes = new HashMap();
    /**
     * Map from graph edge (Object) to visualization (Line)
     */
    Map edges = new HashMap();
    /**
     * diffuse light amount.
     */
    private static final float SCENE_LIGHT = 0.7f;
    /**
     * Accessor used for reading the graph.
     */
    private GraphAccessor accessor;
    /**
     * Layouter to query positions for graph node visuals.
     */
    private GraphLayouter layouter;
    /**
     * Flag for toggling visibility of all/path edges.
     */
    private boolean pathOnly;
    /**
     * Command for toggling edges.
     */
    private static final String COMMAND_PATH_ONLY = "toggle_path_only";

    /**
     * Create a graphbrowser app that uses given {@link GraphAccessor} and {@link GraphLayouter}.
     * @param accessor graph data
     * @param layouter graph layout
     */
    public GraphBrowser( GraphAccessor accessor, GraphLayouter layouter ) {
        this.accessor = accessor;
        this.layouter = layouter;
    }

    /**
     * Create all the visual stuff for the graph.
     */
    protected void simpleInitGame() {
        AmbientLight light = new AmbientLight();
        light.setEnabled( true );
        light.setDiffuse( new ColorRGBA( SCENE_LIGHT, SCENE_LIGHT, SCENE_LIGHT, 1 ) );
        lightState.attach( light );

        for ( int i = accessor.getNodeCount() - 1; i >= 0; i-- ) {
            Object node = accessor.getNode( i );
            Box nodeVis = new Box( String.valueOf( node ), new Vector3f( -1, -1, -1 ), new Vector3f( 1, 1, 1 ) );
            nodeVis.getLocalTranslation().set( layouter.getCoordinates( node ) );

            MaterialState material = display.getRenderer().createMaterialState();
            material.setEnabled( true );
            material.setDiffuse( colorForNode( node ) );
            nodeVis.setRenderState( material );

            rootNode.attachChild( nodeVis );
            nodes.put( node, nodeVis );
        }

        for ( int i = accessor.getEdgeCount() - 1; i >= 0; i-- ) {
            Object edge = accessor.getEdge( i );

            Spatial fromVis = (Spatial) nodes.get( accessor.getEdgeSource( edge ) );
            Spatial toVis = (Spatial) nodes.get( accessor.getEdgeTarget( edge ) );
            
            Vector3f[] points = {fromVis.getLocalTranslation(), toVis.getLocalTranslation()};
            Line edgeVis = new Line( edge.toString(), points, null, null, null );

            MaterialState material = display.getRenderer().createMaterialState();
            material.setEnabled( true );
            ColorRGBA color = colorForEdge( edge );
            material.setDiffuse( color );
            material.setEmissive( color );
            edgeVis.setRenderState( material );
            edgeVis.setLightCombineMode( LightState.COMBINE_CLOSEST );

            rootNode.attachChild( edgeVis );
            edgeVis.updateRenderState();
            edges.put( edge, edgeVis );
        }

        KeyBindingManager.getKeyBindingManager().set( COMMAND_PATH_ONLY, KeyInput.KEY_O );
        final Text text = createText( "Press O to toggle edges/path" );
        text.getLocalTranslation().set( 0, 20, 0 );
        rootNode.attachChild( text );

        cam.getLocation().set( 40, 40, 100 );
        cam.update();
    }

    /**
     * Query color for an edge - could be moved to layouter...
     * @param edge edge of interest
     * @return any color (not null)
     */
    private ColorRGBA colorForEdge( Object edge ) {
        boolean steiner = accessor.isEdgePath( edge );
        return new ColorRGBA( 1, steiner ? 0 : 1, steiner ? 0 : 1, 1 );
    }

    /**
     * Query color for a node - could be moved to layouter...
     * @param node node of interest
     * @return any color (not null)
     */
    private ColorRGBA colorForNode( Object node ) {
        return new ColorRGBA( 1, 1, accessor.isNodeTerminal( node ) ? 0 : 1, 1 );
    }

    /**
     * Process key input.
     */
    protected void simpleUpdate() {
        super.simpleUpdate();

        if ( KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand( COMMAND_PATH_ONLY, false ) ) {
            pathOnly = !pathOnly;
            for ( int i = accessor.getEdgeCount() - 1; i >= 0; i-- ) {
                Object edge = accessor.getEdge( i );
                if ( !accessor.isEdgePath( edge ) ) {
                    Spatial spatial = (Spatial) edges.get( edge );
                    if ( spatial != null ) {
                        spatial.setForceCull( pathOnly );
                    }
                }
            }
        }

        // rearrange nodes
//        for ( int i = accessor.getNodeCount() - 1; i >= 0; i-- ) {
//            Object node = accessor.getNode( i );
//
//            Spatial nodeVis = (Spatial) nodes.get( node );
//
//            nodeVis.getLocalTranslation().set( layouter.getCoordinate( node, 0 ),
//                    layouter.getCoordinate( node, 1 ),
//                    layouter.getCoordinate( node, 2 ) );
//        }
    }
}
