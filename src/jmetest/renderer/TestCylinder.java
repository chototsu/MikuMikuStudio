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
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestCylinder</code>
 * @author Mark Powell
 * @version $Id: TestCylinder.java,v 1.8 2004-04-23 00:33:41 renanse Exp $
 */
public class TestCylinder extends SimpleGame {

  private Quaternion rotQuat = new Quaternion();
  private float angle = 0;
  private Vector3f axis = new Vector3f(1, 1, 0);
  private Cylinder t;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestCylinder app = new TestCylinder();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate(float interpolation) {
    if (timer.getTimePerFrame() < 1) {
      angle = angle + (timer.getTimePerFrame() * 1);
      if (angle > 360)
        angle = 0;
    }

    rotQuat.fromAngleAxis(angle, axis);
    t.setLocalRotation(rotQuat);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Cylinder Test");

    t = new Cylinder("Cylinder", 20, 50, 5, 10);
    t.setModelBound(new BoundingBox());
    t.updateModelBound();
    rootNode.attachChild(t);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestBoxColor.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    rootNode.setRenderState(ts);

    lightState.setTwoSidedLighting(true);
  }

}
