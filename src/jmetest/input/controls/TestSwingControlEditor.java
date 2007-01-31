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
package jmetest.input.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

import javax.swing.*;

import com.jme.image.*;
import com.jme.input.*;
import com.jme.input.controls.*;
import com.jme.input.controls.controller.*;
import com.jme.input.controls.controller.camera.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.shape.Box;
import com.jme.scene.state.*;
import com.jme.util.*;
import com.jmex.awt.swingui.*;
import com.jmex.editors.swing.controls.*;
import com.jmex.game.*;
import com.jmex.game.state.*;

/**
 * @author Matthew D. Hicks
 */
public class TestSwingControlEditor {
	private static GameControlManager manager;
	
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("TestSwingControlEditor");
		game.start();
		
		// Create our sample GameControls
		manager = GameControlManager.load(game.getSettings());
		if (manager == null) {
			manager = new GameControlManager();
			manager.addControl("Forward");
			manager.addControl("Backward");
			manager.addControl("Rotate Left");
			manager.addControl("Rotate Right");
			manager.addControl("Jump");
			manager.addControl("Crouch");
			manager.addControl("Run");
			manager.addControl("Fire");
			manager.addControl("Cycle Camera");
		}
		
		// Create a game state to display the configuration menu
		final JMEDesktopState desktopState = new JMEDesktopState();
		GameStateManager.getInstance().attachChild(desktopState);
		desktopState.setActive(true);
		
		BasicGameState state = new BasicGameState("Basic");
		GameStateManager.getInstance().attachChild(state);
		state.setActive(true);
		
		// Create Box
		Box box = new Box("Test Node", new Vector3f(), 5.0f, 5.0f, 5.0f);
		state.getRootNode().attachChild(box);
		TextureState ts = game.getDisplay().getRenderer().createTextureState();
	    Texture t = TextureManager.loadTexture(TestSwingControlEditor.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR);
	    t.setWrap(Texture.WM_WRAP_S_WRAP_T);
	    ts.setTexture(t);
	    box.setRenderState(ts); 
	    box.updateRenderState();
		
		// Create Throttle Controller
		state.getRootNode().addController(new ThrottleController(box, manager.getControl("Forward"), 5.0f, manager.getControl("Backward"), -5.0f, 0.5f, Axis.Z));
		// Create Rotation Controller
		state.getRootNode().addController(new RotationController(box, manager.getControl("Rotate Left"), manager.getControl("Rotate Right"), 0.2f, Axis.Y));
		// Create ActionController
		GameControlAction action = new GameControlAction() {
			public void pressed(GameControl control, float time) {
				System.out.println("Pressed: " + control.getName() + ", elapsed: " + time);
			}

			public void released(GameControl control, float time) {
				System.out.println("Released: " + control.getName() + " after " + time);
			}
		};
		// Jump and Crouch only care about press and release
		state.getRootNode().addController(new ActionController(manager.getControl("Jump"), action));
		state.getRootNode().addController(new ActionController(manager.getControl("Crouch"), action));
		// Run cares about the change - doesn't really make sense, but this is just for testing
		ControlChangeListener listener = new ControlChangeListener() {
			public void changed(GameControl control, float oldValue, float newValue, float time) {
				System.out.println("Changed: " + control.getName() + ", " + oldValue + ", " + newValue + ", " + time);
			}
		};
		state.getRootNode().addController(new ActionChangeController(manager.getControl("Run"), listener));
		Runnable runnable = new Runnable() {
			private long lastRun;
			public void run() {
				if (lastRun == 0) lastRun = System.currentTimeMillis();
				System.out.println("KABOOM: " + (System.currentTimeMillis() - lastRun));
				lastRun = System.currentTimeMillis();
			}
		};
		// Fire action can only occur once per second
		state.getRootNode().addController(new ActionRepeatController(manager.getControl("Fire"), 1000, runnable));
		// Create CameraController
		CameraController cc = new CameraController(box, game.getCamera(), manager.getControl("Cycle Camera"));
		cc.addPerspective(new CameraPerspective() {
			private Camera camera;
			private Vector3f location;
			private Vector3f dir;
			private Vector3f left;
			private Vector3f up;
			
			public void update(Camera camera, Spatial spatial, float time) {
				if (this.camera == null) {
					this.camera = camera;
					try {
						location = (Vector3f)camera.getLocation().clone();
						dir = (Vector3f)camera.getDirection().clone();
						left = (Vector3f)camera.getLeft().clone();
						up = (Vector3f)camera.getUp().clone();
					} catch(Exception exc) {
						exc.printStackTrace();
					}
				} else if (!camera.getLocation().equals(location)) {
					System.out.println("Changing from: " + camera.getDirection() + " to " + dir);
					System.out.println("Another: " + camera.getUp() + "\nAnd: " + camera.getLeft());
					camera.setLocation(location);
					camera.setDirection(dir);
					camera.setLeft(left);
					camera.setUp(up);
				}
			}
		});
		cc.addPerspective(new FixedCameraPerspective(new Vector3f(0.0f, 0.0f, -15.0f)));
		state.getRootNode().addController(cc);
		
		GameTaskQueueManager.getManager().update(new Callable<Object>() {
			public Object call() throws Exception {
				JInternalFrame frame = new JInternalFrame();
				frame.setTitle("Configure Controls");
				Container c = frame.getContentPane();
				c.setLayout(new BorderLayout());
				final GameControlEditor editor = new GameControlEditor(manager, 2);
				c.add(editor, BorderLayout.CENTER);
				JPanel bottom = new JPanel();
				bottom.setLayout(new FlowLayout());
				JButton button = new JButton("Close");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						game.finish();
					}
				});
				bottom.add(button);
				button = new JButton("Clear");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						editor.clear();
					}
				});
				bottom.add(button);
				button = new JButton("Reset");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						editor.reset();
					}
				});
				bottom.add(button);
				button = new JButton("Apply");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						editor.apply();	// Apply bindings
						GameControlManager.save(manager, game.getSettings());	// Save them
						for (GameControl control : manager.getControls()) {
							System.out.println(control.getName() + ":");
							for (Binding binding : control.getBindings()) {
								System.out.println("\t" + binding.getName());
							}
							System.out.println("-------");
						}
					}
				});
				bottom.add(button);
				c.add(bottom, BorderLayout.SOUTH);
				frame.pack();
				frame.setLocation(200, 100);
				frame.setVisible(true);
				desktopState.getDesktop().getJDesktop().add(frame);
				
				// Show the mouse cursor
				MouseInput.get().setCursorVisible(true);
				
				return null;
			}
		});
	}
}
