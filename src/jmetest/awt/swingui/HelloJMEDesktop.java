package jmetest.awt.swingui;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;

import com.jme.app.SimpleGame;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.math.FastMath;
import com.jmex.awt.swingui.JMEDesktop;
import com.jmex.awt.swingui.JMEAction;

/**
 * Very short example for JMEDesktop - see {@link TestJMEDesktop} for more features.
 */
public class HelloJMEDesktop extends SimpleGame {

    private Node guiNode;

    protected void simpleInitGame() {
        // create a node for ortho gui stuff
        guiNode = new Node( "gui" );
        guiNode.setRenderQueueMode( Renderer.QUEUE_ORTHO );

        // create the desktop Quad
        JMEDesktop desktop = new JMEDesktop( "desktop", 500, 400, input );
        // make it transparent blue
        desktop.getJDesktop().setBackground( new Color( 0, 0, 1, 0.2f ) );
        // and attach it to the gui node
        guiNode.attachChild( desktop );
        // center it on screen
        desktop.getLocalTranslation().set( display.getWidth() / 2, display.getHeight() / 2, 0 );

        // create a swing button
        final JButton button = new JButton( "click me" );
        // and put it directly on the desktop
        desktop.getJDesktop().add( button );
        // desktop has no layout - we layout ourselfes (could assign a layout to desktop here instead)
        button.setLocation( 200, 200 );
        button.setSize( button.getPreferredSize() );
        // add some actions
        // standard swing action:
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // this gets executed in swing thread
                // alter swing components ony in swing thread!
                button.setLocation( FastMath.rand.nextInt( 400 ), FastMath.rand.nextInt( 300 ) );
            }
        } );
        // action that gets executed in the update thread:
        button.addActionListener( new JMEAction( "my action", input ) {
            public void performAction( InputActionEvent evt ) {
                // this gets executed in jme thread
                // do 3d system calls in jme thread only!
                showDepth = !showDepth; // ok this is no system call - just an example line
            }
        });

        // don't cull the gui away
        guiNode.setCullMode( Spatial.CULL_NEVER );
        // gui needs no lighting
        guiNode.setLightCombineMode( LightState.OFF );
        // update the render states (especially the texture state of the deskop!)
        guiNode.updateRenderState();
        // update the world vectors (needed as we have altered local translation of the desktop and it's
        //  not called in the update loop)
        guiNode.updateGeometricState( 0, true );

        // finally show the system mouse cursor to allow the user to click our button
        MouseInput.get().setCursorVisible( true );
    }

    protected void simpleRender() {
        // draw the gui stuff after the scene
        display.getRenderer().draw( guiNode );
    }

    public static void main( String[] args ) {
        new HelloJMEDesktop().start();
    }
}
