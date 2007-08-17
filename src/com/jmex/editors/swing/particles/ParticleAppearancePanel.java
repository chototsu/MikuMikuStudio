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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.editors.swing.widget.ValuePanel;
import com.jmex.editors.swing.widget.ValueSpinner;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticlePoints;

public abstract class ParticleAppearancePanel extends ParticleEditPanel {
    private static final Logger logger = Logger
            .getLogger(ParticleAppearancePanel.class.getName());
    
    private static final long serialVersionUID = 1L;
    private static File newTexture = null;

    private JCheckBox additiveBlendingBox;
    private JColorChooser colorChooser = new JColorChooser();
    private JDialog colorChooserDialog = new JDialog((JFrame)null, "Choose a color:");
    private boolean colorstart = false;
    private JLabel countLabel;
    private ValueSpinner endAlphaSpinner = new ValueSpinner(0, 255, 1);
    private JLabel endColorHex = new JLabel();
    private JPanel endColorPanel = new JPanel();
    private ValuePanel endSizePanel = new ValuePanel("End Size: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private JComboBox geomTypeBox;
    private JCheckBox velocityAlignedBox;
    private JLabel imageLabel = new JLabel();

    private Preferences prefs;
    private ValueSpinner startAlphaSpinner = new ValueSpinner(0, 255, 1);
    private JLabel startColorHex = new JLabel();
    private JPanel startColorPanel = new JPanel();
    private ValuePanel startSizePanel = new ValuePanel("Start Size: ", "", 0f,
            Float.MAX_VALUE, 1f);
    private JFileChooser textureChooser = new JFileChooser();
    private JPanel texturePanel;
    private JComboBox renderQueueCB;

    public ParticleAppearancePanel(Preferences prefs) {
        super();
        this.prefs = prefs;
        setLayout(new GridBagLayout());
        initPanel();
        setColorChooserDialogOwner(null);
        initTextureChooser();
    }

    private void initPanel() {
        countLabel = createBoldLabel("Particles: 300");
        JButton countButton = new JButton(new AbstractAction("Change...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                countButton_actionPerformed(e);
            }
        });
        countButton.setFont(new Font("Arial", Font.BOLD, 12));
        countButton.setMargin(new Insets(2, 2, 2, 2));

        JPanel countPanel = new JPanel(new GridBagLayout());
        countPanel.setBorder(createTitledBorder("PARTICLE COUNT"));
        countPanel.add(countLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 10, 5, 10), 0, 0));
        countPanel.add(countButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));

        geomTypeBox = new JComboBox(new String[] { "Quad", "Triangle", "Point",
                "Line" });
        geomTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeParticleType(geomTypeBox.getSelectedIndex());
            }
        });

        velocityAlignedBox = new JCheckBox(new AbstractAction(
                "Align with Velocity") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().setVelocityAligned(
                        velocityAlignedBox.isSelected());
            }
        });
        velocityAlignedBox.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel geomPanel = new JPanel(new GridBagLayout());
        geomPanel.setBorder(createTitledBorder("PARTICLE GEOMETRY"));
        geomPanel.add(createBoldLabel("Type:"), new GridBagConstraints(0, 0, 1,
                1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        geomPanel.add(geomTypeBox, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        geomPanel.add(velocityAlignedBox, new GridBagConstraints(0, 1, 2, 1,
                1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));

        JLabel startColorLabel = createBoldLabel("Starting Color:"), colorLabel = createBoldLabel(">>"), endColorLabel = createBoldLabel("End Color:"), startAlphaLabel = new JLabel(
                "A:"), endAlphaLabel = new JLabel("A:");
        startColorHex.setFont(new Font("Arial", Font.PLAIN, 10));
        startColorHex.setText("#FFFFFF");
        endColorHex.setFont(new Font("Arial", Font.PLAIN, 10));
        endColorHex.setText("#FFFFFF");

        startColorPanel.setBackground(Color.white);
        startColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        startColorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startColorPanel_mouseClicked(e);
            }
        });
        endColorPanel.setBackground(Color.white);
        endColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        endColorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                endColorPanel_mouseClicked(e);
            }
        });

        startAlphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().getStartColor().a = ((Number) startAlphaSpinner
                        .getValue()).intValue() / 255f;
            }
        });
        endAlphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().getEndColor().a = ((Number) endAlphaSpinner
                        .getValue()).intValue() / 255f;
            }
        });

        additiveBlendingBox = new JCheckBox(new AbstractAction(
                "Additive Blending") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                updateAlphaState(additiveBlendingBox.isSelected());
            }
        });
        additiveBlendingBox.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel colorPanel = new JPanel(new GridBagLayout());
        colorPanel.setBorder(createTitledBorder("PARTICLE COLOR"));
        colorPanel.add(startColorLabel, new GridBagConstraints(0, 0, 2, 1, 0.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 0, 10), 0, 0));
        colorPanel.add(colorLabel, new GridBagConstraints(2, 0, 1, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        0, 5, 0, 5), 0, 0));
        colorPanel.add(endColorLabel, new GridBagConstraints(3, 0, 2, 1, 0.0,
                0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 10, 0, 10), 0, 0));
        colorPanel.add(startColorPanel, new GridBagConstraints(0, 1, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 25, 25));
        colorPanel.add(endColorPanel, new GridBagConstraints(3, 1, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 25, 25));
        colorPanel.add(startColorHex, new GridBagConstraints(0, 2, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 4, 0), 0, 0));
        colorPanel.add(endColorHex, new GridBagConstraints(3, 2, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 4, 0), 0, 0));
        colorPanel.add(startAlphaSpinner, new GridBagConstraints(1, 3, 1, 1,
                0.25, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 20, 0));
        colorPanel.add(startAlphaLabel, new GridBagConstraints(0, 3, 1, 1,
                0.25, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        colorPanel.add(endAlphaLabel, new GridBagConstraints(3, 3, 1, 1, 0.25,
                0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        colorPanel.add(endAlphaSpinner, new GridBagConstraints(4, 3, 1, 1,
                0.25, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 20, 0));
        colorPanel.add(additiveBlendingBox, new GridBagConstraints(0, 4, 5, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        startSizePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setStartSize(startSizePanel.getFloatValue());
            }
        });
        endSizePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                getEdittedParticles().setEndSize(endSizePanel.getFloatValue());
            }
        });
        JPanel sizePanel = new JPanel(new GridBagLayout());
        sizePanel.setBorder(createTitledBorder("PARTICLE SIZE"));
        sizePanel.add(startSizePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        sizePanel.add(endSizePanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        JLabel textureLabel = createBoldLabel("Texture Image:");
        JButton changeTextureButton = new JButton(new AbstractAction(
                "Browse...") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                changeTexture();
            }
        });
        changeTextureButton.setFont(new Font("Arial", Font.BOLD, 12));
        changeTextureButton.setMargin(new Insets(2, 2, 2, 2));

        JButton clearTextureButton = new JButton(new AbstractAction("Clear") {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ((TextureState) getEdittedParticles()
                        .getRenderState(RenderState.RS_TEXTURE))
                        .setTexture(null);
                imageLabel.setIcon(null);
            }
        });
        clearTextureButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearTextureButton.setMargin(new Insets(2, 2, 2, 2));

        imageLabel.setBackground(Color.lightGray);
        imageLabel.setMaximumSize(new Dimension(128, 128));
        imageLabel.setMinimumSize(new Dimension(0, 0));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(false);

        texturePanel = new JPanel(new GridBagLayout());
        texturePanel.setBorder(createTitledBorder("PARTICLE TEXTURE"));
        texturePanel.add(textureLabel, new GridBagConstraints(0, 0, 2, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(changeTextureButton, new GridBagConstraints(0, 1, 1,
                1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(clearTextureButton, new GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(imageLabel, new GridBagConstraints(2, 0, 1, 2, 1.0,
                1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));


        final JLabel queueLabel = new JLabel();
        queueLabel.setForeground(Color.WHITE);
        queueLabel.setFont(new Font("Arial", Font.BOLD, 10));
        queueLabel.setText("Render Queue:");

        renderQueueCB = new JComboBox(new String[] {"INHERIT", "SKIP", "OPAQUE", "TRANSPARENT", "ORTHO"});
        renderQueueCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getEdittedParticles().setRenderQueueMode(renderQueueCB.getSelectedIndex());
            }
        });
        final JPanel queuePanel = new JPanel(new GridBagLayout());
        queuePanel.setBorder(createTitledBorder("RENDER QUEUE"));
        queuePanel.add(queueLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        queuePanel.add(renderQueueCB, new GridBagConstraints(1, 0, 1, 1, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        
        add(countPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));
        add(geomPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));
        add(colorPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 5, 5), 0, 0));
        add(sizePanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 10), 0, 0));
        add(texturePanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 5), 0, 0));
        add(queuePanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
                new Insets(5, 10, 5, 5), 0, 0));
    }

    private void changeParticleType(int newType) {
        int oldType = getEdittedParticles().getParticleType();
        if (newType == oldType) {
            return;
        }
        ParticleGeometry oldGeom = getEdittedParticles(), newGeom;
        if (newType == ParticleGeometry.PT_POINT) {
            ParticlePoints pPoints = ParticleFactory.buildPointParticles(oldGeom.getName(),
                    oldGeom.getNumParticles());
            newGeom = pPoints;
            pPoints.setPointSize(5);
            pPoints.setAntialiased(true);
        } else if (newType == ParticleGeometry.PT_LINE) {
            newGeom = ParticleFactory.buildLineParticles(oldGeom.getName(),
                oldGeom.getNumParticles());
        } else {
            newGeom = ParticleFactory.buildParticles(oldGeom.getName(),
                oldGeom.getNumParticles(), newType);
        }
        // copy appearance parameters
        newGeom.setVelocityAligned(oldGeom.isVelocityAligned());
        newGeom.setStartColor(oldGeom.getStartColor());
        newGeom.setEndColor(oldGeom.getEndColor());
        newGeom.setStartSize(oldGeom.getStartSize());
        newGeom.setEndSize(oldGeom.getEndSize());
        
        // copy origin parameters
        newGeom.setLocalTranslation(oldGeom.getLocalTranslation());
        newGeom.setLocalRotation(oldGeom.getLocalRotation());
        newGeom.setLocalScale(oldGeom.getLocalScale());
        newGeom.setOriginOffset(oldGeom.getOriginOffset());
        newGeom.setGeometry(oldGeom.getLine());
        newGeom.setGeometry(oldGeom.getRectangle());
        newGeom.setGeometry(oldGeom.getRing());
        newGeom.setEmitType(oldGeom.getEmitType());
        
        // copy emission parameters
        newGeom.setRotateWithScene(oldGeom.isRotateWithScene());
        newGeom.setEmissionDirection(oldGeom.getEmissionDirection());
        newGeom.setMinimumAngle(oldGeom.getMinimumAngle());
        newGeom.setMaximumAngle(oldGeom.getMaximumAngle());
        newGeom.setInitialVelocity(oldGeom.getInitialVelocity());
        newGeom.setParticleSpinSpeed(oldGeom.getParticleSpinSpeed());
        
        // copy flow parameters
        newGeom.setControlFlow(oldGeom.getParticleController().isControlFlow());
        newGeom.setReleaseRate(oldGeom.getReleaseRate());
        newGeom.setReleaseVariance(oldGeom.getReleaseVariance());
        newGeom.setRepeatType(oldGeom.getParticleController().getRepeatType());
        
        // copy world parameters
        newGeom.setSpeed(oldGeom.getParticleController().getSpeed());
        newGeom.setParticleMass(oldGeom.getParticle(0).getMass());
        newGeom.setMinimumLifeTime(oldGeom.getMinimumLifeTime());
        newGeom.setMaximumLifeTime(oldGeom.getMaximumLifeTime());
        newGeom.getParticleController().setPrecision(
                oldGeom.getParticleController().getPrecision());
        
        // copy influence parameters
        ArrayList<ParticleInfluence> infs = oldGeom.getInfluences();
        if (infs != null) {
            for (ParticleInfluence inf : infs) {
                newGeom.addInfluence(inf);
            }
        }
        
        // copy render states
        for (int ii = 0; ii < RenderState.RS_MAX_STATE; ii++) {
            RenderState rs = oldGeom.getRenderState(ii);
            if (rs != null) {
                newGeom.setRenderState(rs);
            }
        }
        
        requestParticleSystemOverwrite(newGeom);
    }
    
    protected abstract void requestParticleSystemOverwrite(ParticleGeometry newParticles);

    private void changeTexture() {
        try {
            int result = textureChooser.showOpenDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File textFile = textureChooser.getSelectedFile();
            prefs.put("texture_dir", textFile.getParent());

            newTexture = textFile;
            
            GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(new Callable<Object>() {
                public Object call() throws Exception{
                    loadApplyTexture();
                    return null;
                }
            });

            ImageIcon icon = new ImageIcon(
                getToolkit().createImage(textFile.getAbsolutePath()));
            imageLabel.setIcon(icon);
            validate();
        } catch (Exception ex) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "changeTexture()", "Exception",
                    ex);
        }
    }
    
    private String convColorToHex(Color c) {
        if (c == null)
            return null;
        String sRed = Integer.toHexString(c.getRed());
        if (sRed.length() == 1)
            sRed = "0" + sRed;
        String sGreen = Integer.toHexString(c.getGreen());
        if (sGreen.length() == 1)
            sGreen = "0" + sGreen;
        String sBlue = Integer.toHexString(c.getBlue());
        if (sBlue.length() == 1)
            sBlue = "0" + sBlue;
        return "#" + sRed + sGreen + sBlue;
    }
    
    private void countButton_actionPerformed(ActionEvent e) {
        String response = JOptionPane.showInputDialog(this,
                "Please enter a new particle count for this system:",
                "How many particles?", JOptionPane.PLAIN_MESSAGE);
        if (response == null)
            return;
        int particles = 100;
        try {
            particles = Integer.parseInt(response);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid number entered.  Using 100 instead.", "Invalid",
                    JOptionPane.WARNING_MESSAGE);
            particles = 100;
        }
        getEdittedParticles().recreate(particles);
        updateCountLabels();
        validate();
    }

    public void setColorChooserDialogOwner(Frame owner) {
        colorChooserDialog = new JDialog(owner, "Choose a color:");
        initColorChooser();
    }
    
    private void initColorChooser() {
        colorChooser.setColor(endColorPanel.getBackground());
        colorChooserDialog.setLayout(new BorderLayout());
        colorChooserDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        colorChooserDialog.add(colorChooser, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

        JButton okButton = new JButton("Ok");
        okButton.setOpaque(true);
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color color = colorChooser.getColor();
                if (color == null) {
                    return;
                }
                ColorRGBA rgba = makeColorRGBA(color);
                if (colorstart) {
                    rgba.a = (Integer.parseInt(startAlphaSpinner.getValue()
                            .toString()) / 255f);
                    getEdittedParticles().setStartColor(rgba);
                    startColorPanel.setBackground(color);
                } else {
                    rgba.a = (Integer.parseInt(endAlphaSpinner.getValue()
                            .toString()) / 255f);
                    getEdittedParticles().setEndColor(rgba);
                    endColorPanel.setBackground(color);
                }
                updateColorLabels();
                colorChooserDialog.setVisible(false);
            }
         });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setOpaque(true);
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               colorChooserDialog.setVisible(false);
           }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        colorChooserDialog.add(buttonPanel, BorderLayout.SOUTH);
        colorChooserDialog.setSize(colorChooserDialog.getPreferredSize());
        colorChooserDialog.setLocationRelativeTo(null);
    }

    private void initTextureChooser() {
        String tdir = prefs.get("texture_dir", null);
        if (tdir != null) {
            textureChooser.setCurrentDirectory(new File(tdir));
        }
    }

    private void loadApplyTexture() throws MalformedURLException {
        TextureState ts = (TextureState)getEdittedParticles().getRenderState(RenderState.RS_TEXTURE);
        TextureManager.clearCache();
        ts.setTexture(
                TextureManager.loadTexture(
                        newTexture.toURI().toURL(),
                        Texture.MM_LINEAR,
                        Texture.FM_LINEAR));
        ts.setEnabled(true);
        getEdittedParticles().setRenderState(ts);
        getEdittedParticles().updateRenderState();
        newTexture = null;
    }

    private Color makeColor(ColorRGBA rgba, boolean useAlpha) {
        return new Color(rgba.r, rgba.g, rgba.b, (useAlpha ? rgba.a : 1f));
    }

    private ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    private void startColorPanel_mouseClicked(MouseEvent e) {
        colorChooser.setColor(startColorPanel.getBackground());
        if (!colorChooserDialog.isVisible()) {
            colorstart = true;
            colorChooserDialog.setVisible(true);
        }
    }

    private void endColorPanel_mouseClicked(MouseEvent e) {
        colorChooser.setColor(endColorPanel.getBackground());
        if (!colorChooserDialog.isVisible()) {
            colorstart = false;
            colorChooserDialog.setVisible(true);
        }
    }

    private void updateAlphaState(boolean additive) {
        AlphaState as = (AlphaState)getEdittedParticles().getRenderState(
            RenderState.RS_ALPHA);
        if (as == null) {
            as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
            as.setBlendEnabled(true);
            as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            as.setTestEnabled(true);
            as.setTestFunction(AlphaState.TF_GREATER);
            getEdittedParticles().setRenderState(as);
            getEdittedParticles().updateRenderState();
        }
        as.setDstFunction(additive ?
            AlphaState.DB_ONE : AlphaState.DB_ONE_MINUS_SRC_ALPHA);
    }

    
    /**
     * updateColorLabels
     */
    private void updateColorLabels() {
        startColorHex.setText(convColorToHex(startColorPanel.getBackground()));
        endColorHex.setText(convColorToHex(endColorPanel.getBackground()));
    }
    
    /**
     * updateCountLabels
     */
    private void updateCountLabels() {
        int val = getEdittedParticles().getNumParticles();
        countLabel.setText("Particles: " + val);
    }

    @Override
    public void updateWidgets() {
        ParticleGeometry particleGeom = getEdittedParticles();
        updateCountLabels();
        geomTypeBox.setSelectedIndex(particleGeom.getParticleType());
        velocityAlignedBox.setSelected(particleGeom.isVelocityAligned());
        startColorPanel.setBackground(makeColor(particleGeom
                .getStartColor(), false));
        endColorPanel.setBackground(makeColor(particleGeom
                .getEndColor(), false));
        startAlphaSpinner.setValue(new Integer(makeColor(
                particleGeom.getStartColor(), true).getAlpha()));
        endAlphaSpinner.setValue(new Integer(makeColor(
                particleGeom.getEndColor(), true).getAlpha()));
        updateColorLabels();
        AlphaState as = (AlphaState)particleGeom.getRenderState(
            RenderState.RS_ALPHA);
        additiveBlendingBox.setSelected(as == null ||
            as.getDstFunction() == AlphaState.DB_ONE);
        startSizePanel.setValue(particleGeom.getStartSize());
        endSizePanel.setValue(particleGeom.getEndSize());
        renderQueueCB.setSelectedIndex(particleGeom.getRenderQueueMode());
        if (getTexturePanel().isVisible()) {
            Texture tex = null;
            try {
                tex = ((TextureState)particleGeom.getRenderState(
                        RenderState.RS_TEXTURE)).getTexture();
                if (tex != null) {
                    if (tex.getTextureKey() != null && tex.getTextureKey().getLocation() != null)
                        imageLabel.setIcon(
                                new ImageIcon(tex.getTextureKey().getLocation()));
                    else
                        imageLabel.setIcon(
                            new ImageIcon(new URL(tex.getImageLocation())));
                } else {
                    imageLabel.setIcon(null);
                }
            } catch (Exception e) {
                logger.warning("image: "+tex+" : "+ tex != null ? tex.getImageLocation() : "");
            }
        }
    }

    public JCheckBox getAdditiveBlendingBox() {
        return additiveBlendingBox;
    }

    public JPanel getTexturePanel() {
        return texturePanel;
    }
}
