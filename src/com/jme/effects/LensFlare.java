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
package com.jme.effects;

import java.util.Iterator;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.scene.Geometry;

/**
 * <code>LensFlare</code>
 *  First crack at a lens flare for jme.  Notice that currently,
 *  it doesn't do occlusion culling.
 *
 *   The easiest way to use this class is to attach it as a child to a lightnode.
 *   Optionally you can make it a child or a sibling of an object you wish to
 *   have a 'glint' on.  In the case of sibling, use
 *   setLocalTranslation(sibling.getLocalTranslation()) or something similar to
 *   ensure position.
 * @author Joshua Slack
 * @version $Id: LensFlare.java,v 1.7 2004-06-23 02:05:18 renanse Exp $
 */

public class LensFlare extends Node {
  private Quad mainFlare;
  private Quad sFlare[];
  private Vector2f midPoint;
  private Vector3f flarePoint;

  public LensFlare(String name) {
    super(name);
    initChildren();
    Iterator it = children.iterator();
    while (it.hasNext()) {
      Geometry spat = (Geometry)it.next();
      spat.setRenderQueueMode(Renderer.QUEUE_ORTHO);
      spat.setVBOVertexEnabled(true);
      spat.setVBONormalEnabled(true);
      spat.setVBOTextureEnabled(true);
      spat.setVBOColorEnabled(true);
    }
    this.setRenderQueueMode(Renderer.QUEUE_ORTHO);
  }

  /**
   * <code>updateWorldData</code> updates all the children maintained by
   * this node.
   * @param time the frame time.
   */
  public void updateWorldData(float time) {
    // Update flare:
    super.updateWorldData(time);
    // Locate light src on screen x,y
    flarePoint = DisplaySystem.getDisplaySystem().getScreenCoordinates(
        worldTranslation).subtractLocal(midPoint.x, midPoint.y, 0);
    if (Math.abs(flarePoint.x) > midPoint.x ||
        Math.abs(flarePoint.y) > midPoint.y ||
        flarePoint.z >= 1.0f) {
      setForceCull(true);
      return;
    } else setForceCull(false);
    // define a line from light src to one opposite across the center point
    // draw main flare at src point
    Vector3f tempPoint;
    tempPoint = mainFlare.getWorldTranslation();
    tempPoint.x = flarePoint.x;
    tempPoint.y = flarePoint.y;
    tempPoint.z = 0;

    tempPoint = sFlare[0].getWorldTranslation();
    tempPoint.x = flarePoint.x / .8f;
    tempPoint.y = flarePoint.y / .8f;
    tempPoint.z = 0;

    tempPoint = sFlare[1].getWorldTranslation();
    tempPoint.x = flarePoint.x;
    tempPoint.y = flarePoint.y;
    tempPoint.z = 0;

    tempPoint = sFlare[2].getWorldTranslation();
    tempPoint.x = flarePoint.x / .8f;
    tempPoint.y = flarePoint.y / .8f;
    tempPoint.z = 0;

    tempPoint = sFlare[3].getWorldTranslation();
    tempPoint.x = flarePoint.x / 2f;
    tempPoint.y = flarePoint.y / 2f;
    tempPoint.z = 0;

    tempPoint = sFlare[4].getWorldTranslation();
    tempPoint.x = flarePoint.x / 2.2f;
    tempPoint.y = flarePoint.y / 2.2f;
    tempPoint.z = 0;

    tempPoint = sFlare[5].getWorldTranslation();
    tempPoint.x = flarePoint.x / 2.4f;
    tempPoint.y = flarePoint.y / 2.4f;
    tempPoint.z = 0;

    tempPoint = sFlare[6].getWorldTranslation();
    tempPoint.x = flarePoint.x / 3f;
    tempPoint.y = flarePoint.y / 3f;
    tempPoint.z = 0;

    tempPoint = sFlare[7].getWorldTranslation();
    tempPoint.x = 0;
    tempPoint.y = 0;
    tempPoint.z = 0;

    tempPoint = sFlare[8].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 3f;
    tempPoint.y = -flarePoint.y / 3f;
    tempPoint.z = 0;

    tempPoint = sFlare[9].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 2f;
    tempPoint.y = -flarePoint.y / 2f;
    tempPoint.z = 0;

    tempPoint = sFlare[10].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 1.8f;
    tempPoint.y = -flarePoint.y / 1.8f;
    tempPoint.z = 0;

    tempPoint = sFlare[11].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 1.5f;
    tempPoint.y = -flarePoint.y / 1.5f;
    tempPoint.z = 0;

    tempPoint = sFlare[12].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 1.4f;
    tempPoint.y = -flarePoint.y / 1.4f;
    tempPoint.z = 0;

    tempPoint = sFlare[13].getWorldTranslation();
    tempPoint.x = -flarePoint.x / 1.1f;
    tempPoint.y = -flarePoint.y / 1.1f;
    tempPoint.z = 0;

    tempPoint = sFlare[14].getWorldTranslation();
    tempPoint.x = -flarePoint.x;
    tempPoint.y = -flarePoint.y;
    tempPoint.z = 0;
  }

