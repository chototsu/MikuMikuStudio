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
package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Box;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestScenegraph</code>
 */
public class TestScenegraph extends SimpleGame {
    private Camera cam;
    private Node root, scene;
    private NodeHandler input, nc1, nc2, nc3, nc4, nc5, nc6;
    private Thread thread;
    private Timer timer;
    private Box box1, box2, box3, box4, box5, box6;
    private Box selectionBox;
    private Node node1, node2, node3, node4, node5, node6;
    private Text text;
    private Node selectedNode;
    private TextureState ts, ts2, ts3;

    private KeyInput key;
    private boolean drawBounds = false;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestScenegraph app = new TestScenegraph();
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
        scene.updateGeometricState(0.0f, true);

        selectionBox.setLocalTranslation(selectedNode.getWorldTranslation());
        selectionBox.setLocalRotation(selectedNode.getWorldRotation());

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("tex1")) {
            selectedNode.setRenderState(ts);
        }

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("tex2")) {
            selectedNode.setRenderState(ts2);
        }

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("tex3")) {
            selectedNode.setRenderState(ts3);
        }

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("notex")) {
            selectedNode.clearRenderState(RenderState.RS_TEXTURE);
        }

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("tog_bounds")) {
            drawBounds = !drawBounds;
        }
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(root);
        if (drawBounds)
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

        // setup our camera
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, -100.0f);
        Vector3f left = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, 1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        // Setup the input controller and timer
        //input = new FirstPersonHandler(this, cam, "LWJGL");
        //input.setKeySpeed(10f);
        //input.setMouseSpeed(1f);
        timer = Timer.getTimer("LWJGL");

        display.setTitle("Test Scene Graph");
        display.getRenderer().enableStatistics(true);

        InputSystem.createInputSystem(properties.getRenderer());
        key = InputSystem.getKeyInput();
        KeyBindingManager.getKeyBindingManager().setKeyInput(key);

        KeyBindingManager.getKeyBindingManager().set(
                "notex",
                KeyInput.KEY_7);

        KeyBindingManager.getKeyBindingManager().set(
                "tex1",
                KeyInput.KEY_8);

        KeyBindingManager.getKeyBindingManager().set(
                "tex2",
                KeyInput.KEY_9);

        KeyBindingManager.getKeyBindingManager().set(
                "tex3",
                KeyInput.KEY_0);

        KeyBindingManager.getKeyBindingManager().set(
                "tog_bounds",
                KeyInput.KEY_B);
    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        Vector3f min = new Vector3f(-5, -5, -5);
        Vector3f max = new Vector3f(5, 5, 5);
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);

        SpotLight am = new SpotLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, 0));
        am.setLocation(new Vector3f(25, 100, 0));
        am.setAngle(15);

        SpotLight am2 = new SpotLight();
        am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am2.setDirection(new Vector3f(0, 0, 0));
        am2.setLocation(new Vector3f(250, 10, 100));
        am2.setAngle(15);

        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0, 0 , 150));

        LightState state = display.getRenderer().getLightState();
        state.setEnabled(true);
        state.attach(am);
        state.attach(dr);
        state.attach(am2);
        am.setEnabled(true);
        am2.setEnabled(true);
        dr.setEnabled(true);

        TextureState font = display.getRenderer().getTextureState();
        font.setTexture(
            TextureManager.loadTexture(
                TestScenegraph.class.getClassLoader().getResource(
                    "jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        font.setEnabled(true);
        text = new Text("Selected Node", "Selected Node: Node 1");
        text.setRenderState(font);
        text.setRenderState(as1);

        scene = new Node("3D Scene Node");
        root = new Node("Root Scene Node");
        root.attachChild(text);

        CullState cs = display.getRenderer().getCullState();
        cs.setCullMode(CullState.CS_BACK);
        cs.setEnabled(true);
        scene.setRenderState(cs);





        // Setup our params for the depth buffer
        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);

        ColorRGBA[] green = new ColorRGBA[24];
        for(int i = 0; i < 24; i++) {
            green[i] = new ColorRGBA(0,1,0,0.5f);
        }

        selectionBox = new Box("Selection", min.mult(1.25f), max.mult(1.25f));
        selectionBox.setColors(green);
        selectionBox.setRenderState(as1);
        selectionBox.setModelBound(new BoundingSphere());
        selectionBox.updateModelBound();


        node1 = new Node("Node 1");
        box1 = new Box("Box 1", min, max);
        node1.attachChild(box1);
        node1.setLocalTranslation(new Vector3f(0, 30, 0));
        selectedNode = node1;
        node1.setRenderState(state);
        box1.setModelBound(new BoundingSphere());
        box1.updateModelBound();

        node2 = new Node("Node 2");
        box2 = new Box("Box 2", min, max);
        node2.attachChild(box2);
        node1.attachChild(node2);
        node2.setLocalTranslation(new Vector3f(-20, -20, 0));
        box2.setModelBound(new BoundingSphere());
        box2.updateModelBound();

        node3 = new Node("Node 3");
        box3 = new Box("Box 3", min, max);
        node3.attachChild(box3);
        node1.attachChild(node3);
        node3.setLocalTranslation(new Vector3f(20, -20, 0));
        box3.setModelBound(new BoundingSphere());
        box3.updateModelBound();

        node4 = new Node("Node 4");
        box4 = new Box("Box 4", min, max);
        node4.attachChild(box4);
        node2.attachChild(node4);
        node4.setLocalTranslation(new Vector3f(-20, -20, 0));
        box4.setModelBound(new BoundingSphere());
        box4.updateModelBound();

        node5 = new Node("Node 5");
        box5 = new Box("Box 5", min, max);
        node5.attachChild(box5);
        node2.attachChild(node5);
        node5.setLocalTranslation(new Vector3f(20, -20, 0));
        box5.setModelBound(new BoundingSphere());
        box5.updateModelBound();

        node6 = new Node("Node 6");
        box6 = new Box("Box 6", min, max);
        node6.attachChild(box6);
        node3.attachChild(node6);
        node6.setLocalTranslation(new Vector3f(0, -20, 0));
        box6.setModelBound(new BoundingSphere());
        box6.updateModelBound();

        Vector3f[] lines = {node1.getWorldTranslation(), node2.getWorldTranslation(),
                            node1.getWorldTranslation(), node3.getWorldTranslation(),
                            node2.getWorldTranslation(), node4.getWorldTranslation(),
                            node2.getWorldTranslation(), node5.getWorldTranslation(),
                            node3.getWorldTranslation(), node6.getWorldTranslation()};
        Line line = new Line("Connection", lines, null, null, null);

        ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                TestBoxColor.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true);
        ts.setTexture(t1);

        ts2 = display.getRenderer().getTextureState();
        ts2.setEnabled(true);
        Texture t2 = TextureManager.loadTexture(
                TestBoxColor.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true);
        ts2.setTexture(t2);

        ts3 = display.getRenderer().getTextureState();
        ts3.setEnabled(true);
        Texture t3 = TextureManager.loadTexture(
                TestBoxColor.class.getClassLoader().getResource("jmetest/data/texture/snowflake.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true);
        ts3.setTexture(t3);

        node1.setRenderState(ts);

        scene.attachChild(node1);
        root.attachChild(line);
        root.attachChild(scene);
        scene.attachChild(selectionBox);
        cam.update();
        scene.updateGeometricState(0.0f, true);

        nc1 = new NodeHandler(this, node1, "LWJGL");
        nc2 = new NodeHandler(this, node2, "LWJGL");
        nc3 = new NodeHandler(this, node3, "LWJGL");
        nc4 = new NodeHandler(this, node4, "LWJGL");
        nc5 = new NodeHandler(this, node5, "LWJGL");
        nc6 = new NodeHandler(this, node6, "LWJGL");
        nc1.setKeySpeed(5);
        nc1.setMouseSpeed(1);
        nc2.setKeySpeed(5);
        nc2.setMouseSpeed(1);
        nc3.setKeySpeed(5);
        nc3.setMouseSpeed(1);
        nc4.setKeySpeed(5);
        nc4.setMouseSpeed(1);
        nc5.setKeySpeed(5);
        nc5.setMouseSpeed(1);
        nc6.setKeySpeed(5);
        nc6.setMouseSpeed(1);

        input = nc1;

        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        keyboard.set("node1", KeyInput.KEY_1);
        keyboard.set("node2", KeyInput.KEY_2);
        keyboard.set("node3", KeyInput.KEY_3);
        keyboard.set("node4", KeyInput.KEY_4);
        keyboard.set("node5", KeyInput.KEY_5);
        keyboard.set("node6", KeyInput.KEY_6);
        TestNodeSelectionAction s1 = new TestNodeSelectionAction(this, 1);
        s1.setKey("node1");
        nc1.addAction(s1);
        nc2.addAction(s1);
        nc3.addAction(s1);
        nc4.addAction(s1);
        nc5.addAction(s1);
        nc6.addAction(s1);
        TestNodeSelectionAction s2 = new TestNodeSelectionAction(this, 2);
        s2.setKey("node2");
        nc1.addAction(s2);
        nc2.addAction(s2);
        nc3.addAction(s2);
        nc4.addAction(s2);
        nc5.addAction(s2);
        nc6.addAction(s2);
        TestNodeSelectionAction s3 = new TestNodeSelectionAction(this, 3);
        s3.setKey("node3");
        nc1.addAction(s3);
        nc2.addAction(s3);
        nc3.addAction(s3);
        nc4.addAction(s3);
        nc5.addAction(s3);
        nc6.addAction(s3);
        TestNodeSelectionAction s4 = new TestNodeSelectionAction(this, 4);
        s4.setKey("node4");
        nc1.addAction(s4);
        nc2.addAction(s4);
        nc3.addAction(s4);
        nc4.addAction(s4);
        nc5.addAction(s4);
        nc6.addAction(s4);
        TestNodeSelectionAction s5 = new TestNodeSelectionAction(this, 5);
        s5.setKey("node5");
        nc1.addAction(s5);
        nc2.addAction(s5);
        nc3.addAction(s5);
        nc4.addAction(s5);
        nc5.addAction(s5);
        nc6.addAction(s5);
        TestNodeSelectionAction s6 = new TestNodeSelectionAction(this, 6);
        s6.setKey("node6");
        nc1.addAction(s6);
        nc2.addAction(s6);
        nc3.addAction(s6);
        nc4.addAction(s6);
        nc5.addAction(s6);
        nc6.addAction(s6);


    }

    public void setSelectedNode(int node) {
        switch (node) {
            case 1 :
                input = nc1;
                text.print("Selected Node: Node 1");
                selectedNode = node1;
                break;
            case 2 :
                input = nc2;
                text.print("Selected Node: Node 2");
                selectedNode = node2;
                break;
            case 3 :
                input = nc3;
                text.print("Selected Node: Node 3");
                selectedNode = node3;
                break;
            case 4 :
                input = nc4;
                text.print("Selected Node: Node 4");
                selectedNode = node4;
                break;
            case 5 :
                input = nc5;
                text.print("Selected Node: Node 5");
                selectedNode = node5;
                break;
            case 6 :
                input = nc6;
                text.print("Selected Node: Node 6");
                selectedNode = node6;
                break;
        }
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
