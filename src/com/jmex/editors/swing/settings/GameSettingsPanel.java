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
import java.awt.event.*;

import java.util.*;
import java.util.List;

import javax.swing.*;

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
	private JComboBox frequency;
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
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints constraints = new GridBagConstraints();
		
		List<Component> list = getSettingsComponents();
		revert();
		JLabel label = null;
		for (int i = 0; i < list.size(); i++) {
			Component c = list.get(i);
			label = new JLabel(" " + c.getName() + ": ");
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			
			constraints.gridwidth = 1;
			constraints.anchor = GridBagConstraints.EAST;
			constraints.insets = new Insets(5, 5, 5, 5);
			layout.setConstraints(label, constraints);
			add(label);
			
			constraints.anchor = GridBagConstraints.WEST;
			constraints.gridwidth = GridBagConstraints.REMAINDER;
			layout.setConstraints(c, constraints);
			add(c);
		}
	}
	
	protected List<Component> getSettingsComponents() {
		List<Component> components = new ArrayList<Component>();
		components.add(createRenderer());
		components.add(createResolution());
		components.add(createDepth());
		components.add(createFrequency());
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
	
	protected Component createFrequency() {
		frequency = new JComboBox(new Object[] {
						"60",
						"70",
						"72",
						"75",
						"85",
						"100",
						"120",
						"140"
		});
		frequency.setName("Frequency");
		return frequency;
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
	
	public void defaults() {
		try {
			settings.clear();
			revert();
		} catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public void revert() {
		renderer.setSelectedItem(settings.getRenderer());
		resolution.setSelectedItem(settings.getWidth() + "x" + settings.getHeight());
		depth.setSelectedItem(String.valueOf(settings.getDepth()));
		frequency.setSelectedItem(String.valueOf(settings.getFrequency()));
		verticalSync.setSelectedItem(settings.isVerticalSync() ? "Yes" : "No");
		fullscreen.setSelectedItem(settings.isFullscreen() ? "Yes" : "No");
		music.setSelectedItem(settings.isMusic() ? "Yes" : "No");
		sfx.setSelectedItem(settings.isSFX() ? "Yes" : "No");
		depthBits.setSelectedItem(String.valueOf(settings.getDepthBits()));
		alphaBits.setSelectedItem(String.valueOf(settings.getAlphaBits()));
		stencilBits.setSelectedItem(String.valueOf(settings.getStencilBits()));
		samples.setSelectedItem(String.valueOf(settings.getSamples()));
	}

	public void apply() {
		settings.setRenderer((String)renderer.getSelectedItem());
		String[] parser = ((String)resolution.getSelectedItem()).split("x");
		settings.setWidth(Integer.parseInt(parser[0]));
		settings.setHeight(Integer.parseInt(parser[1]));
		settings.setDepth(Integer.parseInt((String)depth.getSelectedItem()));
		settings.setFrequency(Integer.parseInt((String)frequency.getSelectedItem()));
		settings.setVerticalSync(verticalSync.getSelectedItem().equals("Yes"));
		settings.setFullscreen(fullscreen.getSelectedItem().equals("Yes"));
		settings.setMusic(music.getSelectedItem().equals("Yes"));
		settings.setSFX(sfx.getSelectedItem().equals("Yes"));
		settings.setDepthBits(Integer.parseInt((String)depthBits.getSelectedItem()));
		settings.setAlphaBits(Integer.parseInt((String)alphaBits.getSelectedItem()));
		settings.setStencilBits(Integer.parseInt((String)stencilBits.getSelectedItem()));
		settings.setSamples(Integer.parseInt((String)samples.getSelectedItem()));
	}
}