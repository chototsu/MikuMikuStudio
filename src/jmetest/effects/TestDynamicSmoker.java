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
package jmetest.effects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import jmetest.renderer.loader.TestMilkJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Disk;
import com.jme.renderer.Renderer;
import com.jmex.effects.ParticleManager;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MilkToJme;

/**
 * <code>TestDynamicSmoker</code>
 * @author Joshua Slack
 */
public class TestDynamicSmoker extends SimpleGame {
  private Node smokeNode;
  private Vector3f offset = new Vector3f(0,3.75f,14.0f);
  private ParticleManager manager;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestDynamicSmoker app = new TestDynamicSmoker();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
    cam.update();

    smokeNode = new Node("Smoker Node");
    smokeNode.setLocalTranslation(new Vector3f(0, 50, -50));

    // Setup the input controller and timer
    input = new NodeHandler(this, smokeNode, "LWJGL");
    input.setKeySpeed(10f);
    input.setMouseSpeed(1f);

    display.setTitle("Dynamic Smoke box");

    // hijack the camera model for our own purposes
    Node camBox;
    MilkToJme converter=new MilkToJme();
    URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
    "jmetest/data/model/msascii/camera.ms3d");
    ByteArrayOutputStream BO=new ByteArrayOutputStream();

    try {
        converter.convert(MSFile.openStream(),BO);
    } catch (IOException e) {
        System.out.println("IO problem writting the file!!!");
        System.out.println(e.getMessage());
        System.exit(0);
    }
    JmeBinaryReader jbr=new JmeBinaryReader();
    URL TEXdir=TestMilkJmeWrite.class.getClassLoader().getResource(
            "jmetest/data/model/msascii/");
    jbr.setProperty("texurl",TEXdir);
    camBox=null;
    try {
    	camBox=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
    } catch (IOException e) {
        System.out.println("darn exceptions:" + e.getMessage());
    }
    
    camBox.setLocalScale(5f);
    camBox.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    smokeNode.attachChild(camBox);
    Disk emitDisc = new Disk("disc", 6, 6, 1.5f);
    emitDisc.setLocalTranslation(offset);
    emitDisc.setForceCull(true);
    smokeNode.attachChild(emitDisc);
    rootNode.attachChild(smokeNode);

    AlphaState as1 = display.getRenderer().createAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setTexture(
        TextureManager.loadTexture(
        TestDynamicSmoker.class.getClassLoader().getResource(
        "jmetest/data/texture/flaresmall.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR));
    ts.setEnabled(true);

    manager = new ParticleManager(300, display.getRenderer().getCamera());
    manager.setGravityForce(new Vector3f(0.0f, 0.0f, 0.0f));
    manager.setEmissionDirection(new Vector3f(0f, 0f, 1f));
    manager.setEmissionMaximumAngle(0.0f);
    manager.setSpeed(1.0f);
    manager.setParticlesMinimumLifeTime(750.0f);
    manager.setStartSize(1.6f);
    manager.setEndSize(15.0f);
    manager.setStartColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    manager.setEndColor(new ColorRGBA(0.6f, 0.2f, 0.0f, 0.0f));
    manager.setRandomMod(.5f);
    manager.setInitialVelocity(0.5f);
    manager.setGeometry(emitDisc);

    manager.warmUp(60);
    TriMesh smoke = manager.getParticles();
    smoke.addController(manager);

    ZBufferState zbuf = display.getRenderer().createZBufferState();
    zbuf.setWritable(false);
    zbuf.setEnabled(true);
    zbuf.setFunction(ZBufferState.CF_LEQUAL);

    smoke.setRenderState(ts);
    smoke.setRenderState(as1);
    smoke.setRenderState(zbuf);
    rootNode.attachChild(smoke);
  }

}
