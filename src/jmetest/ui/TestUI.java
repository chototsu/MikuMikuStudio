/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

/*
 * Created on Jul 21, 2004
 *
 */
package jmetest.ui;

import com.jme.app.BaseGame;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.action.AbstractInputAction;
import com.jme.input.action.KeyExitAction;
import com.jme.input.action.KeyToggleBoolean;
import com.jme.input.action.KeyToggleRenderState;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.ui.UIBillboard;
import com.jme.ui.UIButton;
import com.jme.ui.UICheck;
import com.jme.ui.UIText;
import com.jme.ui.UIEditBox;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import jmetest.input.TestAbsoluteMouse;
import com.jme.util.*;

/**
 * @author schustej
 *
 */
public class TestUI extends BaseGame {

    /*
     * Base application objects
     */
    protected Camera cam;
    protected Node rootNode;

    protected InputHandler input;
    /*
     * Things to change the rootNode rendering
     */
    protected WireframeState wireState;
    protected LightState lightState;
    protected boolean showBounds = false;

    /*
     * Node for the text outputs fps and mouse location
     */
    protected Node debugNode;

    protected Timer timer;
    protected Text fpsText;
    protected float tpf;
    protected Text mouseText;

    /*
     * The Mouse and it's node
     */
    protected Node mouseNode;
    protected AbsoluteMouse mouse;

    /*
     * The UI component's node
     */
    protected Node uiNode;

    /*
     * UI Components
     */

    UIButton _button = null;
    UICheck  _check = null;
    UIBillboard _bill = null;
    UIText _text = null;
    UIEditBox _edit = null;

    /*
     * used by a bunch of stuff
     */
    public static String fontLocation = "jmetest/data/font/new32.bmp";

    /**
     * initializes the display and camera.
     *
     */
    protected void initSystem() {

        /*
         * Setup display system and create the main
         * window
         */
        try {
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(properties.getWidth(), properties.getHeight(), properties.getDepth(),
                    properties.getFreq(), properties.getFullscreen());

            cam = display.getRenderer().createCamera(properties.getWidth(), properties.getHeight());
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Create the input system
         */
        InputSystem.createInputSystem(properties.getRenderer());

        /*
         * Set the background color for the window
         */
        display.getRenderer().setBackgroundColor(ColorRGBA.black);

        /*
         * Create an input handler
         * This is a specialized input handler
         * for interacting with UI... you may of
         * course add other input handlers
         *
         * Be sure to call the update method of the input handler
         * during the update phase
         */
        input = new InputHandler();

        /*
         * really the camera settings for this whole app really shouldn't
         * matter. Everything is set as ortho
         */
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

        /*
         * setup the timer for fps checking
         */
        timer = Timer.getTimer(properties.getRenderer());

        /*
         * Setup Display
         */
        display.setTitle("UI Test");
        display.getRenderer().enableStatistics(true);

    }

    /**
     * initializes the scene
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {

        /*
         * Init the nodes
         */

        rootNode = new Node("rootNode");
        debugNode = new Node("debugNode");
        mouseNode = new Node("mouseNode");
        uiNode = new Node("uiNode");

        /*
         * Setup Render States
         */

        /*
         * Wire State
         */
        wireState = display.getRenderer().createWireframeState();
        wireState.setEnabled(false);

        /*
         * ZBuffer
         */
        ZBufferState bufState = display.getRenderer().createZBufferState();
        bufState.setEnabled(true);
        bufState.setFunction(ZBufferState.CF_LEQUAL);

        /*
         * Light State
         */
        PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3f(100, 100, 100));
        light.setEnabled(true);

        lightState = display.getRenderer().createLightState();
        /*
         * Turn it off for right now
         */
        lightState.setEnabled(false);
        lightState.attach(light);

