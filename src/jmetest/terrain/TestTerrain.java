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

package jmetest.terrain;

import javax.swing.ImageIcon;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.terrain.TerrainBlock;
import com.jme.terrain.util.MidPointHeightMap;
import com.jme.terrain.util.ProceduralTextureGenerator;
import com.jme.util.TextureManager;

/**
 * <code>TestTerrain</code>
 *
 * @author Mark Powell
 * @version $Id: TestTerrain.java,v 1.27 2004-06-26 00:13:39 renanse Exp $
 */
public class TestTerrain extends SimpleGame {

  /**
   * Entry point for the test,
   *
   * @param args
   */
  public static void main(String[] args) {
    TestTerrain app = new TestTerrain();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   *
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Terrain Test");
    input.setKeySpeed(50f);
    cam.setLocation(new Vector3f(64*5, 250, 64*5));
    cam.update();

    FogState fs = display.getRenderer().getFogState();
    fs.setEnabled(false);
    rootNode.setRenderState(fs);

    CullState cs = display.getRenderer().getCullState();
    cs.setCullMode(CullState.CS_BACK);
    cs.setEnabled(true);

    lightState.setTwoSidedLighting(true);
    ((PointLight)lightState.get(0)).setLocation(new Vector3f(100,500,50));
    MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.9f);
    Vector3f terrainScale = new Vector3f(5,1,5);
    TerrainBlock tb = new TerrainBlock("Terrain", heightMap.getSize(), terrainScale,
                                       heightMap.getHeightMap(),
                                       new Vector3f(0, 0, 0), false);
    tb.setDetailTexture(1, 16);
    tb.setModelBound(new BoundingBox());
    tb.updateModelBound();
    tb.setLocalTranslation(new Vector3f(0,0,0));
    rootNode.attachChild(tb);
    rootNode.setRenderState(cs);

    ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
        heightMap);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                                .getResource("jmetest/data/texture/grassb.png")),
                  -128, 0, 128);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                                .getResource("jmetest/data/texture/dirt.jpg")),
                  0, 128, 255);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                                .getResource("jmetest/data/texture/highest.jpg")),
                  128, 255,
                  384);

    pt.createTexture(64);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    Texture t1 = TextureManager.loadTexture(
        pt.getImageIcon().getImage(),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR, true, true);
    ts.setTexture(t1, 0);

    Texture t2 = TextureManager.loadTexture(
        TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/Detail.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR, true);

    ts.setTexture(t2, 1);
    t2.setWrap(Texture.WM_WRAP_S_WRAP_T);

    t1.setApply(Texture.AM_COMBINE);
    t1.setCombineFuncRGB(Texture.ACF_MODULATE);
    t1.setCombineSrc0RGB(Texture.ACS_TEXTURE);
    t1.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
    t1.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);
    t1.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
    t1.setCombineScaleRGB(0);

    t2.setApply(Texture.AM_COMBINE);
    t2.setCombineFuncRGB(Texture.ACF_ADD_SIGNED);
    t2.setCombineSrc0RGB(Texture.ACS_TEXTURE);
    t2.setCombineOp0RGB(Texture.ACO_SRC_COLOR);
    t2.setCombineSrc1RGB(Texture.ACS_PREVIOUS);
    t2.setCombineOp1RGB(Texture.ACO_SRC_COLOR);
    t2.setCombineScaleRGB(0);
    rootNode.setRenderState(ts);

  }
}
