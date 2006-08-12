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
package com.jmex.editors.swing.settings;

import java.awt.*;

import java.util.*;
import java.util.List;

import javax.swing.*;

import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import com.jme.system.*;

/**
 * @author Matthew D. Hicks
 */
public class GameSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private GameSettings settings;
	
	private JComboBox renderer;
	private JComboBox resolution;
	private JComboBox depth;
	private JComboBox refresh;
	private JComboBox verticalSync;
	private JComboBox fullscreen;
	private JComboBox music;
	private JComboBox sfx;
	private JComboBox depthBits;
	private JComboBox alphaBits;
	private JComboBox stencilBits;
	private JComboBox samples;
	
	public GameSettingsPanel(GameSettings settings) {
		this.settings = settings;
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		JPanel labels = new JPanel();
		JPanel components = new JPanel();
		List<Component> list = getSettingsComponents();
		labels.setLayout(new GridLayout(list.size(), 1));
		components.setLayout(new GridLayout(list.size(), 1));
		add(labels, BorderLayout.WEST);
		add(components, BorderLayout.CENTER);
		JLabel label = null;
		for (int i = 0; i < list.size(); i++) {
			Component c = list.get(i);
			label = new JLabel(c.getName() + ": ");
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			labels.add(label);
			components.add(c);
		}
	}
	
	protected List<Component> getSettingsComponents() {
		List<Component> components = new ArrayList<Component>();
		components.add(createRenderer());
		components.add(createResolution());
		components.add(createDepth());
		components.add(createRefresh());
		components.add(createVerticalSync());
		components.add(createFullscreen());
		components.add(createMusic());
		components.add(createSFX());
		components.add(createDepthBits());
		components.add(createAlphaBits());
		components.add(createStencilBits());
		components.add(createSamples());
		return components;
	}
	
	protected Component createRenderer() {
		renderer = new JComboBox(new Object[] {"LWJGL"});
		renderer.setName("Renderer");
		return renderer;
	}
	
	protected Component createResolution() {
		resolution = new JComboBox(new Object[] {
						"640x480",
						"800x600",
						"1024x768",
						"1280x1024",
						"1600x1200",
						"1440x900"});
		resolution.setName("Resolution");
		return resolution;
	}
	
	protected Component createDepth() {
		depth = new JComboBox(new Object[] {
						"16",
						"32"});
		depth.setName("Depth");
		return depth;
	}
	
	protected Component createRefresh() {
		refresh = new JComboBox(new Object[] {
						"60",
						"70",
						"72",
						"75",
						"85",
						"100",
						"120",
						"140"
		});
		refresh.setName("Refresh");
		return refresh;
	}
	
	protected Component createVerticalSync() {
		verticalSync = new JComboBox(new Object[] {"Yes", "No"});
		verticalSync.setName("Vertical Sync");
		return verticalSync;
	}
	
	protected Component createFullscreen() {
		fullscreen = new JComboBox(new Object[] {"Yes", "No"});
		fullscreen.setName("Fullscreen");
		return fullscreen;
	}
	
	protected Component createMusic() {
		music = new JComboBox(new Object[] {"Yes", "No"});
		music.setName("Music");
		return music;
	}
	
	protected Component createSFX() {
		sfx = new JComboBox(new Object[] {"Yes", "No"});
		sfx.setName("Sound Effects");
		return sfx;
	}
	
	protected Component createDepthBits() {
		depthBits = new JComboBox(new Object[] {"8"});
		depthBits.setName("Depth Bits");
		return depthBits;
	}
	
	protected Component createAlphaBits() {
		alphaBits = new JComboBox(new Object[] {"0"});
		alphaBits.setName("Alpha Bits");
		return alphaBits;
	}
	
	protected Component createStencilBits() {
		stencilBits = new JComboBox(new Object[] {"0"});
		stencilBits.setName("Stencil Bits");
		return stencilBits;
	}
	
	protected Component createSamples() {
		samples = new JComboBox(new Object[] {"0"});
		samples.setName("Samples");
		return samples;
	}
}
