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
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.terrain.TerrainPage;
import com.jme.terrain.util.FaultFractalHeightMap;
import com.jme.terrain.util.ProceduralTextureGenerator;
import com.jme.terrain.util.TerrainFollower;
import com.jme.util.TextureManager;
import com.jme.renderer.Renderer;

/**
 *
 * This class test TerrainFollower utility.
 *
 * @author Mark Powell
 * @author Jack Lindamood (added terrain follower)
 * @version $Id: TestTerrainFollower.java,v 1.2 2004-08-18 20:51:23 cep21 Exp $
 */
public class TestTerrainFollower extends SimpleGame {

 TerrainFollower tf;

  public static void main(String[] args) {
    TestTerrainFollower app = new TestTerrainFollower();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleInitGame() {
      rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
      fpsNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

    DirectionalLight dl = new DirectionalLight();
    dl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dl.setDirection(new Vector3f(1, -0.5f, 1));
    dl.setEnabled(true);
    lightState.attach(dl);

    cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
    cam.update();

    input.setKeySpeed(150f);
    input.setMouseSpeed(1f);
    display.setTitle("Terrain Test");
    display.getRenderer().setBackgroundColor(new ColorRGBA(0.5f,0.5f,0.5f,1));

    DirectionalLight dr = new DirectionalLight();
    dr.setEnabled(true);
    dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    dr.setDirection(new Vector3f(0.5f, -0.5f, 0));

    CullState cs = display.getRenderer().createCullState();
    cs.setCullMode(CullState.CS_BACK);
    cs.setEnabled(true);
    rootNode.setRenderState(cs);

    lightState.setTwoSidedLighting(true);
    lightState.attach(dr);

//    MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.9f);
    FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0, 255,
        0.75f);
    Vector3f terrainScale = new Vector3f(10,1,10);
    heightMap.setHeightScale( 0.001f);
    TerrainPage tb = new TerrainPage("Terrain", 33, heightMap.getSize(), terrainScale,
                                     heightMap.getHeightMap(), false);

    tb.setDetailTexture(1, 16);
    rootNode.attachChild(tb);
      tb.updateGeometricState(0,true);  // IMPORTANT CHANGE: You must first update any
      // TerrainFollower's Spatials before handing them to TerrainFollower's constructor.
      tf=new TerrainFollower(tb,heightMap.getSize()*5,heightMap.getSize()*5);

    ProceduralTextureGenerator pt = new ProceduralTextureGenerator(heightMap);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/grassb.png")), -128, 0, 128);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/dirt.jpg")), 0, 128, 255);
    pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader().getResource(
        "jmetest/data/texture/highest.jpg")), 128, 255, 384);

    pt.createTexture(512);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    Texture t1 = TextureManager.loadTexture(
        pt.getImageIcon().getImage(),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true,
        true);
    ts.setTexture(t1, 0);

    Texture t2 = TextureManager.loadTexture(TestTerrain.class.getClassLoader().
                                            getResource(
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

    FogState fs = display.getRenderer().createFogState();
    fs.setDensity(0.5f);
    fs.setEnabled(true);
    fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
    fs.setEnd(1000);
    fs.setStart(500);
    fs.setDensityFunction(FogState.DF_LINEAR);
    fs.setApplyFunction(FogState.AF_PER_VERTEX);
    rootNode.setRenderState(fs);
      display.getRenderer().getCamera().setLocation(new Vector3f(0,tf.terrainPosition(0,0)+5,0));
  }

    /**
     * This is where I update my camera's location.
     */
    protected void simpleUpdate(){
        Vector3f camLoc=display.getRenderer().getCamera().getLocation();
        float f=tf.terrainPosition(camLoc.x,camLoc.z);
        if (!Float.isNaN(f)){
            camLoc.y = f+9;
        }
    }
}