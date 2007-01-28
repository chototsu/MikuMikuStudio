/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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
package com.jmex.game.state;

import com.jme.image.*;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import com.jme.util.geom.*;
import com.jmex.game.*;

/**
 * <code>TestGameState</code> provides an extremely basic gamestate with
 * various testing features pre-implemented. The preferred way to utilize this
 * is to instantiate your game, instantiate the TestGameState, register it with
 * <code>GameStateManager</code>, and then use the getRootNode() method on
 * TestGameState to get the root node, or simply extend this class to create
 * your own test scenario.
 * 
 * @author Matthew D. Hicks
 */
public class DebugGameState extends GameState {
	private static final String FONT_LOCATION = "/com/jme/app/defaultfont.tga";
	
    protected Node rootNode;
    protected InputHandler input;
    protected WireframeState wireState;
    protected LightState lightState;
    protected boolean pause;
    protected boolean showBounds = false;
    protected boolean showDepth = false;
    protected boolean showNormals = false;
    
    private Timer timer;
    private Text fps;
	private Node fpsNode;

    public DebugGameState() {
        init();
    }

    private void init() {
        rootNode = new Node("RootNode");

        // Create a wirestate to toggle on and off. Starts disabled with default
        // width of 1 pixel.
        wireState = DisplaySystem.getDisplaySystem().getRenderer()
                .createWireframeState();
        wireState.setEnabled(false);
        rootNode.setRenderState(wireState);

        // Create ZBuffer for depth
        ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer()
                .createZBufferState();
        zbs.setEnabled(true);
        zbs.setFunction(ZBufferState.CF_LEQUAL);
        rootNode.setRenderState(zbs);

        // Initial InputHandler
        input = new FirstPersonHandler(DisplaySystem.getDisplaySystem().getRenderer().getCamera(), 5.0f, 1.0f);

        // Signal to the renderer that it should keep track of rendering
        // information.
        DisplaySystem.getDisplaySystem().getRenderer().enableStatistics(true);

        initKeyBindings();

        PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3f(100.0f, 100.0f, 100.0f));
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = DisplaySystem.getDisplaySystem().getRenderer()
                .createLightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        rootNode.setRenderState(lightState);

        // Create FPS counter
        timer = Timer.getTimer();
        
        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
        
        TextureState font = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		font.setTexture(TextureManager.loadTexture(StandardGame.class.getResource(FONT_LOCATION),
						Texture.MM_LINEAR, Texture.FM_LINEAR));
		font.setEnabled(true);
        
        fps = new Text("FPS label", "");
		fps.setTextureCombineMode(TextureState.REPLACE);
		fpsNode = new Node("FPS node");
		fpsNode.attachChild(fps);
		fpsNode.setRenderState(font);
		fpsNode.setRenderState(as);
		fpsNode.updateGeometricState(0.0f, true);
		fpsNode.updateRenderState();
        
