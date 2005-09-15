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

import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestAnisotropic</code>
 * @author Joshua Slack
 * @version $Id: TestAnisotropic.java,v 1.7 2005-09-15 17:13:20 renanse Exp $
 */
public class TestAnisotropic extends SimpleGame {

  private Quad q;
  private Texture regTexture, anisoTexture;
  private TextureState ts;
  private boolean useAniso = false;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestAnisotropic app = new TestAnisotropic();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
    if (KeyBindingManager.getKeyBindingManager().isValidCommand("aniso", false)) {
        if (useAniso) {
          display.setTitle("Anisotropic Demo - off - press 'f' to switch");
          ts.setTexture(regTexture);
          rootNode.updateRenderState();
        } else {
          display.setTitle("Anisotropic Demo - on - press 'f' to switch");
          ts.setTexture(anisoTexture);
          rootNode.updateRenderState();
        }
        useAniso = !useAniso;
    }
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Anisotropic Demo - off - press 'f' to switch");
    KeyBindingManager.getKeyBindingManager().set("aniso", KeyInput.KEY_F);
    cam.setLocation(new Vector3f(0,10,100));
    cam.update();

    q = new Quad("Quad", 200, 200);
    q.setModelBound(new BoundingSphere());
    q.updateModelBound();
    q.setLocalRotation(new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0}));
    q.setLightCombineMode(LightState.OFF);
    
    FloatBuffer tBuf = q.getTextureBuffer();
    tBuf.clear();
    tBuf.put(0).put(5);
    tBuf.put(0).put(0);
    tBuf.put(5).put(0);
    tBuf.put(5).put(5);

    rootNode.attachChild(q);

    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    regTexture =
        TextureManager.loadTexture(
        TestAnisotropic.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR);
    regTexture.setWrap(Texture.WM_WRAP_S_WRAP_T);

    anisoTexture =
        TextureManager.loadTexture(
        TestAnisotropic.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        ts.getMaxAnisotropic(),
        true);
    anisoTexture.setWrap(Texture.WM_WRAP_S_WRAP_T);

    ts.setTexture(regTexture);
    rootNode.setRenderState(ts);

    lightState.setTwoSidedLighting(true);
  }
}
