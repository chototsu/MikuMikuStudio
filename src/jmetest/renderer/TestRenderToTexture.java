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

import com.jme.app.*;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import org.lwjgl.opengl.*;
import java.nio.*;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 */
public class TestRenderToTexture extends SimpleGame {
    private TriMesh t, t2;
    private Camera cam;
    private Node root;
    private Node scene, fake;
    private InputController input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;
    private TextureState ts;

  /** Pbuffer instance */
  private static Pbuffer pbuffer;

  /** The shared texture */
  private static int tex_handle;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestRenderToTexture app = new TestRenderToTexture();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();

    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        if(timer.getTimePerFrame() < 1) {
            angle = angle + (timer.getTimePerFrame() * 1);
            if(angle > 360) {
                angle = 0;
            }
        }
        rotQuat.fromAngleAxis(angle, axis);
        timer.update();
        input.update(timer.getTimePerFrame());

//        t.setLocalRotation(rotQuat);
        t2.setLocalRotation(rotQuat);
        scene.updateGeometricState(0.0f, true);
        fake.updateGeometricState(0.0f, true);
    }

  private void initPbuffer() {
      try {
          pbuffer = new Pbuffer(800, 600, 32, 0, 0, 0);
          pbuffer.makeCurrent();

          GL.glClearColor(.1f, .1f, .1f, 1f);
          display.getRenderer().getCamera().update();

          GL.glBindTexture(GL.GL_TEXTURE_2D, tex_handle);
          Pbuffer.releaseContext();
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {

        try {

            if (pbuffer.isBufferLost()) {
                System.out.println("Buffer contents lost - will recreate the buffer");
                Pbuffer.releaseContext();
                pbuffer.destroy();
                initPbuffer();
            }

            pbuffer.makeCurrent();
            display.getRenderer().clearBuffers();
            scene.unsetStates();
            display.getRenderer().draw(fake);
            GL.glBindTexture(GL.GL_TEXTURE_2D, tex_handle);
            GL.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_COMPRESSED_RGB, 0, 0, 512, 512, 0);
            pbuffer.releaseContext();
        } catch (Exception e) {
            System.err.println("ouch");
            e.printStackTrace();
            System.exit(0);
        }
        scene.setRenderState(ts);

        display.getRenderer().clearBuffers();
            GL.glPushMatrix();
        display.getRenderer().draw(root);
            GL.glPopMatrix();

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

      if ((Pbuffer.getPbufferCaps() & Pbuffer.PBUFFER_SUPPORTED) == 0) {
          System.out.println("No Pbuffer support!");
          System.exit(1);
      }
      System.out.println("Pbuffer support detected");

        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 75.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonController(this, cam, "LWJGL");
        input.setKeySpeed(5f);
        input.setMouseSpeed(.5f);
        timer = Timer.getTimer("LWJGL");

        rotQuat = new Quaternion();
        axis = new Vector3f(1,1,0.5f);
        display.setTitle("Render to Texture");
        IntBuffer buf =
            ByteBuffer
                .allocateDirect(4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        //Create the texture
        GL.glGenTextures(buf);
        tex_handle = buf.get(0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, tex_handle);
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE );
        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE );
//        GL.glTexParameteri( GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);

        initPbuffer();
    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        TextureState textImage = display.getRenderer().getTextureState();
        textImage.setEnabled(true);
        textImage.setTexture(
            TextureManager.loadTexture(
                TestRenderToTexture.class.getClassLoader().getResource("jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        scene = new Node("3D Scene Node");
        root = new Node("Root Scene Node");
        fake = new Node("Fake node");

        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);



        t = new Box("Box", min,max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(new Vector3f(0,0,0));

        scene.attachChild(t);
        root.attachChild(scene);

        t2 = new Box("Box", min,max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(new Vector3f(0,0,0));

        fake.attachChild(t2);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        DirectionalLight am = new DirectionalLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, 75));

        LightState state = display.getRenderer().getLightState();
        state.attach(am);
        am.setEnabled(true);
        //scene.setRenderState(state);
        scene.setRenderState(buf);
        cam.update();

        ColorRGBA[] colors = new ColorRGBA[24];
        for(int i = 0; i < 24; i++) {
            colors[i] = new ColorRGBA((float)Math.random(),
                    (float)Math.random(),
                    (float)Math.random(),1);
        }
        t2.setColors(colors);


//        TextureState ts = display.getRenderer().getTextureState();
//                ts.setEnabled(true);
            ts = display.getRenderer().getTextureState();
            Texture tex = new Texture();
            ts.setEnabled(true);
            tex.setTextureId(tex_handle);
            tex.setApply(Texture.AM_MODULATE);
            tex.setBlendColor(new ColorRGBA(1, 1, 1, 1));
            tex.setCorrection(Texture.CM_PERSPECTIVE);
            tex.setFilter(GL.GL_LINEAR);
            tex.setMipmapState(GL.GL_LINEAR_MIPMAP_LINEAR);
            tex.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
            ts.setTexture(tex);
//
//                ts.setTexture(
//                    TextureManager.loadTexture(
//                        TestRenderToTexture.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
//                        Texture.MM_LINEAR,
//                        Texture.FM_LINEAR,
//                        true));

        scene.setRenderState(ts);

        scene.updateGeometricState(0.0f, true);

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
