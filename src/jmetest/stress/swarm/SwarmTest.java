package jmetest.stress.swarm;

import java.util.*;
import java.util.logging.Level;

import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.app.SimpleGame;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.image.Texture;

/**
 * This is a stress test with following charactersitics:
 * very high number of fast rendering geoms (boxes), with many changes to position (on/off), flat/organized tree
 * <br>
 * Many {@link Fish} are swarming around...
 * @author Irrisor
 * @created 21.11.2004, 12:28:12
 */
public class SwarmTest extends SimpleGame
{
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
    private static final int NUMBER_OF_FISH = 1000;

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

        /** Assign key R to COMMAND_REORGANIZATION. */
        KeyBindingManager.getKeyBindingManager().set(
                COMMAND_REORGANIZATION,
                KeyInput.KEY_R);
        final Text text = createText( "Press R to toggle scene graph reorganization" );
        text.getLocalTranslation().set( 0, 20, 0 );
        rootNode.attachChild( text );
    }

    /**
     * Create a line of text.
     * @param string displayed text
     * @return Text
     */
    protected Text createText( final String string ) {
        // -- FPS DISPLAY
        // First setup alpha state
        /** This allows correct blending of text and what is already rendered below it*/
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        // Now setup font texture
        TextureState font = display.getRenderer().createTextureState();
        /** The texture is loaded from fontLocation */
        font.setTexture(
                TextureManager.loadTexture(
                        SimpleGame.class.getClassLoader().getResource(
                                fontLocation),
                        Texture.MM_LINEAR,
                        Texture.FM_LINEAR));
        font.setEnabled(true);

        Text text = new Text("hint", string);
        text.setForceView(true);
        text.setTextureCombineMode(TextureState.REPLACE);

        text.setRenderState(font);
        text.setRenderState(as1);
        text.setForceView(true);
        text.setLightCombineMode( LightState.OFF );
        return text;
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
        new SwarmTest().start();
    }
}