        // Finish up
        rootNode.updateRenderState();
        rootNode.updateWorldBound();
        rootNode.updateGeometricState(0.0f, true);
    }

    private void initKeyBindings() {
        /** Assign key P to action "toggle_pause". */
        KeyBindingManager.getKeyBindingManager().set("toggle_pause",
                KeyInput.KEY_P);
        /** Assign key T to action "toggle_wire". */
        KeyBindingManager.getKeyBindingManager().set("toggle_wire",
                KeyInput.KEY_T);
        /** Assign key L to action "toggle_lights". */
        KeyBindingManager.getKeyBindingManager().set("toggle_lights",
                KeyInput.KEY_L);
        /** Assign key B to action "toggle_bounds". */
        KeyBindingManager.getKeyBindingManager().set("toggle_bounds",
                KeyInput.KEY_B);
        /** Assign key N to action "toggle_normals". */
        KeyBindingManager.getKeyBindingManager().set("toggle_normals",
                KeyInput.KEY_N);
        /** Assign key C to action "camera_out". */
        KeyBindingManager.getKeyBindingManager().set("camera_out",
                KeyInput.KEY_C);
        KeyBindingManager.getKeyBindingManager().set("screen_shot",
                KeyInput.KEY_F1);
        KeyBindingManager.getKeyBindingManager().set("exit",
                KeyInput.KEY_ESCAPE);
        KeyBindingManager.getKeyBindingManager().set("parallel_projection",
                KeyInput.KEY_F2);
        KeyBindingManager.getKeyBindingManager().set("toggle_depth",
                KeyInput.KEY_F3);
        KeyBindingManager.getKeyBindingManager().set("mem_report",
                KeyInput.KEY_R);
    }

    public void update(float tpf) {
        // Update the InputHandler
        input.update(tpf);
    	
        /** If toggle_pause is a valid command (via key p), change pause. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_pause", false)) {
            pause = !pause;
        }
    	
        if (pause)
            return;

		// Update FPS
        timer.update();
		fps.print(Math.round(timer.getFrameRate()) + " fps");
        

        // Update the geometric state of the rootNode
        rootNode.updateGeometricState(tpf, true);

        /** If toggle_wire is a valid command (via key T), change wirestates. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_wire", false)) {
            wireState.setEnabled(!wireState.isEnabled());
            rootNode.updateRenderState();
        }
        /** If toggle_lights is a valid command (via key L), change lightstate. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_lights", false)) {
            lightState.setEnabled(!lightState.isEnabled());
            rootNode.updateRenderState();
        }
        /** If toggle_bounds is a valid command (via key B), change bounds. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_bounds", false)) {
            showBounds = !showBounds;
        }
        /** If toggle_depth is a valid command (via key F3), change depth. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_depth", false)) {
            showDepth = !showDepth;
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "toggle_normals", false)) {
            showNormals = !showNormals;
        }
        /** If camera_out is a valid command (via key C), show camera location. */
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "camera_out", false)) {
            System.err.println("Camera at: "
                    + DisplaySystem.getDisplaySystem().getRenderer()
                            .getCamera().getLocation());
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "screen_shot", false)) {
            DisplaySystem.getDisplaySystem().getRenderer().takeScreenShot(
                    "SimpleGameScreenShot");
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "parallel_projection", false)) {
            if (DisplaySystem.getDisplaySystem().getRenderer().getCamera()
                    .isParallelProjection()) {
                cameraPerspective();
            } else {
                cameraParallel();
            }
        }
        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "mem_report", false)) {
            long totMem = Runtime.getRuntime().totalMemory();
            long freeMem = Runtime.getRuntime().freeMemory();
            long maxMem = Runtime.getRuntime().maxMemory();

            System.err.println("|*|*|  Memory Stats  |*|*|");
            System.err.println("Total memory: " + (totMem >> 10) + " kb");
            System.err.println("Free memory: " + (freeMem >> 10) + " kb");
            System.err.println("Max memory: " + (maxMem >> 10) + " kb");
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("exit",
                false)) {
            System.exit(0);
        }
    }

    protected void cameraPerspective() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        Camera cam = display.getRenderer().getCamera();
        cam.setFrustumPerspective(45.0f, (float) display.getWidth()
                / (float) display.getHeight(), 1, 1000);
        cam.setParallelProjection(false);
        cam.update();
    }

    protected void cameraParallel() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        Camera cam = display.getRenderer().getCamera();
        cam.setParallelProjection(true);
        float aspect = (float) display.getWidth() / display.getHeight();
        cam.setFrustum(-100.0f, 1000.0f, -50.0f * aspect, 50.0f * aspect,
                -50.0f, 50.0f);
        cam.update();
    }

    public void render(float tpf) {
        // Render the rootNode
        DisplaySystem.getDisplaySystem().getRenderer().draw(rootNode);

        if (showBounds) {
            Debugger.drawBounds(rootNode, DisplaySystem.getDisplaySystem()
                    .getRenderer(), true);
        }

        if (showNormals) {
            Debugger.drawNormals(rootNode, DisplaySystem.getDisplaySystem()
                    .getRenderer());
        }

        if (showDepth) {
            DisplaySystem.getDisplaySystem().getRenderer().renderQueue();
            Debugger.drawBuffer(Texture.RTT_SOURCE_DEPTH, Debugger.NORTHEAST,
                    DisplaySystem.getDisplaySystem().getRenderer());
        }
        
		// Render FPS
		DisplaySystem.getDisplaySystem().getRenderer().draw(fpsNode);
    }

    public void cleanup() {
    }

    public Node getRootNode() {
        return rootNode;
    }
}
