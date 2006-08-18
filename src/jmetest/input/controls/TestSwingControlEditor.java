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
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

import javax.swing.*;

import com.jme.input.*;
import com.jme.input.controls.*;
import com.jme.util.*;
import com.jmex.awt.swingui.*;
import com.jmex.editors.swing.controls.*;
import com.jmex.game.*;
import com.jmex.game.state.*;

/**
 * @author Matthew D. Hicks
 */
public class TestSwingControlEditor {
	public static void main(String[] args) throws Exception {
		final StandardGame game = new StandardGame("TestSwingControlEditor");
		game.start();
		
		// Create our sample GameControls
		final List<GameControl> controls = new ArrayList<GameControl>();
		controls.add(new GameControl("Forward"));
		controls.add(new GameControl("Backward"));
		controls.add(new GameControl("Strafe Left"));
		controls.add(new GameControl("Strafe Right"));
		controls.add(new GameControl("Jump"));
		controls.add(new GameControl("Run"));
		controls.add(new GameControl("Duck"));
		
		// Create a game state to display the configuration menu
		final JMEDesktopState desktopState = new JMEDesktopState();
		GameStateManager.getInstance().attachChild(desktopState);
		desktopState.setActive(true);
		GameTaskQueueManager.getManager().update(new Callable<Object>() {
			public Object call() throws Exception {
				JInternalFrame frame = new JInternalFrame();
				frame.setTitle("Configure Controls");
				Container c = frame.getContentPane();
				c.setLayout(new BorderLayout());
				ControlConfigurationPanel ccp = new ControlConfigurationPanel(controls, 2);
				c.add(ccp, BorderLayout.CENTER);
				JPanel bottom = new JPanel();
				bottom.setLayout(new FlowLayout());
				JButton button = new JButton("Close");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						for (GameControl control : controls) {
							System.out.println(control.getName() + ":");
							for (Binding binding : control.getBindings()) {
								System.out.println("\t" + binding.getName());
							}
							System.out.println("-------");
						}
						game.finish();
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
