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
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Clone;
import com.jme.scene.CloneNode;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.util.LoggingSystem;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestManyChildren.java,v 1.14 2004-04-23 04:01:29 renanse Exp $
 */
public class TestManyChildren extends SimpleGame {

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
    TestManyChildren app = new TestManyChildren();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("2,500 Box Test");
    Vector3f max = new Vector3f(0.5f, 0.5f, 0.5f);
    Vector3f min = new Vector3f( -0.5f, -0.5f, -0.5f);

    CloneNode scene = new CloneNode("Clone node");
    TriMesh t = new Box("Box", min, max);
    t.setModelBound(new BoundingBox());
    t.updateModelBound();
    scene.setGeometry(t);

    for (int i = 0; i < 2500; i++) {
      float x = (float) FastMath.nextRandomFloat() * 10;
      float y = (float) FastMath.nextRandomFloat() * 10;
      float z = (float) FastMath.nextRandomFloat() * 10;
      Clone c = new Clone("Box Clone " + i);
      c.setLocalTranslation(new Vector3f(x, y, z));
      scene.attachChild(c);
    }

    SpotLight am = new SpotLight();
    am.setDiffuse(new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
    am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am.setDirection(new Vector3f( -30, -10, -5).normalizeLocal());
    am.setLocation(new Vector3f(30, 10, 5));
    am.setAngle(15);

    SpotLight am2 = new SpotLight();
    am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
    am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am2.setDirection(new Vector3f( -30, -10, -20).normalizeLocal());
    am2.setLocation(new Vector3f(30, 10, 20));
    am2.setAngle(10);

    DirectionalLight dr = new DirectionalLight();
    dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    //dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
    dr.setDirection(new Vector3f(150, 0, 150));

    lightState.detachAll();
    lightState.attach(am);
    lightState.attach(am2);
    lightState.attach(dr);
    am.setEnabled(true);
    am2.setEnabled(true);
    dr.setEnabled(true);
    rootNode.attachChild(scene);
  }
}
