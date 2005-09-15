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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jmetest.renderer.loader.TestASEJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.app.VariableTimestepGame;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Disk;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.AseToJme;

/**
 * <code>TestAutoClodMesh</code> shows off the use of the AreaClodMesh in jME.
 *
 * keys:
 * L    Toggle lights
 * T    Toggle Wireframe mode
 * M    Toggle Model or Disc
 *
 * @author Joshua Slack
 * @version $Id: TestAutoClodMesh.java,v 1.12 2005-09-15 17:13:22 renanse Exp $
 */

public class TestAutoClodMesh extends SimpleGame {
  private Node model;

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

    InputStream statue=TestASEJmeWrite.class.getClassLoader().getResourceAsStream("jmetest/data/model/Statue.ase");
    URL stateTextureDir=TestASEJmeWrite.class.getClassLoader().getResource("jmetest/data/model/");
    if (statue==null){
        System.out.println("Unable to find statue file, did you include jme-test.jar in classpath?");
        System.exit(0);
    }
    AseToJme i=new AseToJme();
    ByteArrayOutputStream BO=new ByteArrayOutputStream();
    try {
        i.convert(statue,BO);
        JmeBinaryReader jbr=new JmeBinaryReader();
        jbr.setProperty("texurl",stateTextureDir);
        model=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {

    }

    model.updateGeometricState(0, true);

    iNode = new AreaClodMesh("model", new Disk("disc", 50, 50, 8), null);
    rootNode.attachChild(iNode);
    iNode.setForceCull(true);
    iNode.setModelBound(new BoundingSphere());
    iNode.updateModelBound();

    Spatial child = model.getChild(0);
    while(child instanceof Node) {
    	child = ((Node)child).getChild(0);
    }

    iNode2 = new AreaClodMesh("model", (TriMesh)child, null);
    rootNode.attachChild(iNode2);
    iNode2.setDistanceTolerance( 0.0f);
    iNode2.setForceCull(false);
    iNode2.setModelBound(new BoundingSphere());
    iNode2.updateModelBound();

  }
}
