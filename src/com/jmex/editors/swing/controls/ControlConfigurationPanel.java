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
package com.jmex.editors.swing.controls;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

import com.jme.input.controls.*;

/**
 * @author Matthew D. Hicks
 */
public class ControlConfigurationPanel extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	
	public static int MOUSE_THRESHOLD = 5;
	public static float JOYSTICK_THRESHOLD = 0.2f;

	private List<GameControl> controls;
	private GameControlContainer[] panels;
	private int bindings;
	private ControlListener listener;
	
	public ControlConfigurationPanel(List<GameControl> controls, int bindings) {
		this.controls = controls;
		this.bindings = bindings;
		init();
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		panels = new GameControlContainer[controls.size()];
		for (int i = 0; i < controls.size(); i++) {
			GameControl control = controls.get(i);
			panels[i] = new GameControlContainer(this, control, bindings);
			panels[i].init(this, constraints);
			panels[i].addMouseListener(this);
		}
		listener = new ControlListener();
	}

	public void mouseClicked(MouseEvent evt) {
		if ((evt.getButton() == MouseEvent.BUTTON1) && (evt.getComponent() instanceof BindingField)) {
            BindingField field = (BindingField)evt.getComponent();
            field.promptForInput();
		}
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	public void mouseReleased(MouseEvent evt) {
	}
	
	public void update() {
		for (GameControlContainer panel : panels) {
			panel.update();
		}
	}
	
	public ControlListener getControlListener() {
		return listener;
	}
	
	public List<GameControl> getControls() {
		return controls;
	}

	public void replaceBindings(List<GameControl> replacements) {
		GameControl.replaceBindings(getControls(), replacements);
		update();
	}
	
	public void clearBindings() {
		GameControl.clearBindings(getControls());
		update();
	}
}
