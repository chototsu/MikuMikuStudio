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
import com.jme.bounding.BoundingSphere;
import com.jme.app.VariableTimestepGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.ClodMesh;
import com.jme.scene.model.Model;
import com.jme.scene.model.ase.ASEModel;
//import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.shape.Disk;

/**
 * <code>TestClodMesh</code> shows off the use of the ClodMesh in jME.
 *
 * keys:
 * +,-  Change level of detail
 * L    Toggle lights
 * T    Toggle Wireframe mode
 * M    Toggle Model or Disc
 *
 * @author Joshua Slack
 * @version $Id: TestClodMesh.java,v 1.15 2004-08-25 18:12:20 renanse Exp $
 */

public class TestClodMesh extends SimpleGame {

  private Model model;

  private ClodMesh cNode, cNode2;
  private boolean useModel = true;
  private long lastPress = 0;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestClodMesh app = new TestClodMesh();
    app.setDialogBehaviour(VariableTimestepGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {

    if (System.currentTimeMillis() - lastPress > 100) {
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("detail_down")) {
        if (useModel)
          cNode2.setTargetRecord( (cNode2.getTargetRecord()) + 10);
        else
          cNode.setTargetRecord( (cNode.getTargetRecord()) + 25);
        lastPress = System.currentTimeMillis();
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("detail_up")) {
        if (useModel)
          cNode2.setTargetRecord( (cNode2.getTargetRecord()) - 10);
        else
          cNode.setTargetRecord( (cNode.getTargetRecord()) - 25);
        lastPress = System.currentTimeMillis();
      }
    }
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("switch_models", false)) {
      useModel = !useModel;
      cNode.setForceCull(useModel);
      cNode2.setForceCull(!useModel);
    }
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {

    display.setTitle("Imposter Test");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 25.0f));
    cam.update();
    KeyBindingManager.getKeyBindingManager().set(
        "detail_up",
        KeyInput.KEY_ADD);
    KeyBindingManager.getKeyBindingManager().set(
        "detail_down",
        KeyInput.KEY_SUBTRACT);
    KeyBindingManager.getKeyBindingManager().set(
        "switch_models",
        KeyInput.KEY_M);

    model = new ASEModel("Statue of Liberty");
    URL data = TestClodMesh.class.getClassLoader()
        .getResource("jmetest/data/model/Statue.ase");
    model.load(data, "jmetest/data/model/");

//    model = new Md2Model("Dr Freak");
//    model.load(TestClodMesh.class.getClassLoader()
//               .getResource("jmetest/data/model/drfreak.md2"));

    model.updateGeometricState(0, true);

    cNode = new ClodMesh("model", new Disk("disc", 50, 50, 8), null);
    rootNode.attachChild(cNode);
    cNode.setForceCull(true);
    cNode.setModelBound(new BoundingSphere());
    cNode.updateModelBound();

    TriMesh child = (TriMesh) model.getChild(0);
    cNode2 = new ClodMesh("model", child, null);
    rootNode.attachChild(cNode2);
    cNode2.setForceCull(false);
    cNode2.setModelBound(new BoundingSphere());
    cNode2.updateModelBound();
  }
}
