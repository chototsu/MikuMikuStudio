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
package com.jme.test.input;

import com.jme.app.AbstractGame;
import com.jme.image.Texture;
import com.jme.input.RelativeMouse;
import com.jme.input.InputSystem;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.LWJGLCamera;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.JmeException;
import com.jme.system.LWJGLDisplaySystem;
import com.jme.util.TextureManager;

/**
 * <code>TestRelativeMouse</code>
 * @author Mark Powell
 * @version 
 */
public class TestRelativeMouse extends AbstractGame {

    private Text text;
    private Camera cam;
    private Node scene;
    private RelativeMouse mouse;
    
    String xString, yString;

    public static void main(String[] args) {
        TestRelativeMouse app = new TestRelativeMouse();
        app.useDialogAlways(true);
        app.start();
    }

    /**
     * Not used.
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {
        mouse.update();
        
        if(mouse.getLocalTranslation().x > 0) {
            xString = "Right";
        } else if(mouse.getLocalTranslation().x < 0){
            xString = "Left";
        }
        
        if(mouse.getLocalTranslation().y > 0) {
            yString = "Up";
        } else if(mouse.getLocalTranslation().y < 0){
            yString = "Down";
        }
        text.print("Position: " + xString + " , " +
                yString);
    }

    /**
     * draws the scene graph
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);
    }

    /**
     * initializes the display and camera.
     * @see com.jme.app.AbstractGame#initSystem()
     */
    protected void initSystem() {
        try {
            if ("LWJGL".equalsIgnoreCase(properties.getRenderer())) {
                display = new LWJGLDisplaySystem();
                display.createWindow(
                    properties.getWidth(),
                    properties.getHeight(),
                    properties.getDepth(),
                    properties.getFreq(),
                    properties.getFullscreen());
                cam =
                    new LWJGLCamera(
                        properties.getWidth(),
                        properties.getHeight());
            }
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
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        
        mouse = new RelativeMouse();
        
        mouse.setMouseInput(InputSystem.getMouseInput());
       
        text = new Text("Testing Mouse");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                "data/Font/font.png",
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        text.setRenderState(ts);
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_ZERO);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_COLOR);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        text.setRenderState(as1);
        mouse.setRenderState(as1);
        scene = new Node();
        scene.attachChild(text);
        scene.attachChild(mouse);
        cam.update();

        scene.updateGeometricState(0.0f, true);
    }

    /**
     * not used.
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * not used.
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {

    }

}
