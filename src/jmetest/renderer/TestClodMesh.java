/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetest.renderer.loader.TestASEJmeWrite;

import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.ClodMesh;
import com.jme.scene.shape.Disk;
import com.jme.util.TextureKey;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.XMLparser.Converters.AseToJme;

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
 * @version $Id: TestClodMesh.java,v 1.27 2007-08-17 10:34:34 rherlitz Exp $
 */

public class TestClodMesh extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestClodMesh.class
            .getName());

  private Node model;

  private ClodMesh cNode, cNode2;
  private boolean useModel = true;
  private long lastPress = 0;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestClodMesh app = new TestClodMesh();
    app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
      if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
              "load_here", false ) ) {
          try {
              rootNode = null;
              rootNode = (Node)BinaryImporter.getInstance().load(new File("rootNode.save"));
              rootNode.updateGeometricState(0, true);
              rootNode.updateRenderState();
              cNode2 = (ClodMesh)rootNode.getChild(1);
              logger.info("loaded here successfully");
          } catch (IOException e) {
              logger.logp(Level.SEVERE, this.getClass().toString(), "simpleUpdate()", "Exception",
                    e);
          }
      }

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
      cNode.setCullMode(useModel ? SceneElement.CULL_ALWAYS : SceneElement.CULL_DYNAMIC);
      cNode2.setCullMode(!useModel ? SceneElement.CULL_ALWAYS : SceneElement.CULL_DYNAMIC);
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
    KeyBindingManager.getKeyBindingManager().set( "load_here", KeyInput.KEY_F7);
    KeyBindingManager.getKeyBindingManager().set(
        "detail_up",
        KeyInput.KEY_ADD);
    KeyBindingManager.getKeyBindingManager().set(
        "detail_down",
        KeyInput.KEY_SUBTRACT);
    KeyBindingManager.getKeyBindingManager().set(
        "switch_models",
        KeyInput.KEY_M);
    
    InputStream statue=TestASEJmeWrite.class.getClassLoader().getResourceAsStream("jmetest/data/model/Statue.ase");
    URL stateTextureDir=TestASEJmeWrite.class.getClassLoader().getResource("jmetest/data/model/");
    if (statue==null){
        logger.info("Unable to find statue file, did you include jme-test.jar in classpath?");
        System.exit(0);
    }
    AseToJme i=new AseToJme();
    ByteArrayOutputStream BO=new ByteArrayOutputStream();
    try {
        i.convert(statue,BO);
        TextureKey.setOverridingLocation(stateTextureDir);
        model=(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {}

    model.updateGeometricState(0, true);

    cNode = new ClodMesh("model", new Disk("disc", 50, 50, 8), null);
    rootNode.attachChild(cNode);
    cNode.setCullMode(SceneElement.CULL_ALWAYS);
    cNode.setModelBound(new BoundingSphere());
    cNode.updateModelBound();

    Spatial child = model.getChild(0);
    while(child instanceof Node) {
    	child = ((Node)child).getChild(0);
    }
    
    cNode2 = new ClodMesh("model", (TriMesh)child, null);
    rootNode.attachChild(cNode2);
    cNode2.setCullMode(SceneElement.CULL_DYNAMIC);
    cNode2.setModelBound(new BoundingSphere());
    cNode2.updateModelBound();
  }
}
