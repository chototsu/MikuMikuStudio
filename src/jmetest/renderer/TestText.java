/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.renderer;

import com.jme.app.BaseGame;
import com.jme.image.Texture;
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
 * <code>TestText</code> draws text using the scenegraph.
 * @author Mark Powell
 * @version $Id: TestText.java,v 1.11 2005-09-15 17:13:24 renanse Exp $
 */
public class TestText extends BaseGame {

    private Text text;
    private Camera cam;
    private Node scene;

    public static void main(String[] args) {
        TestText app = new TestText();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Not used.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {}

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
            display.createWindow(properties.getWidth(), properties.getHeight(),
                    properties.getDepth(), properties.getFreq(), properties.getFullscreen());
            display.setTitle("Test Text");
            cam = display.getRenderer().createCamera(properties.getWidth(), properties.getHeight());
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA blueColor = new ColorRGBA(0f, 0f, 255f, 1f);
        display.getRenderer().setBackgroundColor(blueColor);

        cam.setFrustum(1.0f,1000.0f,-0.55f,0.55f,0.4125f,-0.4125f);
        Vector3f loc = new Vector3f(4.0f,0.0f,0.0f);
        Vector3f left = new Vector3f(0.0f,-1.0f,0.0f);
        Vector3f up = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f dir = new Vector3f(-1.0f,0f,0.0f);
        cam.setFrame(loc,left,up,dir);
        display.getRenderer().setCamera(cam);
    }

    /**
     * initializes the scene
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        AlphaState as = display.getRenderer().createAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        as.setEnabled(true);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
            TextureManager.loadTexture(
                TestText.class.getClassLoader().getResource("jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR));
        ts.setEnabled(true);

        text = new Text("text", "Testing Text! Look, symbols: <>?!^&*_");
        text.setLocalTranslation(new Vector3f(1,60,0));
        text.setRenderState(ts);
        text.setRenderState(as);

        scene = new Node("3D Scene Node");
        scene.attachChild(text);
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
