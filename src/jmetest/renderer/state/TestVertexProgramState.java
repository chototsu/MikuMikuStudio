/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package jmetest.renderer.state;

import com.jme.app.VariableTimestepGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * @author Eric Woroshow
 * @version $Id: TestVertexProgramState.java,v 1.7 2004-04-22 22:27:44 renanse Exp $
 */
public class TestVertexProgramState extends VariableTimestepGame {

    /** The position of the light in object space */
    private final float[] lightPosition = { -0.8f, 0.8f, 0.8f, 0.0f };

    private Camera cam;
    private NodeHandler control;
    private Node scene;

    /**
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update(float deltaT) {
        control.update(deltaT * 100);
    }

    /**
     * Render the scene. Start drawing at the root node, and let
     * our scene graph take care of the rest!
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render(float f) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);
    }

    /**
     * Set up the display system and camera.
     * @see com.jme.app.AbstractGame#initSystem()
     */
    protected void initSystem() {
        try {
            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(properties.getWidth(), properties.getHeight(),
                    properties.getDepth(), properties.getFreq(), properties
                            .getFullscreen());
            display.setTitle("Vertex Programs");
            display.setMinSamples(4);
            cam = display.getRenderer().getCamera(properties.getWidth(),
                    properties.getHeight());
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA blackColor = new ColorRGBA(0.02f, 0.0f, 0.776f, 1.0f);
        display.getRenderer().setBackgroundColor(blackColor);

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 50.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);
    }

    /**
     * Set up the scene.
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        //Enable depth testing
        ZBufferState zstate = display.getRenderer().getZBufferState();
        zstate.setEnabled(true);

        //Create the root node
        scene = new Node("scene");
        scene.setRenderState(zstate);

        //To acheive a cartoon render look, we attatch both a cel shaded
        //torus and its outline to the scene. The two torii occupy the
        //same space, so the outline will overlap and highlight the lit torus.
		Torus shaded = createShadedTorus(), outline = createOutlineTorus();
        scene.attachChild(shaded);
        scene.attachChild(outline);
//
//        AttributeState as = display.getRenderer().getAttributeState();
//        as.setEnabled(true);
//        as.setMask(AttributeState.ALL_ATTRIB_BIT);
//
//        scene.setRenderState(as);

        //Allow the torus to be controlled by the mouse.
        //By attatching the controller to the scene root, we can manipulate
        //both torii at once, thus guaranteeing that the outline and shaded
        //version will never be out of sync.
        control = new NodeHandler(this, scene, properties.getRenderer());
        scene.updateRenderState();
    }

    private Torus createShadedTorus() {
        //Load the vertex program from a file and bind it to a render state
        VertexProgramState vp = display.getRenderer().getVertexProgramState();
        vp.setParameter(lightPosition, 8);
        vp.load(TestVertexProgramState.class.getClassLoader().getResource(
                "jmetest/data/images/celshaderARB.vp"));
        vp.setEnabled(true);

        //Bind a 1-dimensional luminance texture for use by the vertex program
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(
                TestVertexProgramState.class.getClassLoader().getResource(
                        "jmetest/data/images/shader.png"),
                        Texture.MM_NEAREST, Texture.FM_NEAREST, false));

        //Generate the torus
        Torus torus = new Torus("shadedTorus", 128, 32, 3.0f, 5.0f);
        torus.setRenderState(vp);
        torus.setRenderState(ts);

		return torus;
    }

    private Torus createOutlineTorus() {
        CullState cs = display.getRenderer().getCullState();
        cs.setCullMode(CullState.CS_FRONT);
        cs.setEnabled(true);

        WireframeState ws = display.getRenderer().getWireframeState();
        ws.setLineWidth(6.0f);
        ws.setFace(WireframeState.WS_FRONT);
        ws.setEnabled(true);

        Torus torus = new Torus("outlineTorus", 128, 32, 3.0f, 5.0f);

        ColorRGBA black = new ColorRGBA(0f, 0f, 0f, 1f);
        ColorRGBA[] colors = new ColorRGBA[torus.getVertices().length];
        for (int i = 0; i < colors.length; i++)
            colors[i] = black;

        torus.setColors(colors);
        torus.setRenderState(cs);
        torus.setRenderState(ws);

        return torus;
    }

    /**
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {}

    /**
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {}

    public static void main(String[] args) {
        TestVertexProgramState app = new TestVertexProgramState();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
