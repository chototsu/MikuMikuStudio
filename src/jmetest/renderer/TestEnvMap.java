/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.model.ase.ASEModel;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestBackwardAction</code>
 *
 * @author Mark Powell
 * @version $Id: TestEnvMap.java,v 1.3 2004-07-02 23:50:32 renanse Exp $
 */
public class TestEnvMap extends SimpleGame {

  private ASEModel model;

  public static void main(String[] args) {
    TestEnvMap app = new TestEnvMap();
    app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleInitGame() {
    display.setTitle("Environmental Maps");

    cam.setLocation(new Vector3f(0, 0, 100));
    cam.update();


    Torus torus = new Torus("Torus", 50, 50, 10, 20);

    Quad background = new Quad("Background");
    background.initialize(150,120);
    background.setLocalTranslation(new Vector3f(0,0,-30));

    Texture bg = TextureManager.loadTexture(
            TestEnvMap.class.getClassLoader().getResource(
            "jmetest/data/texture/clouds.png"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true);
    TextureState bgts = display.getRenderer().getTextureState();
    bgts.setTexture(bg);
    bgts.setEnabled(true);
    background.setRenderState(bgts);

    TextureState ts = display.getRenderer().getTextureState();
    //Base texture, not environmental map.
    Texture t0 = TextureManager.loadTexture(
            TestEnvMap.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    //Environmental Map (reflection of clouds)
    Texture t = TextureManager.loadTexture(
            TestEnvMap.class.getClassLoader().getResource(
            "jmetest/data/texture/clouds.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    t.setEnvironmentalMapMode(Texture.EM_SPHERE);
    ts.setTexture(t0,0);
    ts.setTexture(t,1);
    ts.setEnabled(true);

    PointLight pl = new PointLight();
    pl.setAmbient(new ColorRGBA(0.75f,0.75f,0.75f,1));
    pl.setDiffuse(new ColorRGBA(1,0,0,1));
    pl.setLocation(new Vector3f(50,0,0));
    pl.setEnabled(true);

    lightState.attach(pl);

    torus.setRenderState(ts);
    rootNode.attachChild(torus);
    rootNode.attachChild(background);

    input = new NodeHandler(this, torus, properties.getRenderer());
    input.setKeySpeed(10);
    input.setMouseSpeed(2);
  }
}
