package jmetest.stress.swarm;

import java.util.*;
import java.util.logging.Level;
import java.io.IOException;

import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.app.SimpleGame;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.image.Texture;
import jmetest.stress.StressApp;

/**
 * This is a stress test with following charactersitics:
 * very high number of fast rendering geoms (boxes), with many changes to position (on/off), flat/organized tree
 * <br>
 * Many {@link Fish} are swarming around...
 * @author Irrisor
 * @created 21.11.2004, 12:28:12
 */
public class TestSwarm extends StressApp {
    /**
     * Flag for toggling flat/organized.
     */
    private boolean doReorganizeScenegraph = true;

    /**
     * Manager for scene graph (flat/organized).
     */
    private CollisionTreeManager collisionTreeManager;
    /**
     * command string for flat/organized toggle.
     */
    private static final String COMMAND_REORGANIZATION = "toggle_reorganization";
    /**
     * Total number of fish created.
     */
    private static final int NUMBER_OF_FISH = 1000;
    /**
     * command string for full behaviour / independent behaviour
     */
    private static final String COMMAND_COLLISION = "toggle_collision";

    /**
     * Called near end of initGame(). Must be defined by derived classes.
     */
    protected void simpleInitGame() {
        Random random = new Random();

        collisionTreeManager = new CollisionTreeManager( rootNode, new float[]{0.2f, 1.2f} );
//        collisionTreeManager = new CollisionTreeManager( rootNode, new float[]{0.1f, 1.0f} );

        //create some fish
        for ( int i=0; i<NUMBER_OF_FISH/10; ++i )
        {
            final Fish fish = new Fish(
                    0 + random.nextFloat()- 0.5f, 0 + random.nextFloat()- 0.5f, 0,
                    random.nextFloat()- 0.5f, random.nextFloat()- 0.5f, 0,
                    0.001f, rootNode );
            collisionTreeManager.add( fish );
        }

        for ( int i=0; i<NUMBER_OF_FISH*2/10; ++i )
        {
            final Fish fish = new Fish(
                    0 + random.nextFloat()- 0.5f, random.nextFloat()- 0.5f, 0,
                    random.nextFloat()- 0.5f, random.nextFloat()- 0.5f, 0,
                    0.005f, rootNode );
            collisionTreeManager.add( fish );
        }

        for ( int i=0; i<NUMBER_OF_FISH*7/10; ++i )
        {
            final Fish fish = new Fish(
                    0 + random.nextFloat()- 0.5f, 0 + random.nextFloat()- 0.5f, 0,
                    random.nextFloat()- 0.5f, random.nextFloat()- 0.5f, 0,
                    0.01f, rootNode );
            collisionTreeManager.add( fish );
        }

        //get view nearer
        cam.getLocation().set( 0, 0, 5 );
        cam.update();

        KeyBindingManager.getKeyBindingManager().set(
                COMMAND_REORGANIZATION,
                KeyInput.KEY_R);
        final Text text = createText( "Press R to toggle scene graph reorganization (node tree / flat)" );
        text.getLocalTranslation().set( 0, 20, 0 );
        rootNode.attachChild( text );

        KeyBindingManager.getKeyBindingManager().set(
                COMMAND_COLLISION,
                KeyInput.KEY_U);
        final Text text2 = createText( "Press U to toggle collision detection use (fish perception on/off)" );
        text2.getLocalTranslation().set( 0, 40, 0 );
        rootNode.attachChild( text2 );
    }

    /**
     * Can be defined in derived classes for custom updating.
     * Called every frame in update.
     */
    protected void simpleUpdate() {
        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand(COMMAND_REORGANIZATION, false)) {
            doReorganizeScenegraph = !doReorganizeScenegraph;
            if ( !doReorganizeScenegraph )
            {
                collisionTreeManager.disable();
            }
        }
        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand(COMMAND_COLLISION, false)) {
            Fish.useCollisionDetection = !Fish.useCollisionDetection;
        }
        if ( doReorganizeScenegraph )
        {
            collisionTreeManager.reorganize();
        }
    }

    /**
     * Main.
     * @param args command line arguments
     */
    public static void main( String[] args ) {
        LoggingSystem.getLogger().setLevel( Level.WARNING );
        new TestSwarm().start();
    }
}
