/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * <code>ImposterNode</code>
 * @author Joshua Slack
 * @version $Id: ImposterNode.java,v 1.1 2004-03-31 03:08:49 renanse Exp $
 */
public class ImposterNode extends Node {
  private TextureRenderer tRenderer;
  private Texture texture;
  private Node quadScene;
  private static int inode_val = 0;
  private Quad standIn;
  private DisplaySystem display;
  private float redrawRate;
  private float elapsed;
  private float cameraDistance = 75f;

  public ImposterNode(String name, DisplaySystem display, float width, float height) {
    super(name);
    this.display = display;
    tRenderer = display.createTextureRenderer(false, true, false, false,
                                              TextureRenderer.RENDER_TEXTURE_2D,
                                              4);

    tRenderer.getCamera().setLocation(new Vector3f(0,0,75f));
    tRenderer.getCamera().update();
    tRenderer.setBackgroundColor(new ColorRGBA(1,0,0,0f));

    quadScene = new Node("imposter_scene_" + inode_val);

    standIn = new Quad("imposter_quad_" + inode_val);
    standIn.initialize(width, height);
    standIn.setModelBound(new BoundingBox());
    standIn.updateModelBound();
    standIn.setParent(this);

    inode_val++;
    resetTexture();
    redrawRate = elapsed = 0.01f;
  }

  /**
   * <code>draw</code> calls the onDraw method for each child maintained
   * by this node.
   * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
   * @param r the renderer to draw to.
   */
  public void draw(Renderer r) {
    if (shouldDoUpdate()) {
      updateCamera();
      updateTexture();
      renderTexture();
    }
    standIn.onDraw(r);
  }

  /**
   * updateCamera
   */
  private void updateCamera() {
    Vector3f myLoc = display.getRenderer().getCamera().getLocation();
    float vDist = myLoc.distance(standIn.getCenter());
    float ratio = cameraDistance / vDist;
    Vector3f newPos = (myLoc.subtract(standIn.getCenter())).multLocal(ratio).addLocal(standIn.getCenter());
    tRenderer.getCamera().setLocation(newPos);
    tRenderer.getCamera().lookAt(standIn.getCenter());
  }

  /**
   * shouldDoUpdate
   *
   * @return boolean
   */
  private boolean shouldDoUpdate() {
    if (redrawRate <= 0) return true;
    if (redrawRate > 0 && elapsed >= redrawRate) {
      elapsed = 0f;
      return true;
    }
    return false;
  }

  /**
   * <code>draw</code> calls the onDraw method for each child maintained
   * by this node.
   * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
   * @param r the renderer to draw to.
   */
  public void drawBounds(Renderer r) {
    standIn.onDrawBounds(r);
  }

  /**
   *
   * <code>attachChild</code> attaches a child to this node. This node
   * becomes the child's parent. The current number of children maintained
   * is returned.
   * @param child the child to attach to this node.
   * @return the number of children maintained by this node.
   */
  public int attachChild(Spatial child) {
    return quadScene.attachChild(child);
  }

  public void setTextureRenderer(TextureRenderer tRenderer) {
    this.tRenderer = tRenderer;
    resetTexture();
  }

  public TextureRenderer getTextureRenderer() {
    return tRenderer;
  }

  public float getCameraDistance() {
    return cameraDistance;
  }

  public void setCameraDistance(float cameraDistance) {
    this.cameraDistance = cameraDistance;
  }

  public float getRedrawRate() {
    return redrawRate;
  }

  public void setRedrawRate(float redrawRate) {
    this.redrawRate = redrawRate;
  }

  public Quad getStandIn() {
    return standIn;
  }

  public void resetTexture() {
    if (texture != null)
      texture = tRenderer.setupTexture(texture.getTextureId());
    else
      texture = tRenderer.setupTexture();
    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    ts.setTexture(texture, 0);
    standIn.setRenderState(ts);

    // Add a blending mode...  This is so the background of the texture is transparent.
    AlphaState as1 = display.getRenderer().getAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);
    standIn.setRenderState(as1);
  }

  private void updateTexture() {
    updateGeometricState(0.0f, true);
  }

  public void renderTexture() {
    tRenderer.render(quadScene, texture);
  }

  /**
   * <code>updateWorldBound</code> merges the bounds of all the children
   * maintained by this node. This will allow for faster culling operations.
   * @see com.jme.scene.Spatial#updateWorldBound()
   */
  public void updateWorldBound() {
    worldBound = (BoundingVolume)standIn.getWorldBound().clone(worldBound);
  }

  /**
   *
   * <code>updateWorldData</code> updates the world transforms from the
   * parent down to the leaf.
   * @param time the frame time.
   */
  public void updateWorldData(float time) {
    super.updateWorldData(time);
    quadScene.updateGeometricState(time, true);
    standIn.updateGeometricState(time, false);
    elapsed+=time;
  }
}
