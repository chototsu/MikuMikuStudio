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

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.app.VariableTimestepGame;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.model.Model;
import com.jme.scene.model.ase.ASEModel;
//import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.shape.Disk;

/**
 * <code>TestAutoClodMesh</code> shows off the use of the AreaClodMesh in jME.
 *
 * keys:
 * L    Toggle lights
 * T    Toggle Wireframe mode
 * M    Toggle Model or Disc
 *
 * @author Joshua Slack
 * @version $Id: TestAutoClodMesh.java,v 1.5 2004-04-23 05:06:44 renanse Exp $
 */

public class TestAutoClodMesh extends SimpleGame {
  private Model model;

  private AreaClodMesh iNode, iNode2;
  private boolean useModel = true;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestAutoClodMesh app = new TestAutoClodMesh();
    app.setDialogBehaviour(VariableTimestepGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("switch_models", false)) {
      useModel = !useModel;
      iNode.setForceCull(useModel);
      iNode2.setForceCull(!useModel);
    }
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    KeyBindingManager.getKeyBindingManager().set(
        "switch_models",
        KeyInput.KEY_M);

    display.setTitle("Auto-Change Clod Test (using AreaClodMesh)");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 25.0f));
    cam.update();

    input.setKeySpeed(25);

    model = new ASEModel("Statue of Liberty");
    URL data = TestAutoClodMesh.class.getClassLoader()
        .getResource("jmetest/data/model/Statue.ase");
    model.load(data, "jmetest/data/model/");

//    model = new Md2Model("Dr Freak");
//    model.load(TestAutoClodMesh.class.getClassLoader()
//               .getResource("jmetest/data/model/drfreak.md2"));

    model.updateGeometricState(0, true);

    iNode = new AreaClodMesh("model", new Disk("disc", 50, 50, 8), null);
    iNode.setForceCull(true);
    iNode.setModelBound(new BoundingSphere());
    iNode.updateModelBound();

    TriMesh child = (TriMesh) model.getChild(0);
    iNode2 = new AreaClodMesh("model", child, null);
    iNode2.setForceCull(false);
    iNode2.setModelBound(new BoundingSphere());
    iNode2.updateModelBound();

    rootNode.attachChild(iNode);
    rootNode.attachChild(iNode2);
  }
}
