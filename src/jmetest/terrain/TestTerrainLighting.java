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

package jmetest.terrain;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.effects.LensFlare;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.terrain.TerrainBlock;
import com.jme.terrain.util.MidPointHeightMap;
import com.jme.util.TextureManager;
import com.jme.renderer.Renderer;

/**
 * <code>TestTerrainLighting</code>
 * @author Mark Powell
 * @version $Id: TestTerrainLighting.java,v 1.21 2004-07-08 20:34:59 renanse Exp $
 */
public class TestTerrainLighting extends SimpleGame {
  private CameraNode camNode;
  private Vector3f currentPos;
  private Vector3f newPos;
  private LightNode lightNode;
  private LensFlare flare;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestTerrainLighting app = new TestTerrainLighting();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
    if ( (int) currentPos.x == (int) newPos.x
        && (int) currentPos.z == (int) newPos.z) {
      newPos.x = FastMath.nextRandomFloat() * 128 * 5;
      newPos.z = FastMath.nextRandomFloat() * 128 * 5;
    }

    currentPos.x -= (currentPos.x - newPos.x)
        / (timer.getFrameRate() / 2);
    currentPos.y = 255;
    currentPos.z -= (currentPos.z - newPos.z)
        / (timer.getFrameRate() / 2);

    lightNode.setLocalTranslation(currentPos);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {

    rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    fpsNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    currentPos = new Vector3f();
    newPos = new Vector3f();

    cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
    cam.update();

    camNode = new CameraNode("Camera Node", cam);
    camNode.setLocalTranslation(new Vector3f(0, 250, -20));
    camNode.updateWorldData(0);

    input = new NodeHandler(this, camNode, "LWJGL");
    input.setKeySpeed(50f);
    input.setMouseSpeed(.5f);

    display.setTitle("Terrain Test");

    PointLight dr = new PointLight();
    dr.setEnabled(true);
    dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    dr.setLocation(new Vector3f(0.5f, -0.5f, 0));

    CullState cs = display.getRenderer().getCullState();
    cs.setCullMode(CullState.CS_BACK);
    cs.setEnabled(true);
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

    flare = new LensFlare("flare");
    flare.setLocalScale(.5f);
    lightNode.attachChild(flare);
    MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.5f);
    Vector3f terrainScale = new Vector3f(5,1,5);
    TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(), terrainScale,
                                       heightMap.getHeightMap(),
                                       new Vector3f(0,0,0), false);
    tb.setDetailTexture(1, 4);
    tb.setModelBound(new BoundingBox());
    tb.updateModelBound();
    rootNode.attachChild(tb);
    rootNode.setRenderState(cs);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(false);
    Texture t1 = TextureManager.loadTexture(
        TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/grassb.png"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    ts.setTexture(t1, 0);

    Texture t2 = TextureManager.loadTexture(
        TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/Detail.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true);
    ts.setTexture(t2, 1);
    t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

    t1.setApply(Texture.AM_COMBINE);
    t1.setCombineFuncRGB(Texture.ACF_MODULATE);
    t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
    t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
    t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
    t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
    t1.setCombineScaleRGB(1.0f);

    t2.setApply(Texture.AM_COMBINE);
    t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
    t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
    t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
    t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
    t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
    t2.setCombineScaleRGB(1.0f);
    rootNode.setRenderState(ts);

    rootNode.attachChild(lightNode);
    rootNode.attachChild(camNode);
  }
}
