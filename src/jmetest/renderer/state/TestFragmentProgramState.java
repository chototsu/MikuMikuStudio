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

package jmetest.renderer.state;

import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FragmentProgramState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.VertexProgramState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;


/**
 * Demonstrates the use of the GL_ARB_fragment_program extension in jME. Uses a parallax
 * mapping technique outlined in the paper "Parallax Mapping with Offset Limiting:
 * A PerPixel Approximation of Uneven Surfaces".
 * @author Eric Woroshow
 * @version $Id: TestFragmentProgramState.java,v 1.4 2005-09-20 21:51:36 renanse Exp $
 */
public class TestFragmentProgramState extends SimpleGame {
    private final static String BRICK_TEX = "jmetest/data/images/rockwall2.png";
    private final static String BRICK_HEIGHT = "jmetest/data/images/rockwall_height2.png";
    private final static String BRICK_NRML = "jmetest/data/images/rockwall_normal2.png";
    private final static String BRICK_VP = "jmetest/data/images/bump_parallax.vp";
    private final static String BRICK_FP = "jmetest/data/images/bump_parallax.fp";
    
    /** Light positioning */
    private float angle0 = 0.0f, angle1 = 0.0f;

    /**
     * Entry point for the test.
     * @param args command line arguments; unused
     */
    public static void main(String[] args) {
        TestFragmentProgramState app = new TestFragmentProgramState();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
    
    private void initLights( ) {
        //Set up two lights in the scene
        PointLight light0 = new PointLight();
        light0.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light0.setAmbient(new ColorRGBA(0.1f, 0.1f, 0.1f, 0.1f));
        light0.setLocation(new Vector3f(2f, 4f, 1f));
        light0.setEnabled(true);
        
        PointLight light1 = new PointLight();
        light1.setDiffuse(new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f));
        light1.setAmbient(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        light1.setLocation(new Vector3f(2f, 2f, 1f));
        light1.setEnabled(true);

        lightState = display.getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.attach(light0);
        lightState.attach(light1);
        
        rootNode.setRenderState(lightState);        
    }
    
    protected void simpleInitGame() {
        //Set up cull state
        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        cs.setEnabled(true);
       
        //Basic brick texture
        TextureState brick = display.getRenderer().createTextureState();
        
        Texture tex = TextureManager.loadTexture(
                           TestFragmentProgramState.class.getClassLoader().getResource(BRICK_TEX),
                           Texture.MM_LINEAR_LINEAR,
                           Texture.FM_LINEAR);
        tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
        
        //Height map of the brick wall       
        Texture height = TextureManager.loadTexture(
                            TestFragmentProgramState.class.getClassLoader().getResource(BRICK_HEIGHT),
                            Texture.MM_LINEAR_LINEAR,
                            Texture.FM_LINEAR);
        height.setWrap(Texture.WM_WRAP_S_WRAP_T);

        //Normal map of the brick wall
        Texture normal = TextureManager.loadTexture(
                            TestFragmentProgramState.class.getClassLoader().getResource(BRICK_NRML),
                            Texture.MM_LINEAR_LINEAR,
                            Texture.FM_LINEAR);
        normal.setWrap(Texture.WM_WRAP_S_WRAP_T);
        
        brick.setTexture(tex, 0);
        brick.setTexture(normal, 1);
        brick.setTexture(height, 2);
        
        brick.setEnabled(true);        
        
        VertexProgramState vert = display.getRenderer().createVertexProgramState();
        FragmentProgramState frag = display.getRenderer().createFragmentProgramState();
        //Ensure the extensions are supported, else exit immediately
        if (!vert.isSupported() || !frag.isSupported()) {
            com.jme.util.LoggingSystem.getLogger().log(java.util.logging.Level.SEVERE,
            "Your graphics card does not support vertex or fragment programs, and thus cannot run this test.");
            quit();
        }
        
        //Load vertex program
        vert.load(TestFragmentProgramState.class.getClassLoader().getResource(BRICK_VP));
        vert.setEnabled(true);
        
        //Load fragment program       
        frag.load(TestFragmentProgramState.class.getClassLoader().getResource(BRICK_FP));
        frag.setEnabled(true);
        
        Quad q = new Quad("wall", 10f, 10f);
        
        //Set up textures
        q.setRenderState(brick);
        
        FloatBuffer tex1 = BufferUtils.createVector3Buffer(4);
        for (int x = 0; x < 4; x++)
            tex1.put(1.0f).put(0.0f);
        q.setTextureBuffer(tex1, 1);
        
        FloatBuffer tex2 = BufferUtils.createVector3Buffer(4);
        for (int x = 0; x < 4; x++)
            tex2.put(0.0f).put(1.0f);
        q.setTextureBuffer(tex2, 2);
        
        //Set up ARB programs
        q.setRenderState(vert);
        q.setRenderState(frag);
        
        q.setRenderState(cs);
        
        initLights();
        
        rootNode.attachChild(q);
        rootNode.setCullMode(Spatial.CULL_NEVER);
    }
    
    protected void simpleUpdate() {
        angle0 += 0.01111f;
        angle1 += 0.013f;
        
        ((PointLight)lightState.get(0)).setLocation(new Vector3f(2.0f * FastMath.cos(angle0), 2.0f * FastMath.sin(angle0), 1.5f));
        ((PointLight)lightState.get(1)).setLocation(new Vector3f(2.0f * FastMath.cos(angle1), 2.0f * FastMath.sin(angle1), 1.5f));
    }
}
