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
package jmetest.effects.cloth;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.SpringNodeForce;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.cloth.ClothUtils;
import com.jmex.effects.cloth.CollidingClothPatch;

/**
 * <code>TestCloth</code> shows a simple demo of jME's
 * Cloth abilities, including interaction with forces and
 * triangle based collision.
 *
 * @author Joshua Slack
 * @version $Id: TestCloth.java,v 1.3 2005-05-12 22:49:43 Mojomonkey Exp $
 */
public class TestCloth extends SimpleGame {

	private CollidingClothPatch cloth;
	private float windStrength = 40f;
	private Vector3f windDirection = new Vector3f(.3f, 0f, .8f);
	private SpringNodeForce wind, gravity, drag;
	private Sphere sphere;


	/**
	 * Entry point for the test,
	 * @param args
	 */
	public static void main(String[] args) {
		TestCloth app = new TestCloth();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	/**
	 *
	 */
	public void simpleUpdate() {
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("wind", false)) {
			wind.setEnabled(!wind.isEnabled());
			System.err.println("wind is: "+(wind.isEnabled() ? "on" : "off"));
		}
	}

	/**
	 * Sets up the scene.  First it moves the default point light and camera position.
	 * Then a cloth of 2500 nodes is created and wind, gravity, and drag forces are
	 * added.  Finally a sphere is added to the scene and as a collider object to
	 * the cloth.  Texturing is applied and finally the top edge of the cloth
	 * is fixed in place by setting those nodes to have infinite mass.
	 */
	protected void simpleInitGame() {
		display.setTitle("jME Cloth Demo");
		KeyBindingManager.getKeyBindingManager().set("wind", KeyInput.KEY_RETURN);

		((PointLight)lightState.get(0)).setLocation(new Vector3f(0, -30, 150));
		lightState.setTwoSidedLighting(true);
		cam.setLocation(new Vector3f(0, -30, 100));
		cam.update();
		input.setKeySpeed(30);

		cloth = new CollidingClothPatch("cloth", 50, 50, 1f, 10); // name, nodesX, nodesY, springSize, nodeMass
		// Add a simple breeze with mild random eddies:
		wind = ClothUtils.createBasicWind(windStrength, windDirection, true);
		cloth.addForce(wind);
		// Add a simple gravitational force:
		gravity = ClothUtils.createBasicGravity();
		cloth.addForce(gravity);
		// Add a simple drag force.
		drag = ClothUtils.createBasicDrag(20f);
		cloth.addForce(drag);

		sphere = new Sphere("sphere", 20, 20, 6);
		sphere.updateCollisionTree();
		sphere.setModelBound(new OrientedBoundingBox());
		sphere.updateModelBound();
		rootNode.attachChild(sphere);

		SpatialTransformer st = new SpatialTransformer(1);
		st.setObject(sphere, 0, -1);
		st.setPosition(0, 0, new Vector3f(10, 10, 30));
		st.setPosition(0, 2, new Vector3f(-10, -10, -30));
		st.setPosition(0, 4, new Vector3f(10, 10, 30));
		st.interpolateMissing();
		sphere.addController(st);

		cloth.addCollider(sphere);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setTexture(
			TextureManager.loadTexture(
			TestCloth.class.getClassLoader().getResource(
			"jmetest/data/images/Monkey.jpg"),
			Texture.MM_LINEAR_LINEAR,
			Texture.FM_LINEAR));
		cloth.setRenderState(ts);
		rootNode.attachChild(cloth);
		for (int i = 0; i < 50; i++) {
			cloth.getSystem().getNode(i).position.x *= .8f;
			cloth.getSystem().getNode(i).setMass(Float.POSITIVE_INFINITY);
		}
  }
}
