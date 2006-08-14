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

import javax.swing.*;

import com.jme.input.controls.*;

/**
 * @author Matthew D. Hicks
 */
public class BindingField extends JTextField {
	private static final long serialVersionUID = 1L;

	private GameControlPanel panel;
	private Binding binding;
	
	public BindingField(GameControlPanel panel, Binding binding) {
		setPreferredSize(new Dimension(75, 20));
		this.panel = panel;
		setEditable(false);
		setBinding(binding);
	}
	
	public GameControlPanel getGameControlPanel() {
		return panel;
	}
	
	public Binding getBinding() {
		return binding;
	}
	
	public void setBinding(Binding binding) {
		this.binding = binding;
		updateText();
	}
	
	private void updateText() {
		if (binding == null) {
			setText("");
		} else {
			setText(binding.getName());
		}
	}
	
	public void promptForInput() {
		getGameControlPanel().getControlCongigurationPanel().getControlListener().prompt(this);
	}
}
