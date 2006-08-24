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
import javax.swing.border.*;

import com.jme.input.controls.*;

/**
 * @author Matthew D. Hicks
 */
public class GameControlContainer {
	private static final long serialVersionUID = 1L;

	private ControlConfigurationPanel ccp;
	private GameControl control;
	private JLabel label;
	private BindingField[] bindings;
	
	public GameControlContainer(ControlConfigurationPanel ccp, GameControl control, int bindings) {
		this.ccp = ccp;
		this.control = control;
		this.bindings = new BindingField[bindings];
	}
	
	public ControlConfigurationPanel getControlCongigurationPanel() {
		return ccp;
	}
	
	public void addMouseListener(MouseListener listener) {
		for (BindingField field : bindings) {
			field.addMouseListener(listener);
		}
	}
	
	public void init(JComponent container, GridBagConstraints constraints) {
		GridBagLayout layout = (GridBagLayout)container.getLayout();
		
		label = new JLabel(control.getName() + ": ");
		label.setFont(new Font("Arial", Font.BOLD, 12));
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(label, constraints);
		container.add(label);
		
		List<Binding> bList = control.getBindings();
		for (int i = 0; i < bindings.length; i++) {
			Binding b;
			if (i >= bList.size()) {
				b = null;
			} else {
				b = bList.get(i);
			}
			bindings[i] = new BindingField(this, b);
			
			if (i == bindings.length - 1) {
				constraints.gridwidth = GridBagConstraints.REMAINDER;
			}
			layout.setConstraints(bindings[i], constraints);
			container.add(bindings[i]);
		}
	}
	
	public void update() {
		label.setText(control.getName() + ": ");
		List<Binding> bList = control.getBindings();
		for (int i = 0; i < bindings.length; i++) {
			Binding b;
			if (i >= bList.size()) {
				b = null;
			} else {
				b = bList.get(i);
			}
			bindings[i].setBinding(b);
		}
	}
	
	public GameControl getGameControl() {
		return control;
	}
}