        /*
         * Alpha states for allowing 'black' to be transparent.
         * This one is for the text font texture
         */
        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);

        /*
         * This one is for the mouse cursor
         */
        AlphaState as2 = display.getRenderer().createAlphaState();
        as2.setBlendEnabled(true);
        as2.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as2.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as2.setTestEnabled(true);
        as2.setTestFunction(AlphaState.TF_GREATER);

        /*
         * The absolute mouse
         */
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(), display.getHeight());
        TextureState cursor = display.getRenderer().createTextureState();
        cursor.setEnabled(true);
        cursor.setTexture(TextureManager.loadTexture(TestAbsoluteMouse.class.getClassLoader().getResource(
                "jmetest/data/cursor/cursor1.png"), Texture.MM_LINEAR, Texture.FM_LINEAR, true));

        /*
         * Add the cursor texture to the mouse
         */
        mouse.setRenderState(cursor);
        /*
         * Add the alpha state to the mouse
         */
        mouse.setRenderState( as2);
        mouse.setMouseInput( InputSystem.getMouseInput());

        /*
         * Set the speed of the mouse
         */
        mouse.setSpeed( 2.0f);
        /*
         * Set the mouse that the inputHandler is using
         * so the UIObjects can retrieve it
         */
        input.setMouse( mouse);

        /*
         * The texture for the font
         */
        TextureState font = display.getRenderer().createTextureState();
        font.setTexture(TextureManager.loadTexture(SimpleGame.class.getClassLoader()
                .getResource(fontLocation), Texture.MM_LINEAR, Texture.FM_LINEAR, true));
        font.setEnabled(true);

        /*
         * The Frames Per Second text
         */
        fpsText = new Text("FPS Label", "");
        fpsText.setForceView(true);
        fpsText.setRenderState(font);
        fpsText.setRenderState(as1);
        fpsText.setLocalTranslation(new Vector3f(1, 0, 0));

        /*
         * Mouse location text
         */
        mouseText = new Text("Text Label", "Testing Mouse");
        mouseText.setForceView(true);
        mouseText.setRenderState(font);
        mouseText.setRenderState(as1);
        mouseText.setLocalTranslation(new Vector3f(1, 15, 0));

        /*
         * UI Objects
         */

        _button = new UIButton( "button",
                					input,
                					"jmetest/data/images/buttonup.png",
                					"jmetest/data/images/buttonover.png",
                					"jmetest/data/images/buttondown.png",
                					100,
                					100,
                					1.0f);

        _check = new UICheck( "check",
                					input,
                					"jmetest/data/images/checkup.png",
                					"jmetest/data/images/checkover.png",
                					"jmetest/data/images/checkdown.png",
                					"jmetest/data/images/checked.png",
                					100,
                					200,
                					1.0f);

        _bill = new UIBillboard( "bill",
                					"jmetest/data/images/Monkey.png",
                					100,
                					300,
                					0.5f);
        
        _text = new UIText( "text",
                			"jmetest/data/font/conc_font.png",
                			400,
                			100,
                			1.0f,
                			50.0f,
                			5.0f);

        _text.setText( "Test some Text!");
                
        _edit = new UIEditBox( "edit",
                				"jmetest/data/font/conc_font.png",
                				100,
                				20,
                				input,
                				400,
                				200,
                				1.0f,
                				50.0f,
                				5.0f);
        //_edit.setActivateOnHover( false);
        //_edit.setActive( true);
        
        /*
         * Put everything together
         */

        /*
         * Debug Node
         */
        debugNode.attachChild(fpsText);
        debugNode.attachChild(mouseText);

        /*
         * Mouse Node
         */
        mouseNode.attachChild(mouse);

        /*
         * UI Node
         */

        uiNode.attachChild( _button);
        uiNode.attachChild( _check);
        uiNode.attachChild( _bill);
        uiNode.attachChild( _text);
        uiNode.attachChild( _edit);

        /*
         * root Node
         */

        rootNode.setRenderState(wireState);
        rootNode.setRenderState(bufState);
        rootNode.setRenderState(lightState);

        /*
         * Attach the mouse after the ui to make sure the mouse cursor is over
         * everything else
         */
        rootNode.attachChild(mouseNode);
        rootNode.attachChild(uiNode);

        /*
         * Get it going via final updates
         */
        debugNode.updateGeometricState(0.0f, true);
        debugNode.updateRenderState();

        rootNode.updateGeometricState(0.0f, true);
        rootNode.updateRenderState();

        /*
         * Key bindings
         */
		input.addKeyboardAction( "exit", KeyInput.KEY_ESCAPE, new KeyExitAction( this));
		input.addKeyboardAction( "toggle_wire", KeyInput.KEY_T, new KeyToggleRenderState( wireState, rootNode));
		input.addKeyboardAction( "toggle_lights", KeyInput.KEY_L, new KeyToggleRenderState( lightState, rootNode));
		input.addKeyboardAction( "toggle_bounds", KeyInput.KEY_B, new KeyToggleBoolean( showBounds));

		/*
		 * tester for doing the bufferered reader
		 */
		
		input.addBufferedKeyAction( new AbstractInputAction() {
            public void performAction(float time) {
                LoggingSystem.getLogger().log( java.util.logging.Level.FINE, this.key);
            }
        });
		
		/*
		 * Set up logging filtering
		 */
		
		LoggingSystem.getLogger().setLevel( java.util.logging.Level.SEVERE);
    }

    /**
     * Not used.
     *
     * @see com.jme.app.SimpleGame#update()
     */
    protected final void update(float interpolation) {
        /*
         * update the timer and the fps text
         */
        timer.update();
        tpf = timer.getTimePerFrame();
        input.update(tpf);
        fpsText.print("FPS: " + (int) timer.getFrameRate() + " - " + display.getRenderer().getStatistics());

        /*
         * Update the mouse and the mouse location text
         * This call updates the location of the mouse cursor
         */
        mouse.update();
        mouseText.print("Position: " + mouse.getLocalTranslation().x + " , " + mouse.getLocalTranslation().y);
        
        /*
         * UI Object Updates, only need to call this for objects
         * that interact with the mouse
         */
        _button.update();
        _check.update();
        _edit.update();

    }

    /**
     * draws the scene graph
     *
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {

        /*
         * Clear, getting ready for render
         */
        display.getRenderer().clearStatistics();
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(rootNode);
        if (showBounds)
            display.getRenderer().drawBounds(rootNode);
        /*
         * Draw the debug node after everything else to make sure it's on top
         */
        display.getRenderer().draw(debugNode);
    }

    /**
     * not used.
     *
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {

    }

    /**
     *
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {
        InputSystem.getMouseInput().destroy();
        System.exit(0);
    }

    /**
     * Entry point for the test,
     *
     * @param args
     */
    public static void main(String[] args) {
        TestUI app = new TestUI();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

}
