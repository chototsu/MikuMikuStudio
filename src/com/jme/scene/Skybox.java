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

package com.jme.scene;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.scene.state.RenderState;
import com.jme.renderer.Renderer;

/**
 * A Box made of textured quads that simulate having a sky, horizon and so forth
 * around your scene.  Either attach to a camera node or update on each frame to
 * set this skybox at the camera's position.
 * @author David Bitkowski
 * @version $Id: Skybox.java,v 1.1 2004-07-03 21:45:23 renanse Exp $
 */
public class Skybox extends Node {
  public final static int NORTH = 0;
  public final static int SOUTH = 1;
  public final static int EAST = 2;
  public final static int WEST = 3;
  public final static int UP = 4;
  public final static int DOWN = 5;

  private float xExtent;
  private float yExtent;
  private float zExtent;
  private Quad[] skyboxQuads;

  public Skybox(String name, float xExtent, float yExtent, float zExtent) {
    super(name);

    this.xExtent = xExtent;
    this.yExtent = yExtent;
    this.zExtent = zExtent;

    initialize();
  }

  /**
   * Set the texture to be displayed on the given side of the skybox.
   * Replaces any existing texture on that side.
   * @param direction int
   * @param texture Texture
   */
  public void setTexture(int direction, Texture texture) {
    if (direction < 0 || direction > 5) {
      throw new JmeException("Direction " + direction +
                             " is not a valid side for the skybox");
    }

    skyboxQuads[direction].clearRenderState(RenderState.RS_TEXTURE);
    setTexture(direction, texture, 0);
  }

  /**
   * Set the texture to be displayed on the given side of the skybox.  Only
   * replaces the texture at the index specified by textureUnit.
   * @param direction int
   * @param texture Texture
   * @param textureUnit int
   */
  public void setTexture(int direction, Texture texture, int textureUnit) {
    // Validate
    if (direction < 0 || direction > 5) {
      throw new JmeException("Direction " + direction +
                             " is not a valid side for the skybox");
    }

    TextureState ts = (TextureState)skyboxQuads[direction].getRenderStateList()[RenderState.RS_TEXTURE];
    if (ts == null) {
      ts = DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
    }

    // Initialize the texture state
    ts.setTexture(texture, textureUnit);
    ts.setEnabled(true);

    // Set the texture to the quad
    skyboxQuads[direction].setRenderState(ts);

    return;
  }

  private void initialize() {
    DisplaySystem display = DisplaySystem.getDisplaySystem();

    // Skybox consists of 6 sides
    skyboxQuads = new Quad[6];

    // Create each of the quads
    skyboxQuads[NORTH] = new Quad("north", xExtent * 2, yExtent * 2);
    skyboxQuads[NORTH].setLocalRotation(new Quaternion(new float[] {0,
        (float) Math.toRadians(180), 0}));
    skyboxQuads[NORTH].setLocalTranslation(new Vector3f(0, 0, zExtent));
    skyboxQuads[SOUTH] = new Quad("south", xExtent * 2, yExtent * 2);
    skyboxQuads[SOUTH].setLocalTranslation(new Vector3f(0, 0, -zExtent));
    skyboxQuads[EAST] = new Quad("east", zExtent * 2, yExtent * 2);
    skyboxQuads[EAST].setLocalRotation(new Quaternion(new float[] {0,
        (float) Math.toRadians(90), 0}));
    skyboxQuads[EAST].setLocalTranslation(new Vector3f(-xExtent, 0, 0));
    skyboxQuads[WEST] = new Quad("west", zExtent * 2, yExtent * 2);
    skyboxQuads[WEST].setLocalRotation(new Quaternion(new float[] {0,
        (float) Math.toRadians(270), 0}));
    skyboxQuads[WEST].setLocalTranslation(new Vector3f( xExtent, 0, 0));
    skyboxQuads[UP] = new Quad("up", xExtent * 2, zExtent * 2);
    skyboxQuads[UP].setLocalRotation(new Quaternion(new float[] { (float) Math.
        toRadians(90), (float) Math.toRadians(270), 0}));
    skyboxQuads[UP].setLocalTranslation(new Vector3f(0, yExtent, 0));
    skyboxQuads[DOWN] = new Quad("down", xExtent * 2, zExtent * 2);
    skyboxQuads[DOWN].setLocalRotation(new Quaternion(new float[] { (float)
        Math.toRadians(270), (float) Math.toRadians(270), 0}));
    skyboxQuads[DOWN].setLocalTranslation(new Vector3f(0, -yExtent, 0));

    // We don't want the light to effect our skybox
    LightState lightState = display.getRenderer().getLightState();
    lightState.setEnabled(false);
    setRenderState(lightState);
    setLightCombineMode(LightState.REPLACE);
    setTextureCombineMode(TextureState.REPLACE);

    // We don't want it making our skybox disapear, so force view
    setForceView(true);

    for (int i = 0; i < 6; i++) {
      // Make sure texture is only what is set.
      skyboxQuads[i].setTextureCombineMode(TextureState.REPLACE);

      // Make sure no lighting on the skybox
      skyboxQuads[i].setLightCombineMode(LightState.REPLACE);

      // Make sure the quad is viewable
      skyboxQuads[i].setForceView(true);

      // Set a bounding volume
      skyboxQuads[i].setModelBound(new BoundingBox());
      skyboxQuads[i].updateModelBound();


      skyboxQuads[i].setRenderQueueMode(Renderer.QUEUE_OPAQUE);
      skyboxQuads[i].setVBOVertexEnabled(true);
      skyboxQuads[i].setVBONormalEnabled(true);
      skyboxQuads[i].setVBOTextureEnabled(true);
      skyboxQuads[i].setVBOColorEnabled(true);

      // And attach the skybox as a child
      attachChild(skyboxQuads[i]);
    }
  }

  /**
   * Retrieve the quad indicated by side.
   * @param side int
   * @return Quad
   */
  public Quad getSide(int side) {
    return skyboxQuads[side];
  }
}
