/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.effects.LensFlare;
import com.jme.effects.LensFlareFactory;
import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestLensFlare</code>
 *  Test of the lens flare effect in jME.  Notice that currently it doesn't do
 *  occlusion culling.
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java,v 1.6 2004-11-15 22:04:04 renanse Exp $
 */
public class TestLensFlare extends SimpleGame {

  private LightNode lightNode;

  public static void main(String[] args) {
    TestLensFlare app = new TestLensFlare();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleInitGame() {

    display.setTitle("Lens Flare!");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 200.0f));
    cam.update();
    input.setKeySpeed(100);
    lightState.detachAll();

    PointLight dr = new PointLight();
    dr.setEnabled(true);
    dr.setDiffuse(ColorRGBA.white);
    dr.setAmbient(ColorRGBA.gray);
    dr.setLocation(new Vector3f(0f, 0f, 0f));
    lightState.setTwoSidedLighting(true);

    lightNode = new LightNode("light", lightState);
    lightNode.setLight(dr);

    Vector3f min2 = new Vector3f( -0.5f, -0.5f, -0.5f);
    Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
    Box lightBox = new Box("box", min2, max2);
    lightBox.setModelBound(new BoundingBox());
    lightBox.updateModelBound();
    lightNode.attachChild(lightBox);
    lightNode.setTarget(rootNode);
    lightNode.setLocalTranslation(new Vector3f( -14f, 14f, -14f));

    // clear the lights from this lightbox so the lightbox itself doesn't
		// get affected by light:
    lightBox.setLightCombineMode(LightState.OFF);

		// Setup the lensflare textures.
		TextureState[] tex = new TextureState[4];
		tex[0] = display.getRenderer().createTextureState();
		tex[0].setTexture(
				TextureManager.loadTexture(
				LensFlare.class.getClassLoader().getResource(
				"jmetest/data/texture/flare1.png"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR,
				true));
		tex[0].setEnabled(true);
		tex[0].apply();

		tex[1] = display.getRenderer().createTextureState();
		tex[1].setTexture(
				TextureManager.loadTexture(
				LensFlare.class.getClassLoader().getResource(
				"jmetest/data/texture/flare2.png"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR,
				true));
		tex[1].setEnabled(true);
		tex[1].apply();

		tex[2] = display.getRenderer().createTextureState();
		tex[2].setTexture(
				TextureManager.loadTexture(
				LensFlare.class.getClassLoader().getResource(
				"jmetest/data/texture/flare3.png"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR,
				true));
		tex[2].setEnabled(true);
		tex[2].apply();

		tex[3] = display.getRenderer().createTextureState();
		tex[3].setTexture(
				TextureManager.loadTexture(
				LensFlare.class.getClassLoader().getResource(
				"jmetest/data/texture/flare4.png"),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR,
				true));
		tex[3].setEnabled(true);
		tex[3].apply();

    LensFlare flare = LensFlareFactory.createBasicLensFlare("flare", tex);
    flare.setLocalTranslation(lightNode.getLocalTranslation());

    //lightNode.attachChild(flare);
    Box box = new Box("my box", new Vector3f(0, 0, 0), 10, 10, 10);
    box.setModelBound(new BoundingBox());
    box.updateModelBound();
    rootNode.attachChild(box);
    rootNode.attachChild(lightNode);

    // notice that it comes at the end
    rootNode.attachChild(flare);

  }

}
