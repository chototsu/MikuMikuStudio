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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Skybox;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.util.TextureManager;
import com.jme.renderer.Renderer;

/**
 * <code>TestSkybox</code>
 * @author Joshua Slack
 * @version $Id: TestSkybox.java,v 1.5 2005-02-10 21:48:21 renanse Exp $
 */
public class TestSkybox extends SimpleGame {

  Skybox m_skybox;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestSkybox app = new TestSkybox();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
    m_skybox.setLocalTranslation(cam.getLocation());
  }

  /**
   * builds the game.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("SkyBox");
    cam.setLocation(new Vector3f(0, 0, 75));
    cam.update();

    // Pop a few objects into our scene.
    Torus t = new Torus("Torus", 50, 50, 5, 10);
    t.setModelBound(new BoundingBox());
    t.updateModelBound();
    t.setLocalTranslation(new Vector3f(-40, 0, 10));
    t.setVBOVertexEnabled(true);
    t.setVBOTextureEnabled(true);
    t.setVBONormalEnabled(true);
    t.setVBOColorEnabled(true);
    rootNode.attachChild(t);
    t.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    Sphere s = new Sphere("Sphere", 63, 50, 25);
    s.setModelBound(new BoundingBox());
    s.updateModelBound();
    s.setLocalTranslation(new Vector3f(40, 0, -10));
    rootNode.attachChild(s);
    s.setVBOVertexEnabled(true);
    s.setVBOTextureEnabled(true);
    s.setVBONormalEnabled(true);
    s.setVBOColorEnabled(true);
    s.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    Box b = new Box("box", new Vector3f(-25, 70, -45), 20, 20, 20);
    b.setModelBound(new BoundingBox());
    b.updateModelBound();
    b.setVBOVertexEnabled(true);
    b.setVBOTextureEnabled(true);
    b.setVBONormalEnabled(true);
    b.setVBOColorEnabled(true);
    rootNode.attachChild(b);
    b.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    // Create a skybox
    m_skybox = new Skybox("skybox", 10, 10, 10);

    Texture north = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/north.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);
    Texture south = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/south.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);
    Texture east = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/east.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);
    Texture west = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/west.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);
    Texture up = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/top.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);
    Texture down = TextureManager.loadTexture(
        TestSkybox.class.getClassLoader().getResource(
        "jmetest/data/texture/bottom.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR);

    m_skybox.setTexture(Skybox.NORTH, north);
    m_skybox.setTexture(Skybox.WEST, west);
    m_skybox.setTexture(Skybox.SOUTH, south);
    m_skybox.setTexture(Skybox.EAST, east);
    m_skybox.setTexture(Skybox.UP, up);
    m_skybox.setTexture(Skybox.DOWN, down);
    m_skybox.preloadTextures();
    rootNode.attachChild(m_skybox);

  }
}
