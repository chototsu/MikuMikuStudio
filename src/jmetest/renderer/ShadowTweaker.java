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

package jmetest.renderer;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.renderer.pass.ShadowedRenderPass;

public class ShadowTweaker extends JFrame {
    private JCheckBox enableTextureCheckBox;
    private JSlider lPassDstSlider;
    private JSlider lPassSrcSlider;
    private JSlider tPassDstSlider;
    private JSlider tPassSrcSlider;
    private static final long serialVersionUID = 1L;

    private ButtonGroup lmethodGroup = new ButtonGroup();

    private JRadioButton additiveRadioButton;

    private JRadioButton modulativeRadioButton;
    private static ShadowedRenderPass spass;

    public ShadowTweaker(ShadowedRenderPass pass) {
        super();
        spass = pass;
        getContentPane().setLayout(new GridBagLayout());
        setTitle("ShadowTweaker");
        setBounds(100, 100, 388, 443);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JLabel blendForLightLabel = new JLabel();
        blendForLightLabel.setText("Blend for Light Passes (S/D):");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridx = 0;
        getContentPane().add(blendForLightLabel, gridBagConstraints);

        lPassSrcSlider = new JSlider();
        lPassSrcSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (additiveRadioButton != null && additiveRadioButton.isSelected() && ShadowedRenderPass.blended != null)
                    ShadowedRenderPass.blended.setSrcFunction(lPassSrcSlider.getValue());
                else if (modulativeRadioButton != null && modulativeRadioButton.isSelected() && ShadowedRenderPass.modblended != null)
                    ShadowedRenderPass.modblended.setSrcFunction(lPassSrcSlider.getValue());
            }
        });
        lPassSrcSlider.setValue(2);
        lPassSrcSlider.setSnapToTicks(true);
        lPassSrcSlider.setFont(new Font("Arial", Font.PLAIN, 8));
        lPassSrcSlider.setPaintLabels(true);
        lPassSrcSlider.setPaintTicks(true);
        lPassSrcSlider.setMaximum(6);
        lPassSrcSlider.setMajorTickSpacing(1);
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_1.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_1.weightx = 1;
        gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_1.gridwidth = 2;
        gridBagConstraints_1.gridy = 1;
        gridBagConstraints_1.gridx = 0;
        getContentPane().add(lPassSrcSlider, gridBagConstraints_1);

        lPassDstSlider = new JSlider();
        lPassDstSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (additiveRadioButton != null && additiveRadioButton.isSelected() && ShadowedRenderPass.blended != null)
                    ShadowedRenderPass.blended.setDstFunction(lPassDstSlider.getValue());
                else if (modulativeRadioButton != null && modulativeRadioButton.isSelected() && ShadowedRenderPass.modblended != null)
                    ShadowedRenderPass.modblended.setDstFunction(lPassDstSlider.getValue());
            }
        });
        lPassDstSlider.setValue(1);
        lPassDstSlider.setSnapToTicks(true);
        lPassDstSlider.setFont(new Font("Arial", Font.PLAIN, 8));
        lPassDstSlider.setPaintLabels(true);
        lPassDstSlider.setPaintTicks(true);
        lPassDstSlider.setMaximum(7);
        lPassDstSlider.setMajorTickSpacing(1);
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_3.gridwidth = 2;
        gridBagConstraints_3.gridy = 3;
        gridBagConstraints_3.gridx = 0;
        getContentPane().add(lPassDstSlider, gridBagConstraints_3);

        final JLabel blendForTextureLabel = new JLabel();
        blendForTextureLabel.setText("Blend for Texture Pass (S/D):");
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.gridwidth = 2;
        gridBagConstraints_8.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints_8.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_8.gridy = 4;
        gridBagConstraints_8.gridx = 0;
        getContentPane().add(blendForTextureLabel, gridBagConstraints_8);

        tPassSrcSlider = new JSlider();
        tPassSrcSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (ShadowedRenderPass.blendTex != null)
                    ShadowedRenderPass.blendTex.setSrcFunction(tPassSrcSlider.getValue());
            }
        });
        tPassSrcSlider.setValue(2);
        tPassSrcSlider.setSnapToTicks(true);
        tPassSrcSlider.setFont(new Font("Arial", Font.PLAIN, 8));
        tPassSrcSlider.setPaintLabels(true);
        tPassSrcSlider.setPaintTicks(true);
        tPassSrcSlider.setMajorTickSpacing(1);
        tPassSrcSlider.setMaximum(6);
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_9.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_9.gridwidth = 2;
        gridBagConstraints_9.gridy = 5;
        gridBagConstraints_9.gridx = 0;
        getContentPane().add(tPassSrcSlider, gridBagConstraints_9);

        tPassDstSlider = new JSlider();
        tPassDstSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (ShadowedRenderPass.blendTex != null)
                    ShadowedRenderPass.blendTex.setDstFunction(tPassDstSlider.getValue());
            }
        });
        tPassDstSlider.setValue(0);
        tPassDstSlider.setSnapToTicks(true);
        tPassDstSlider.setFont(new Font("Arial", Font.PLAIN, 8));
        tPassDstSlider.setPaintLabels(true);
        tPassDstSlider.setPaintTicks(true);
        tPassDstSlider.setMajorTickSpacing(1);
        tPassDstSlider.setMaximum(7);
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.insets = new Insets(0, 10, 0, 10);
        gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_10.gridwidth = 2;
        gridBagConstraints_10.gridy = 6;
        gridBagConstraints_10.gridx = 0;
        getContentPane().add(tPassDstSlider, gridBagConstraints_10);

        final JCheckBox enableShadowsCheckBox = new JCheckBox();
        enableShadowsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spass.setRenderShadows(enableShadowsCheckBox.isSelected());
            }
        });
        enableShadowsCheckBox.setSelected(true);
        enableShadowsCheckBox.setText("Enable Shadows");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_4.insets = new Insets(10, 10, 0, 10);
        gridBagConstraints_4.gridy = 7;
        gridBagConstraints_4.gridx = 0;
        getContentPane().add(enableShadowsCheckBox, gridBagConstraints_4);

        enableTextureCheckBox = new JCheckBox();
        enableTextureCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ShadowedRenderPass.rTexture = enableTextureCheckBox.isSelected();
            }
        });
        enableTextureCheckBox.setSelected(true);
        enableTextureCheckBox.setText("Enable Texture");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints_2.gridy = 7;
        gridBagConstraints_2.gridx = 1;
        getContentPane().add(enableTextureCheckBox, gridBagConstraints_2);

        final JLabel methodLabel = new JLabel();
        methodLabel.setText("Lighting Method:");
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.insets = new Insets(4, 10, 0, 10);
        gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_7.gridy = 8;
        gridBagConstraints_7.gridx = 0;
        getContentPane().add(methodLabel, gridBagConstraints_7);

        additiveRadioButton = new JRadioButton();
        additiveRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLMode();
            }
        });
        lmethodGroup.add(additiveRadioButton);
        additiveRadioButton.setSelected(true);
        additiveRadioButton.setText("ADDITIVE");
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.weightx = .5;
        gridBagConstraints_5.gridy = 9;
        gridBagConstraints_5.gridx = 0;
        getContentPane().add(additiveRadioButton, gridBagConstraints_5);

        modulativeRadioButton = new JRadioButton();
        modulativeRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setLMode();
            }
        });
        lmethodGroup.add(modulativeRadioButton);
        modulativeRadioButton.setText("MODULATIVE");
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.weightx = .5;
        gridBagConstraints_6.gridy = 9;
        gridBagConstraints_6.gridx = 1;
        getContentPane().add(modulativeRadioButton, gridBagConstraints_6);
    }

    public void setLMode() {
        if (additiveRadioButton.isSelected()) {
            spass.setLightingMethod(ShadowedRenderPass.ADDITIVE);
            lPassDstSlider.setValue(1);
            lPassSrcSlider.setValue(2);
            tPassDstSlider.setValue(0);
            tPassSrcSlider.setValue(2);
            enableTextureCheckBox.setText("Enable Texture Pass");
        }
        else {
            spass.setLightingMethod(ShadowedRenderPass.MODULATIVE);
            lPassDstSlider.setValue(5);
            lPassSrcSlider.setValue(2);
            enableTextureCheckBox.setText("Enable Dark Pass");
        }
    }
    
}
