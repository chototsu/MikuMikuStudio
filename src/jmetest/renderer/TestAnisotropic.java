/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.scene.shape.Quad;
import com.jme.bounding.BoundingBox;
import com.jme.input.KeyInput;
import com.jme.input.KeyBindingManager;
import com.jme.scene.state.LightState;
import com.jme.math.Vector2f;

/**
 * <code>TestAnisotropic</code>
 * @author Joshua Slack
 * @version $Id: TestAnisotropic.java,v 1.1 2004-06-29 19:33:44 renanse Exp $
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
          display.setTitle("AnisoTropic Demo - off - press 'f' to switch");
          ts.setTexture(regTexture);
          rootNode.updateRenderState();
        } else {
          display.setTitle("AnisoTropic Demo - on - press 'f' to switch");
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
    display.setTitle("AnisoTropic Demo - off - press 'f' to switch");
    KeyBindingManager.getKeyBindingManager().set("aniso", KeyInput.KEY_F);
    cam.setLocation(new Vector3f(0,10,100));
    cam.update();

    q = new Quad("Quad", 200, 200);
    q.setModelBound(new BoundingSphere());
    q.updateModelBound();
    q.setLocalRotation(new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0}));
    q.setLightCombineMode(LightState.OFF);
    q.setTextureCoord(0,0,new Vector2f(0,5));
    q.setTextureCoord(0,1,new Vector2f(0,0));
    q.setTextureCoord(0,2,new Vector2f(5,0));
    q.setTextureCoord(0,3,new Vector2f(5,5));
    q.updateTextureBuffer(0);

    rootNode.attachChild(q);

    ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    regTexture =
        TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    regTexture.setWrap(Texture.WM_WRAP_S_WRAP_T);

    anisoTexture =
        TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    anisoTexture.setWrap(Texture.WM_WRAP_S_WRAP_T);
    anisoTexture.setAnisoLevel(ts.getMaxAnisotropic());

    ts.setTexture(regTexture);
    rootNode.setRenderState(ts);

    lightState.setTwoSidedLighting(true);
  }
}
