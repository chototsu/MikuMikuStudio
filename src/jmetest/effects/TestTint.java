/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.effects.Tint;
import com.jme.image.Texture;
import com.jme.input.AbstractInputController;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.slider.WidgetHSlider;
import com.jme.widget.text.WidgetLabel;

/**
 * <code>TestTint</code>
 * 
 * @author Ahmed
 * @version $Id: TestTint.java,v 1.5 2004-03-07 13:00:10 darkprophet Exp $
 */
public class TestTint extends SimpleGame {

	class SliderPanel extends WidgetAbstractFrame implements Observer {
		WidgetHSlider alphaValue;
		WidgetLabel instructions;

		public SliderPanel(AbstractInputController ic) {
			super(ic);
			setLayout(new WidgetBorderLayout());
			
			instructions = new WidgetLabel("Increase the slider to change alpha. Observe color changes.", WidgetAlignmentType.ALIGN_CENTER);
			instructions.setInsets(new WidgetInsets(0, 0, 5, 0));
			instructions.setFgColor(new ColorRGBA(1, 1, 0, 1));
			instructions.setBgColor(null);
			
			alphaValue = new WidgetHSlider(WidgetOrientationType.DOWN);
			alphaValue.setInsets(new WidgetInsets(5, 5, 5, 5));
			alphaValue.setBorder(
				new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
			alphaValue.setMinimum(0f);
			alphaValue.setMaximum(1f);
			alphaValue.addValueChangeObserver(this);

			add(alphaValue, WidgetBorderLayoutConstraint.SOUTH);
			add(instructions, WidgetBorderLayoutConstraint.NORTH);
			doLayout();
		}

		public void update(Observable o, Object obj) {
			alpha = (float) alphaValue.getValue();
		}
	}

	private Camera cam;
	private Node tintNode, scene;
	private AbstractInputController input;
	private Timer timer;

	private Tint tint;
	private TriMesh box;
	private Quaternion rotQuat;
	private float angle = 0;
	private Vector3f axis;

	private SliderPanel slider;

	private float alpha;
	private float counter;

	protected void update(float interpolation) {
		if (timer.getTimePerFrame() < 1) {
			angle = angle + (timer.getTimePerFrame() * - 0.5f);
			if (angle < 0) {
				angle = 360 - 0.5f;
			}
			
		}
		rotQuat.fromAngleAxis(angle, axis);
		box.setLocalRotation(rotQuat);
		
		timer.update();
		slider.handleInput();
		counter += 0.2f;
		float counter2 = (float) Math.toRadians(counter);

		tint.getTintColor().a = alpha;
		tint.getTintColor().r = (float) Math.cos(counter2);
		tint.getTintColor().g = (float) Math.sin(counter2);
		tint.getTintColor().b =
			(float) (Math.cos(counter2) * Math.sin(counter2));
		scene.updateWorldData(timer.getTimePerFrame() * 10);
		tintNode.updateWorldData(timer.getTimePerFrame() * 10);
	}

	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(scene);
		display.getRenderer().draw(tintNode);
		display.getRenderer().draw(slider);
	}

	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(
				properties.getWidth(),
				properties.getHeight(),
				properties.getDepth(),
				properties.getFreq(),
				properties.getFullscreen());

			display.setTitle("Tint Test");

			cam =
				display.getRenderer().getCamera(
					properties.getWidth(),
					properties.getHeight());
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0, 0, 3);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f dir = new Vector3f(0, 0, -1);
		cam.setFrame(loc, left, up, dir);
		display.getRenderer().setCamera(cam);

		input = new WidgetMouseTestControllerBasic(this);
		timer = Timer.getTimer(properties.getRenderer());

		rotQuat = new Quaternion();
		axis = new Vector3f(1, 1, 0.5f);
	}

	protected void initGame() {
		tintNode = new Node("tintNode");
		scene = new Node("scene");
		alpha = 0f;
		counter = 0f;

		AlphaState as1 = display.getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(false);
		as1.setTestFunction(AlphaState.TF_GEQUAL);
		as1.setEnabled(true);

		TextureState ts1 = display.getRenderer().getTextureState();
		ts1.setEnabled(true);
		ts1.setTexture(
			TextureManager.loadTexture(
				TestTint.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.jpg"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));

		TextureState font = display.getRenderer().getTextureState();
		font.setEnabled(true);
		font.setTexture(
			TextureManager.loadTexture(
				TestTint.class.getClassLoader().getResource(
					"jmetest/data/font/font.png"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));

		ZBufferState zEnabled = display.getRenderer().getZBufferState();
		zEnabled.setEnabled(true);

		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

		box = new Box("Box", min.mult(5), max.mult(5));
		box.setRenderState(ts1);
		box.setLocalTranslation(new Vector3f(0, 0, 0));

		tint =
			new Tint(
				"tint",
				new ColorRGBA(
					(float) Math.cos(counter),
					(float) Math.sin(counter),
					0f,
					alpha));
		tint.setRenderState(as1);

		scene.setRenderState(zEnabled);
		scene.attachChild(box);

		slider = new SliderPanel(input);
		slider.updateGeometricState(0.0f, false);

		tintNode.attachChild(tint);
	}

	protected void reinit() {
		WidgetAbstractFrame.destroy();
		slider.init();
	}

	protected void cleanup() {
		WidgetAbstractFrame.destroy();
	}

	public static void main(String[] args) {
		LoggingSystem.getLogger().setLevel(Level.ALL);
		TestTint app = new TestTint();
		app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}
