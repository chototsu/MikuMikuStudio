/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package jmetest.input;

import com.jme.app.BaseGame;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputSystem;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * <code>TestAbsoluteMouse</code>
 * @author Mark Powell
 * @version 
 */
public class TestAbsoluteMouse extends BaseGame {

    private Text text;
    private Camera cam;
    private Node scene;
    private AbsoluteMouse mouse;

    public static void main(String[] args) {
        TestAbsoluteMouse app = new TestAbsoluteMouse();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Not used.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        mouse.update();
        text.print("Position: " + mouse.getLocalTranslation().x + " , " +
                mouse.getLocalTranslation().y);
    }

    /**
     * draws the scene graph
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);
    }

    /**
     * initializes the display and camera.
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        try {
        	display = DisplaySystem.getDisplaySystem(properties.getRenderer());
        	display.createWindow(
        			properties.getWidth(),
					properties.getHeight(),
					properties.getDepth(),
					properties.getFreq(),
					properties.getFullscreen());

        	cam = display.getRenderer().getCamera(properties.getWidth(), properties.getHeight());
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        InputSystem.createInputSystem(properties.getRenderer());
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

    }

    /**
     * initializes the scene
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        
        mouse = new AbsoluteMouse("Mouse Input", display.getWidth(), display.getHeight());
        TextureState cursor = display.getRenderer().getTextureState();
        cursor.setEnabled(true);
        cursor.setTexture(
        			TextureManager.loadTexture(
        					TestAbsoluteMouse.class.getClassLoader().getResource("jmetest/data/cursor/test.png"),
							Texture.MM_LINEAR, Texture.FM_LINEAR, true)
					);
        mouse.setRenderState(cursor);
        mouse.setMouseInput(InputSystem.getMouseInput());
       
        text = new Text("Text Label","Testing Mouse");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestAbsoluteMouse.class.getClassLoader().getResource("jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        text.setRenderState(ts);
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_ONE);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_COLOR);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        text.setRenderState(as1);
        mouse.setRenderState(as1);
        scene = new Node("Scene node");
        scene.attachChild(text);
        scene.attachChild(mouse);
        cam.update();

        scene.updateGeometricState(0.0f, true);
        scene.updateRenderState();
    }

    /**
     * not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {

    }

}
