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

package jmetest.terrain;

import com.jme.app.*;
import com.jme.bounding.*;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import com.jme.terrain.*;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestTerrain.java,v 1.1 2004-04-09 14:40:43 Mojomonkey Exp $
 */
public class TestTerrain extends SimpleGame {
    private Camera cam;
    private CameraNode camNode;
    private Node root;
    private InputHandler input;
    private Timer timer;
    private Text fps;
    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
        TestTerrain app = new TestTerrain();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {

        timer.update();
        input.update(timer.getTimePerFrame());

        root.updateGeometricState(timer.getTimePerFrame(), true);
        fps.print(
            "FPS: "
                + (int) timer.getFrameRate()
                + " : "
                + display.getRenderer().getStatistics());
        //        System.out.println(timer.getFrameRate());
        display.getRenderer().clearStatistics();
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(root);

    }

    /**
     * creates the displays and sets up the viewport.
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
            cam =
                display.getRenderer().getCamera(
                    properties.getWidth(),
                    properties.getHeight());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);

        display.getRenderer().setCamera(cam);

        camNode = new CameraNode("Camera Node", cam);
        camNode.setLocalTranslation(new Vector3f(0, 0, -20));
        camNode.updateWorldData(0);
        //camNode.setLocalTranslation(new Vector3f();
        input = new NodeHandler(this, camNode, "LWJGL");
        input.setKeySpeed(10f);
        input.setMouseSpeed(1f);
        display.setTitle("Terrain Test");
        display.getRenderer().enableStatistics(true);
        timer = Timer.getTimer(properties.getRenderer());

    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        Vector3f max = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector3f min = new Vector3f(-0.5f, -0.5f, -0.5f);
        
        WireframeState ws = display.getRenderer().getWireframeState();
        ws.setEnabled(true);
        

        Node scene = new Node("scene");
        scene.setRenderState(ws);
        root = new Node("Root node");

        float[] heights = {0.5f, 0.75f, 1.0f, 0.5f, 0.25f, 0.5f, 0.75f, 0.25f, 1.0f};
        TerrainBlock tb = new TerrainBlock("Terrain", 3, 2, heights, new Vector3f(0,0,0));
        
        scene.attachChild(tb);
        
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestTerrain.class.getClassLoader().getResource(
                    "jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));

        scene.setRenderState(ts);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        

        TextureState font = display.getRenderer().getTextureState();
        font.setTexture(
            TextureManager.loadTexture(
                TestTerrain.class.getClassLoader().getResource(
                    "jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        font.setEnabled(true);

        fps = new Text("FPS counter", "");
        fps.setRenderState(font);

        
        scene.setRenderState(buf);
        root.attachChild(scene);
        root.attachChild(fps);
        root.setForceView(true);

        root.updateGeometricState(0.0f, true);

    }
    /**
     * not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * Not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {

    }

}