  /**
   * initChildren
   */
  private void initChildren() {
    DisplaySystem display = DisplaySystem.getDisplaySystem();
    midPoint = new Vector2f(display.getWidth() >> 1, display.getHeight() >> 1);

    AlphaState as1 = display.getRenderer().getAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setTexture(
        TextureManager.loadTexture(
        LensFlare.class.getClassLoader().getResource(
        "jmetest/data/texture/flare1.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts.setEnabled(true);
    ts.apply();

    TextureState ts2 = display.getRenderer().getTextureState();
    ts2.setTexture(
        TextureManager.loadTexture(
        LensFlare.class.getClassLoader().getResource(
        "jmetest/data/texture/flare2.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts2.setEnabled(true);
    ts2.apply();

    TextureState ts3 = display.getRenderer().getTextureState();
    ts3.setTexture(
        TextureManager.loadTexture(
        LensFlare.class.getClassLoader().getResource(
        "jmetest/data/texture/flare3.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts3.setEnabled(true);
    ts3.apply();

    TextureState ts4 = display.getRenderer().getTextureState();
    ts4.setTexture(
        TextureManager.loadTexture(
        LensFlare.class.getClassLoader().getResource(
        "jmetest/data/texture/flare4.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts4.setEnabled(true);
    ts4.apply();

    LightState blankLightState = display.getRenderer().getLightState();
    blankLightState.setEnabled(false);

    for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
      setRenderState(defaultStateList[i]);
    }
    setRenderState(as1);
    setRenderState(ts3);
    setRenderState(blankLightState);

    mainFlare = new Quad("quad", midPoint.x * .75f, midPoint.x * .75f);
    mainFlare.setRenderState(ts);
    mainFlare.setSolidColor(new ColorRGBA(.95f, .95f, .95f, 1f));
    mainFlare.setLightCombineMode(LightState.REPLACE);
    mainFlare.setTextureCombineMode(TextureState.REPLACE);
    this.attachChild(mainFlare);

    sFlare = new Quad[15];
    sFlare[0] = new Quad("sf0", midPoint.x * 1.25f, midPoint.x * 1.25f);
    sFlare[0].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));
    sFlare[0].setRenderState(ts4);

    sFlare[1] = new Quad("sf1", midPoint.x * .75f, midPoint.x * .75f);
    sFlare[1].setSolidColor(new ColorRGBA(.8f, .8f, .8f, 1f));
    sFlare[1].setRenderState(ts2);

    sFlare[2] = new Quad("sf2", midPoint.x * .15f, midPoint.x * .15f);
    sFlare[2].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));

    sFlare[3] = new Quad("sf3", midPoint.x * .08f, midPoint.x * .08f);
    sFlare[3].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
                                          1f));

    sFlare[4] = new Quad("sf4", midPoint.x * .40f, midPoint.x * .40f);
    sFlare[4].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
                                          1f));

    sFlare[5] = new Quad("sf5", midPoint.x * .1f, midPoint.x * .1f);
    sFlare[5].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
                                          1f));

    sFlare[6] = new Quad("sf6", midPoint.x * .25f, midPoint.x * .25f);
    sFlare[6].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 50f / 255f,
                                          1f));

    sFlare[7] = new Quad("sf7", midPoint.x * .01f, midPoint.x * .01f);
    sFlare[7].setSolidColor(new ColorRGBA(.8f, .8f, .8f, 1f));

    sFlare[8] = new Quad("sf8", midPoint.x * .02f, midPoint.x * .02f);
    sFlare[8].setSolidColor(new ColorRGBA(.8f, .8f, .8f, 1f));

    sFlare[9] = new Quad("sf9", midPoint.x * .1f, midPoint.x * .1f);
    sFlare[9].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f, 1f));

    sFlare[10] = new Quad("sf10", midPoint.x * .06f, midPoint.x * .06f);
    sFlare[10].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
                                           1f));

    sFlare[11] = new Quad("sf11", midPoint.x * .375f, midPoint.x * .375f);
    sFlare[11].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
                                           1f));

    sFlare[12] = new Quad("sf12", midPoint.x * .1f, midPoint.x * .1f);
    sFlare[12].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
                                           1f));

    sFlare[13] = new Quad("sf13", midPoint.x * .25f, midPoint.x * .25f);
    sFlare[13].setSolidColor(new ColorRGBA(30f / 255f, 30f / 255f, 0f / 255f,
                                           1f));

    sFlare[14] = new Quad("sf14", midPoint.x * .75f, midPoint.x * .75f);
    sFlare[14].setSolidColor(new ColorRGBA(.8f, .8f, .8f, 1f));
    sFlare[14].setRenderState(ts2);

    for (int i = 0; i < sFlare.length; i++) {
      this.attachChild(sFlare[i]);
      sFlare[i].setLightCombineMode(LightState.REPLACE);
      sFlare[i].setTextureCombineMode(TextureState.REPLACE);
    }
  }

}
