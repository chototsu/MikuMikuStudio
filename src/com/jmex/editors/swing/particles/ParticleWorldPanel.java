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

package com.jmex.editors.swing.particles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.editors.swing.widget.ValuePanel;

public class ParticleWorldPanel extends ParticleEditPanel {

    private static final long serialVersionUID = 1L;
    
    private ValuePanel speedPanel = new ValuePanel("Speed Mod: ", "x", 0f,
            Float.MAX_VALUE, 0.01f);
    private ValuePanel precisionPanel = new ValuePanel("Precision: ", "s", 0f,
            Float.MAX_VALUE, 0.001f);
    private ValuePanel massPanel = new ValuePanel("Particle Mass: ", "", 0f,
            Float.MAX_VALUE, 0.1f);
    private ValuePanel minAgePanel = new ValuePanel("Minimum Age: ", "ms", 0f,
            Float.MAX_VALUE, 10f);
    private ValuePanel maxAgePanel = new ValuePanel("Maximum Age: ", "ms", 0f,
            Float.MAX_VALUE, 10f);
    private JLabel spacerLabel = new JLabel();

    public ParticleWorldPanel() {
        super();
        setLayout(new GridBagLayout());
        initPanel();
    }

    private void initPanel() {
        speedPanel.setBorder(createTitledBorder("PARTICLE SPEED"));
        speedPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().getParticleController().setSpeed(
                    speedPanel.getFloatValue());
            }
        });

        precisionPanel.setBorder(createTitledBorder("UPDATE PRECISION"));
        precisionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().getParticleController().setPrecision(
                        precisionPanel.getFloatValue());
            }
        });

        massPanel.setBorder(createTitledBorder("PHYSICAL PROPERTIES"));
        massPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setParticleMass(massPanel.getFloatValue());
            }
        });
        
        minAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMinimumLifeTime(minAgePanel.getFloatValue());
            }
        });
        maxAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setMaximumLifeTime(maxAgePanel.getFloatValue());
            }
        });
        JPanel agePanel = new JPanel(new GridBagLayout());
        agePanel.setBorder(createTitledBorder("PARTICLE AGE"));
        agePanel.add(minAgePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        agePanel.add(maxAgePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        
        add(speedPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
        add(precisionPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
        add(massPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
        add(agePanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
        add(spacerLabel, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
    }
    
    @Override
    public void updateWidgets() {
        speedPanel.setValue(getEdittedParticles().getParticleController().getSpeed());
        precisionPanel.setValue(getEdittedParticles().getParticleController().getPrecision());
        massPanel.setValue(getEdittedParticles().getParticle(0).getMass());
        minAgePanel.setValue(getEdittedParticles().getMinimumLifeTime());
        maxAgePanel.setValue(getEdittedParticles().getMaximumLifeTime());
    }
}
