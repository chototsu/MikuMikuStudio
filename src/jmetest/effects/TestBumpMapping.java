/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.BumpMapColorController;
import com.jme.util.TextureManager;

/**
 * <code>TestLightState</code>
 * 
 * @author Mark Powell
 * @version $Id: TestBumpMapping.java,v 1.9 2007/08/21 20:10:03 nca Exp $
 */
public class TestBumpMapping extends SimpleGame {
    
	private float angle0;

	private Torus t;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestBumpMapping app = new TestBumpMapping();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	/**
	 * Not used in this test.
	 * 
	 * @see com.jme.app.BaseGame#update(float)
	 */
	protected void simpleUpdate() {
        angle0 += 2 * tpf;
        
        ((DirectionalLight)lightState.get(0)).setDirection(new Vector3f(2.0f * 
        		FastMath.cos(angle0), 2.0f * FastMath.sin(angle0), -1.5f));
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {

		t = new Torus("Torus", 30, 30, 5, 10);
		t.setModelBound(new BoundingBox());
		t.updateModelBound();

		BumpMapColorController c = new BumpMapColorController(t);
		t.addController(c);

		MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
				.createMaterialState();
		ms.setColorMaterial(MaterialState.CM_DIFFUSE);
		t.setRenderState(ms);
		t.updateRenderState();

		rootNode.attachChild(t);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		Texture tex = TextureManager.loadTexture(TestBumpMapping.class
				.getClassLoader().getResource(
						"jmetest/data/images/FieldstoneNormal.jpg"),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);

		tex.setWrap(Texture.WM_WRAP_S_WRAP_T);
		tex.setApply(Texture.AM_COMBINE);
		tex.setCombineFuncRGB(Texture.ACF_DOT3_RGB);
		tex.setCombineSrc0RGB(Texture.ACS_TEXTURE);
		tex.setCombineSrc1RGB(Texture.ACS_PRIMARY_COLOR);

		ts.setTexture(tex, 0);

		Texture tex2 = TextureManager.loadTexture(
				TestBumpMapping.class.getClassLoader().getResource(
						"jmetest/data/texture/Decal.PNG"),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 0.0f, true);
		tex2.setApply(Texture.AM_COMBINE);
		tex2.setWrap(Texture.WM_WRAP_S_WRAP_T);
		tex2.setCombineFuncRGB(Texture.ACF_MODULATE);
		tex2.setCombineSrc0RGB(Texture.ACS_PREVIOUS);
		tex2.setCombineSrc1RGB(Texture.ACS_TEXTURE);
		ts.setTexture(tex2, 1);

		t.copyTextureCoords(0, 0, 1);
		t.getBatch(0).scaleTextureCoordinates(0, 8);

		t.setRenderState(ts);

		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);

		t.setRenderState(buf);
		
		
		DirectionalLight dr = new DirectionalLight();
		dr.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1));
		dr.setDiffuse(new ColorRGBA(1, 1, 1, 1));
		dr.setEnabled(true);
		dr.setDirection(new Vector3f(1,1,-1));
		
		lightState.detachAll();
		lightState.attach(dr);
		
		rootNode.updateRenderState();
		rootNode.updateGeometricState(0.0f, true);
		
	}
}