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

import org.lwjgl.input.Keyboard;

import com.jme.app.*;
import com.jme.image.Texture;
import com.jme.input.*;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;

/**
 * <code>TestPQTorus</code> demonstrates the construction and animation of
 * a parameterized torus, also known as a pq torus.
 * @author Eric Woroshow
 * @version $Id: TestPQTorus.java,v 1.12 2004-04-22 22:27:41 renanse Exp $
 */
public class TestPQTorus extends SimpleGame {

    private Quaternion rotQuat = new Quaternion();
    private float angle = 0;
    private Vector3f axis = new Vector3f(1, 1, 0);
    private PQTorus t;
    private Text pqText;

    private float p = 1, q = 0;
    private boolean anim = false;
    private float targetP = p, targetQ = q;

  /**
     * Entry point for the test.
     * @param args arguments passed to the program; ignored
     */
    public static void main(String[] args) {
        LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
        TestPQTorus app = new TestPQTorus();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Animates the PQ torus.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void simpleUpdate(float interpolation) {
      pqText.print("P: "+p+"  Q: "+q);

      if (!anim) {
        if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
          if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ||
              Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            targetP -= 1;
          else
            targetP += 1;
          anim = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
          if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ||
              Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            targetQ -= 1;
          else
            targetQ += 1;
          anim = true;
        }

      } else {
        if (targetP != p || targetQ != q) {
          if (FastMath.abs(targetP - p) < .01f)
            p = targetP;
          else if (p < targetP) p += .01f;
          else p -= .01f;

          if (FastMath.abs(targetQ - q) < .01f)
            q = targetQ;
          else if (q < targetQ) q += .01f;
          else q -= .01f;

          generatePQTorus();
          rootNode.updateRenderState();
        } else anim = false;
      }

      if (interpolation < 1) {
        angle = angle + interpolation / 50;
        if (angle > 360) angle = 0;
      }

        rotQuat.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);
        rootNode.setLocalRotation(rotQuat);
    }

    /**
     * builds the trimesh.
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
      display.setTitle("PQ Torus Test");
      pqText = new Text("PQ label", "");
      pqText.setLocalTranslation(new Vector3f(0,20,0));
      pqText.setForceView(true);
      fpsNode.attachChild(pqText);

        //Generate the geometry
        generatePQTorus();

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestBoxColor.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true));

        rootNode.setRenderState(ts);

    }

    private void generatePQTorus(){
      rootNode.detachChild(t);

      //Generate a torus with 128 steps along the torus, 16 radial samples,
      //and a radius of 2.0 units
      t = new PQTorus("torus", p, q, 2.0f, 1.0f, 128, 16);

      //Update the scene
      rootNode.attachChild(t);
    }

}
