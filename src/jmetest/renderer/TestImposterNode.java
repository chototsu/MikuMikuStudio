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
import java.net.URL;

import jmetest.renderer.loader.TestMd2JmeWrite;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Controller;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.Md2ToJme;
import com.jmex.model.animation.KeyframeController;

/**
 * <code>TestImposterNode</code> shows off the use of the ImposterNode in jME.
 * @author Joshua Slack
 * @version $Id: TestImposterNode.java,v 1.18 2005-09-15 17:13:24 renanse Exp $
 */
public class TestImposterNode extends SimpleGame {
  private Node fakeScene;

  private Node freakmd2;

  private String FILE_NAME = "data/model/drfreak.md2";
  private String TEXTURE_NAME = "data/model/drfreak.jpg";

  private ImposterNode iNode;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestImposterNode app = new TestImposterNode();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Imposter Test");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 25.0f));
    cam.update();

    // setup the scene to be 'impostered'
    
    Md2ToJme converter=new Md2ToJme();
    ByteArrayOutputStream BO=new ByteArrayOutputStream();

    URL textu=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
    URL freak=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
    freakmd2=null;

    try {
        long time = System.currentTimeMillis();
        converter.convert(freak.openStream(),BO);
        System.out.println("Time to convert from md2 to .jme:"+ ( System.currentTimeMillis()-time));
    } catch (IOException e) {
        System.out.println("damn exceptions:" + e.getMessage());
    }
    JmeBinaryReader jbr=new JmeBinaryReader();
    try {
        long time=System.currentTimeMillis();
        freakmd2=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        System.out.println("Time to convert from .jme to SceneGraph:"+ ( System.currentTimeMillis()-time));
    } catch (IOException e) {
        System.out.println("damn exceptions:" + e.getMessage());
    }
    
    ((KeyframeController) freakmd2.getChild(0).getController(0)).setSpeed(10);
    ((KeyframeController) freakmd2.getChild(0).getController(0)).setRepeatType(Controller.RT_WRAP);
    fakeScene = new Node("Fake node");
    fakeScene.attachChild(freakmd2);
    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
    TextureManager.loadTexture(
        textu,
        Texture.MM_LINEAR,
        Texture.FM_LINEAR));
    freakmd2.setRenderState(ts);
    // apply the appropriate texture to the imposter scene
    TextureState ts2 = display.getRenderer().createTextureState();
    ts2.setEnabled(true);
    ts2.setTexture(
        TextureManager.loadTexture(
        TestImposterNode.class.getClassLoader().getResource("jmetest/" +
        TEXTURE_NAME),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR));
    fakeScene.setRenderState(ts2);

    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.CF_LEQUAL);
    fakeScene.setRenderState(buf);
    fakeScene.updateRenderState();

    // setup the imposter node...
    // we first determine a good texture size (must be equal to or less than the display size)
    int tSize = 256;
    if (display.getHeight() > 512)
      tSize = 512;
    iNode = new ImposterNode("model imposter", 10, tSize, tSize);
    iNode.attachChild(fakeScene);
    iNode.setCameraDistance(100);
    iNode.setRedrawRate(.05f); // .05 = update texture 20 times a second on average
//    iNode.setCameraThreshold(15*FastMath.DEG_TO_RAD);

    // Now add the imposter to a Screen Aligned billboard so the deception is complete.
    BillboardNode bnode = new BillboardNode("imposter bbnode");
    bnode.setType(BillboardNode.SCREEN_ALIGNED);
    bnode.attachChild(iNode);
    rootNode.attachChild(bnode);
  }
}
