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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jmetest.renderer.loader.TestASEJmeWrite;
import jmetest.renderer.loader.TestMilkJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.AseToJme;
import com.jmex.model.XMLparser.Converters.MilkToJme;

/**
 * <code>TestSharedMesh</code>
 * @author Mark Powell
 * @version $Id: TestSharedNode.java,v 1.2 2005-06-04 22:52:44 Mojomonkey Exp $
 */
public class TestSharedNode extends SimpleGame {

  private Quaternion rotQuat = new Quaternion();
  private float angle = 0;
  private Vector3f axis = new Vector3f(1, 1, 0);
  Node file = null;
  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
    TestSharedNode app = new TestSharedNode();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
      file.updateGeometricState(1, true);
  }
  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("jME - Sphere");

    URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
    "jmetest/data/model/msascii/run.ms3d");
    ByteArrayOutputStream BO=new ByteArrayOutputStream();
    if (MSFile==null){
        System.out.println("Unable to find milkshape file, did you include jme-test.jar in classpath?");
        System.exit(0);
    }
    MilkToJme convert=new MilkToJme();
    //ByteArrayOutputStream BO=new ByteArrayOutputStream();
    
    try {
        convert.convert(MSFile.openStream(),BO);
        JmeBinaryReader jbr=new JmeBinaryReader();
        URL TEXdir=TestMilkJmeWrite.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/");
        jbr.setProperty("texurl",TEXdir);
        file=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {
        System.out.println("Damn exceptions:"+e);
    }
    
    
    
    for(int i = 0; i < 500; i++) {
        SharedNode sm = new SharedNode("Share" + i, file);
        sm.setLocalTranslation(new Vector3f((float)Math.random() * 1000 - 500, (float)Math.random() * 1000 - 500, (float)Math.random() *1000 - 500));
        rootNode.attachChild(sm);
    }
    
    

    
  }
}
