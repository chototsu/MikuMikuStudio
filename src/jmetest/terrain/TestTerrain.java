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

import javax.swing.ImageIcon;

import com.jme.app.*;
import com.jme.bounding.BoundingBox;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import com.jme.terrain.*;
import com.jme.terrain.util.FaultFractalHeightMap;
import com.jme.terrain.util.ProceduralTextureGenerator;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestTerrain.java,v 1.15 2004-04-19 02:58:53 mojomonkey Exp $
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
        //LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
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
        display.getRenderer().drawBounds(root);
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
        camNode.setLocalTranslation(new Vector3f(0, 250, -20));
        camNode.updateWorldData(0);
        //camNode.setLocalTranslation(new Vector3f();
        input = new NodeHandler(this, camNode, "LWJGL");
        input.setKeySpeed(50f);
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
        ws.setEnabled(false);

        FogState fs = display.getRenderer().getFogState();
        
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0.5f, -0.5f, 0));



        CullState cs = display.getRenderer().getCullState();
        cs.setCullMode(CullState.CS_BACK);
        cs.setEnabled(true);

        LightState lightstate = display.getRenderer().getLightState();
        lightstate.setTwoSidedLighting(true);
        lightstate.setEnabled(true);
        lightstate.attach(dr);


        Node scene = new Node("scene");
        scene.setRenderState(ws);
        scene.setRenderState(lightstate);
        root = new Node("Root node");

        //MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.9f);
        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(129, 32, 0, 255, 0.75f);
        TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(), 5, heightMap.getHeightMap(), new Vector3f(0,0,0), true);
        tb.setDetailTexture(1, 4);
        tb.setModelBound(new BoundingBox());
        tb.updateModelBound();
        scene.attachChild(tb);
        scene.setRenderState(cs);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource("jmetest/data/texture/highest.jpg")), 128, 255, 384);

        pt.createTexture(512);

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
        		pt.getImageIcon().getImage(),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true,
				true);
        ts.setTexture(t1 ,0);


        Texture t2 = TextureManager.loadTexture(TestTerrain.class.getClassLoader().getResource("jmetest/data/texture/Detail.jpg"),
		        Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true);
        ts.setTexture( t2,1);
        t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

        t1.setApply(Texture.AM_COMBINE);
        t1.setCombineFuncRGB(Texture.ACF_MODULATE);
        t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
        t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
        t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
        t1.setCombineScaleRGB(0);

        t2.setApply(Texture.AM_COMBINE);
        t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
        t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
        t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
        t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
        t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
        t2.setCombineScaleRGB(0);
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
        fps.setRenderState(as1);


        scene.setRenderState(buf);
        root.attachChild(scene);
        root.attachChild(fps);
        root.setForceView(true);

        root.updateGeometricState(0.0f, true);
        root.updateRenderState();

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
