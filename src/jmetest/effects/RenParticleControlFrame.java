/*
 * Created on Jan 20, 2004
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jmetest.effects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.effects.*;

public class RenParticleControlFrame extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane mainTabbedPane1 = new JTabbedPane();
  JPanel appPanel = new JPanel();
  JPanel emitPanel = new JPanel();
  JPanel worldPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JPanel colorPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JLabel startColorLabel = new JLabel();
  JLabel colorLabel = new JLabel();
  JLabel endColorLabel = new JLabel();
  JPanel startColorPanel = new JPanel();
  JPanel endColorPanel = new JPanel();
  JLabel startColorHex = new JLabel();
  JLabel endColorHex = new JLabel();
  TitledBorder colorBorder;
  JSpinner startAlphaSpinner = new JSpinner();
  JLabel startAlphaLabel = new JLabel();
  JLabel endAlphaLabel = new JLabel();
  JSpinner endAlphaSpinner = new JSpinner();
  JPanel sizePanel = new JPanel();
  JLabel startSizeLabel = new JLabel();
  JSlider startSizeSlider = new JSlider();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  TitledBorder sizeBorder;
  JLabel endSizeLabel = new JLabel();
  JSlider endSizeSlider = new JSlider();
  TitledBorder ageBorder;
  JPanel speedPanel = new JPanel();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  TitledBorder speedBorder;
  JLabel speedLabel = new JLabel();
  JSlider speedSlider = new JSlider();
  JPanel texturePanel = new JPanel();
  TitledBorder textureBorder;
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  JLabel textureLabel = new JLabel();
  JButton changeTextureButton = new JButton();
  JLabel imageLabel = new JLabel();
  JPanel gravityPanel = new JPanel();
  TitledBorder gravityBorder;
  JSlider gravYSlider = new JSlider();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JSlider gravZSlider = new JSlider();
  JSlider gravXSlider = new JSlider();
  JLabel gravXLabel = new JLabel();
  JLabel gravYLabel = new JLabel();
  JLabel gravZLabel = new JLabel();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JPanel agePanel = new JPanel();
  JLabel minAgeLabel = new JLabel();
  JSlider minAgeSlider = new JSlider();
  JLabel emitYLabel = new JLabel();
  JLabel emitZLabel = new JLabel();
  JSlider emitYSlider = new JSlider();
  JLabel emitXLabel = new JLabel();
  JSlider emitXSlider = new JSlider();
  JSlider emitZSlider = new JSlider();
  JPanel directionPanel = new JPanel();
  TitledBorder emitBorder;
  JPanel anglePanel = new JPanel();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  TitledBorder angleBorder;
  GridBagLayout gridBagLayout11 = new GridBagLayout();
  JLabel angleLabel = new JLabel();
  JSlider angleSlider = new JSlider();
  JPanel randomPanel = new JPanel();
  TitledBorder randomBorder;
  GridBagLayout gridBagLayout12 = new GridBagLayout();
  JLabel randomLabel = new JLabel();
  JSlider randomSlider = new JSlider();
  JPanel examplesPanel = new JPanel();
  GridBagLayout gridBagLayout13 = new GridBagLayout();
  JScrollPane exampleSP = new JScrollPane();
  JList exampleList = null;
  JLabel examplesLabel = new JLabel();
  JButton exampleButton = new JButton();
  JPanel codePanel = new JPanel();
  GridBagLayout gridBagLayout14 = new GridBagLayout();
  JLabel codeLabel = new JLabel();
  JScrollPane codeSP = new JScrollPane();
  JTextArea codeTextArea = new JTextArea();
  DefaultListModel exampleModel = new DefaultListModel();

  /**
  * <code>RenParticleControlFrame</code>
  *
  * @author Joshua Slack
  * @version $Id: RenParticleControlFrame.java,v 1.8 2004-03-24 20:14:17 renanse Exp $
  *
  */

 public RenParticleControlFrame() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    colorBorder = new TitledBorder("");
    sizeBorder = new TitledBorder("");
    ageBorder = new TitledBorder(" PARTICLE AGE ");
    speedBorder = new TitledBorder(" PARTICLE SPEED ");
    textureBorder = new TitledBorder(" PARTICLE TEXTURE ");
    gravityBorder = new TitledBorder(" GRAVITY ");
    emitBorder = new TitledBorder(" EMISSION DIRECTION ");
    angleBorder = new TitledBorder("");
    randomBorder = new TitledBorder("");
    this.getContentPane().setLayout(borderLayout1);
    appPanel.setLayout(gridBagLayout1);
    emitPanel.setLayout(gridBagLayout2);
    worldPanel.setLayout(gridBagLayout3);
    colorPanel.setLayout(gridBagLayout4);
    startColorLabel.setFont(new java.awt.Font("Arial", 1, 13));
    startColorLabel.setRequestFocusEnabled(true);
    startColorLabel.setText("Starting Color:");
    colorLabel.setFont(new java.awt.Font("Arial", 1, 14));
    colorLabel.setText(">>");
    endColorLabel.setFont(new java.awt.Font("Arial", 1, 13));
    endColorLabel.setText("End Color:");
    startColorHex.setFont(new java.awt.Font("Arial", 0, 10));
    startColorHex.setText("#FFFFFF");
    endColorHex.setFont(new java.awt.Font("Arial", 0, 10));
    endColorHex.setText("#FFFFFF");
    startColorPanel.setBackground(Color.white);
    startColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    startColorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        startColorPanel_mouseClicked(e);
      }
    });
    endColorPanel.setBackground(Color.white);
    endColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    endColorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        endColorPanel_mouseClicked(e);
      }
    });
    colorPanel.setBorder(colorBorder);
    colorPanel.setOpaque(false);
    colorBorder.setTitleColor(Color.black);
    colorBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    colorBorder.setTitle(" PARTICLE COLOR ");
    colorBorder.setBorder(BorderFactory.createEtchedBorder());
    startAlphaLabel.setFont(new java.awt.Font("Arial", 0, 11));
    startAlphaLabel.setText("alpha:");
    endAlphaLabel.setFont(new java.awt.Font("Arial", 0, 11));
    endAlphaLabel.setText("alpha:");
    startSizeLabel.setFont(new java.awt.Font("Arial", 1, 13));
    startSizeLabel.setText("Start Size:  0.0");
    sizePanel.setLayout(gridBagLayout5);
    sizePanel.setBorder(sizeBorder);
    sizePanel.setOpaque(false);
    sizeBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    sizeBorder.setTitle(" PARTICLE SIZE ");
    endSizeLabel.setFont(new java.awt.Font("Arial", 1, 13));
    endSizeLabel.setText("End Size: 0.0");
    endSizeSlider.setMajorTickSpacing(10);
    endSizeSlider.setMaximum(200);
    endSizeSlider.setMinorTickSpacing(1);
    endSizeSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = endSizeSlider.getValue();
        TestRenParticleGUI.manager.setEndSize(val/10f);
        updateSizeLabels();
        regenCode();
      }
    });
    startSizeSlider.setMajorTickSpacing(10);
    startSizeSlider.setMaximum(200);
    startSizeSlider.setMinorTickSpacing(1);
    startSizeSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = startSizeSlider.getValue();
        TestRenParticleGUI.manager.setStartSize(val/10f);
        updateSizeLabels();
        regenCode();
      }
    });
    ageBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    ageBorder.setBorder(BorderFactory.createEtchedBorder());
    speedPanel.setLayout(gridBagLayout7);
    speedPanel.setBorder(speedBorder);
    speedPanel.setOpaque(false);
    speedLabel.setFont(new java.awt.Font("Arial", 1, 13));
    speedLabel.setText("Speed Mod.: 0%");
    speedBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    speedSlider.setMaximum(100);
    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = speedSlider.getValue();
        TestRenParticleGUI.manager.setParticlesSpeed((float)val * .1f);
        updateSpeedLabels();
        regenCode();
      }
    });
    texturePanel.setBorder(textureBorder);
    texturePanel.setLayout(gridBagLayout8);
    textureBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    textureLabel.setFont(new java.awt.Font("Arial", 1, 13));
    textureLabel.setText("Texture Image:");
    changeTextureButton.setEnabled(false);
    changeTextureButton.setFont(new java.awt.Font("Arial", 1, 12));
    changeTextureButton.setMargin(new Insets(2, 2, 2, 2));
    changeTextureButton.setText("Browse...");
    imageLabel.setBackground(Color.lightGray);
    imageLabel.setBorder(BorderFactory.createLineBorder(SystemColor.controlText,1));
    imageLabel.setMaximumSize(new Dimension(128, 128));
    imageLabel.setMinimumSize(new Dimension(0, 0));
    imageLabel.setOpaque(true);
    imageLabel.setText("");

    gravityPanel.setBorder(gravityBorder);
    gravityPanel.setLayout(gridBagLayout9);
    gravityBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));

    gravXLabel.setText("X");
    gravXSlider.setOrientation(JSlider.VERTICAL);
    gravXSlider.setInverted(false);
    gravXSlider.setMajorTickSpacing(16);
    gravXSlider.setMaximum(32);
    gravXSlider.setMinimum(-32);
    gravXSlider.setMinorTickSpacing(4);
    gravXSlider.setPaintLabels(true);
    gravXSlider.setPaintTicks(true);
    gravXSlider.setPaintTrack(true);
    gravXSlider.setValue(0);
    gravXSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravXSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().x = (float)val * 0.001f;
        regenCode();
      }
    });

    gravYLabel.setText("Y");
    gravYSlider.setOrientation(JSlider.VERTICAL);
    gravYSlider.setInverted(false);
    gravYSlider.setMajorTickSpacing(16);
    gravYSlider.setMaximum(32);
    gravYSlider.setMinimum(-32);
    gravYSlider.setMinorTickSpacing(4);
    gravYSlider.setPaintLabels(true);
    gravYSlider.setPaintTicks(true);
    gravYSlider.setPaintTrack(true);
    gravYSlider.setValue(0);
    gravYSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravYSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().y = (float)val * 0.001f;
        regenCode();
      }
    });

    gravZLabel.setText("Z");
    gravZSlider.setOrientation(JSlider.VERTICAL);
    gravZSlider.setInverted(false);
    gravZSlider.setMajorTickSpacing(16);
    gravZSlider.setMaximum(32);
    gravZSlider.setMinimum(-32);
    gravZSlider.setMinorTickSpacing(4);
    gravZSlider.setPaintLabels(true);
    gravZSlider.setPaintTicks(true);
    gravZSlider.setPaintTrack(true);
    gravZSlider.setValue(0);
    gravZSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravZSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().z = (float)val * 0.001f;
        regenCode();
      }
    });

    agePanel.setLayout(gridBagLayout6);
    agePanel.setBorder(ageBorder);
    minAgeLabel.setFont(new java.awt.Font("Arial", 1, 13));
    minAgeLabel.setText("Minimum Age: 1000ms");
    minAgeSlider.setMajorTickSpacing(1000);
    minAgeSlider.setMaximum(10000);
    minAgeSlider.setMinimum(0);
    minAgeSlider.setMinorTickSpacing(100);
    minAgeSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = minAgeSlider.getValue();
        TestRenParticleGUI.manager.setParticlesMinimumLifeTime((float)val);
        updateAgeLabels();
        regenCode();
      }
    });

    directionPanel.setBorder(emitBorder);
    directionPanel.setLayout(gridBagLayout10);
    emitBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    emitBorder.setTitle(" DIRECTION ");
    emitZSlider.setOrientation(JSlider.VERTICAL);
    emitZSlider.setMajorTickSpacing(5);
    emitZSlider.setMinimum(-10);
    emitZSlider.setMaximum(10);
    emitZSlider.setMinorTickSpacing(1);
    emitZSlider.setPaintLabels(true);
    emitZSlider.setPaintTicks(true);
    emitZSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = emitZSlider.getValue();
        if (TestRenParticleGUI.manager != null) {
          TestRenParticleGUI.manager.getEmissionDirection().z = (float) val * .1f;
          TestRenParticleGUI.manager.updateRotationMatrix();
        }
        regenCode();
      }
    });
    emitYSlider.setOrientation(JSlider.VERTICAL);
    emitYSlider.setMajorTickSpacing(5);
    emitYSlider.setMinimum(-10);
    emitYSlider.setMaximum(10);
    emitYSlider.setMinorTickSpacing(1);
    emitYSlider.setPaintLabels(true);
    emitYSlider.setPaintTicks(true);
    emitYSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = emitYSlider.getValue();
        if (TestRenParticleGUI.manager != null) {
          TestRenParticleGUI.manager.getEmissionDirection().y = (float)val * .1f;
          TestRenParticleGUI.manager.updateRotationMatrix();
        }
        regenCode();
      }
    });
    emitXSlider.setOrientation(JSlider.VERTICAL);
    emitXSlider.setMajorTickSpacing(5);
    emitXSlider.setMinimum(-10);
    emitXSlider.setMaximum(10);
    emitXSlider.setMinorTickSpacing(1);
    emitXSlider.setPaintLabels(true);
    emitXSlider.setPaintTicks(true);
    emitXSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = emitXSlider.getValue();
        if (TestRenParticleGUI.manager != null) {
          TestRenParticleGUI.manager.getEmissionDirection().x = (float)val * .1f;
          TestRenParticleGUI.manager.updateRotationMatrix();
        }
        regenCode();
      }
    });
    emitXLabel.setFont(new java.awt.Font("Arial", 1, 13));
    emitXLabel.setText("X");
    emitYLabel.setFont(new java.awt.Font("Arial", 1, 13));
    emitYLabel.setText("Y");
    emitZLabel.setFont(new java.awt.Font("Arial", 1, 13));
    emitZLabel.setText("Z");
    anglePanel.setBorder(angleBorder);
    anglePanel.setLayout(gridBagLayout11);
    angleBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    angleBorder.setTitle(" ANGLE ");
    angleLabel.setText("Degrees Off Direction:  0");
    angleLabel.setFont(new java.awt.Font("Arial", 1, 13));
    angleSlider.setValue(0);
    angleSlider.setMinimum(0);
    angleSlider.setMaximum(180);
    angleSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = angleSlider.getValue();
        TestRenParticleGUI.manager.setEmissionMaximumAngle((float)val * FastMath.DEG_TO_RAD);
        updateAngleLabels();
        regenCode();
      }
    });
    randomPanel.setBorder(randomBorder);
    randomPanel.setLayout(gridBagLayout12);
    randomBorder.setTitleFont(new java.awt.Font("Arial", 0, 10));
    randomBorder.setTitle(" SYSTEM RANDOMNESS ");
    randomLabel.setFont(new java.awt.Font("Arial", 1, 13));
    randomLabel.setText("Random Factor: 0.0");
    randomSlider.setValue(0);
    randomSlider.setMaximum(100);
    randomSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = randomSlider.getValue();
        TestRenParticleGUI.manager.setRandomMod((float)val*.01f);
        updateRandomLabels();
        regenCode();
      }
    });
    examplesPanel.setLayout(gridBagLayout13);
    examplesLabel.setFont(new java.awt.Font("Arial", 1, 13));
    examplesLabel.setText("Prebuilt Examples:");
    exampleButton.setFont(new java.awt.Font("Arial", 0, 12));
    exampleButton.setMargin(new Insets(2, 14, 2, 14));
    exampleButton.setText("Apply");
    exampleButton.setEnabled(false);
    exampleButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyExample();
      }
    });
    updateExampleModel();
    exampleList = new JList(exampleModel);
    exampleList.setFont(new java.awt.Font("Arial", 0, 13));
    exampleList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (exampleList.getSelectedValue() instanceof String)
          exampleButton.setEnabled(true);
        else
          exampleButton.setEnabled(false);
      }
    });
    codePanel.setFont(new java.awt.Font("Arial", 0, 13));
    codePanel.setLayout(gridBagLayout14);
    appPanel.setFont(new java.awt.Font("Arial", 0, 13));
    emitPanel.setFont(new java.awt.Font("Arial", 0, 13));
    worldPanel.setFont(new java.awt.Font("Arial", 0, 13));
    examplesPanel.setFont(new java.awt.Font("Arial", 0, 13));
    mainTabbedPane1.setFont(new java.awt.Font("Arial", 0, 13));
    codeLabel.setFont(new java.awt.Font("Arial", 1, 13));
    codeLabel.setText("Particle Code:");
    codeTextArea.setFont(new java.awt.Font("Monospaced", 0, 10));
    codeTextArea.setText("");
    codeTextArea.setEditable(false);
    codeTextArea.setAutoscrolls(true);
    this.getContentPane().add(mainTabbedPane1, BorderLayout.CENTER);
    mainTabbedPane1.add(appPanel,   "Appearance");
    mainTabbedPane1.add(emitPanel,     "Emission");
    emitPanel.add(directionPanel,         new GridBagConstraints(0, 0, 1, 2, 0.5, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 5), 0, 0));
    directionPanel.add(emitXSlider,          new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 0), 0, 0));
    directionPanel.add(emitYSlider,            new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 5, 0), 0, 0));
    directionPanel.add(emitZSlider,         new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 5, 10), 0, 0));
    directionPanel.add(emitXLabel,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    directionPanel.add(emitYLabel,     new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    directionPanel.add(emitZLabel,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    mainTabbedPane1.add(worldPanel,    "World");

    worldPanel.add(speedPanel,       new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 0, 0));

    startAlphaSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        TestRenParticleGUI.manager.getStartColor().a = (Integer.parseInt(startAlphaSpinner.getValue().toString()) / 255f);
        regenCode();
      }
    });

    endAlphaSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        TestRenParticleGUI.manager.getEndColor().a = (Integer.parseInt(endAlphaSpinner.getValue().toString()) / 255f);
        regenCode();
      }
    });

    appPanel.add(colorPanel,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 0, 0));
    colorPanel.add(startColorLabel,             new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
    colorPanel.add(colorLabel,        new GridBagConstraints(2, 0, 1, 3, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
    colorPanel.add(endColorLabel,       new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 10, 0, 10), 0, 0));
    colorPanel.add(startColorPanel,        new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 25, 25));
    colorPanel.add(endColorPanel,      new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 25, 25));
    colorPanel.add(startColorHex,      new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 4, 0), 0, 0));
    colorPanel.add(endColorHex,     new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 4, 0), 0, 0));
    colorPanel.add(startAlphaSpinner,        new GridBagConstraints(1, 3, 1, 1, 0.25, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 20, 0));
    colorPanel.add(startAlphaLabel,    new GridBagConstraints(0, 3, 1, 1, 0.25, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    colorPanel.add(endAlphaLabel,    new GridBagConstraints(3, 3, 1, 1, 0.25, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    colorPanel.add(endAlphaSpinner,    new GridBagConstraints(4, 3, 1, 1, 0.25, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 20, 0));
    appPanel.add(sizePanel,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 5, 10), 0, 0));
    sizePanel.add(startSizeLabel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
    sizePanel.add(startSizeSlider,         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 100, 0));
    sizePanel.add(endSizeLabel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 0, 0), 0, 0));
    sizePanel.add(endSizeSlider,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 0, 0), 100, 0));
    appPanel.add(texturePanel,   new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10), 0, 0));
    texturePanel.add(textureLabel,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 4), 0, 0));
    texturePanel.add(changeTextureButton,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 5, 4), 0, 0));
    texturePanel.add(imageLabel,       new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 32, 32));
    speedPanel.add(speedLabel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
    speedPanel.add(speedSlider,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    worldPanel.add(gravityPanel,          new GridBagConstraints(1, 0, 1, 3, 0.5, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 10), 0, 0));
    gravityPanel.add(gravXSlider,    new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    gravityPanel.add(gravYSlider,       new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    gravityPanel.add(gravZSlider,    new GridBagConstraints(2, 0, 1, 1, 0.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    gravityPanel.add(gravXLabel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    gravityPanel.add(gravYLabel,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    gravityPanel.add(gravZLabel,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    worldPanel.add(agePanel,      new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 5), 0, 0));
    agePanel.add(minAgeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    agePanel.add(minAgeSlider,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    worldPanel.add(randomPanel,  new GridBagConstraints(0, 2, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 10, 5), 0, 0));
    emitPanel.add(anglePanel,   new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 5, 10), 0, 0));
    anglePanel.add(angleLabel,     new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 5, 10), 0, 0));
    anglePanel.add(angleSlider,   new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0));
    anglePanel.add(angleLabel, null);
    randomPanel.add(randomLabel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
    randomPanel.add(randomSlider,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
    mainTabbedPane1.add(examplesPanel,   "Examples");
    examplesPanel.add(examplesLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 10), 0, 0));
        examplesPanel.add(exampleSP,     new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
    examplesPanel.add(exampleButton,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 10, 10), 0, 0));
    exampleSP.setViewportView(exampleList);
    mainTabbedPane1.add(codePanel,  "Code");
    codePanel.add(codeLabel,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 5, 10), 0, 0));
    codePanel.add(codeSP,   new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 10, 10, 10), 0, 0));
    codeSP.setViewportView(codeTextArea);

    setSize(new Dimension(450, 320));
  }

  /**
   * applyExample
   */
  private void applyExample() {
    if (exampleList == null || exampleList.getSelectedValue() == null) return;
    String examType = exampleList.getSelectedValue().toString();
    RenParticleManager manager = TestRenParticleGUI.manager;
    if ("FIRE".equalsIgnoreCase(examType)) {
      manager.setStartColor(new ColorRGBA(1f, .312f, .121f, 1));
      manager.setEndColor(new ColorRGBA(1f, .312f, .121f, 0));
      manager.setStartSize(40f);
      manager.setEndSize(40f);
      manager.setEmissionMaximumAngle(.174f);
      manager.setRandomMod(5.0f);
      manager.setParticlesSpeed(2.96f);
      manager.setParticlesMinimumLifeTime(800f);
      manager.setGravityForce(new Vector3f(0f,0f,0f));
    } else if ("FOUNTAIN".equalsIgnoreCase(examType)) {
      manager.setStartColor(new ColorRGBA(0f, .0625f, 1f, 1));
      manager.setEndColor(new ColorRGBA(0f, .0625f, 1f, 0));
      manager.setStartSize(10f);
      manager.setEndSize(10f);
      manager.setEmissionMaximumAngle(.244f);
      manager.setRandomMod(1.0f);
      manager.setParticlesSpeed(7.5f);
      manager.setParticlesMinimumLifeTime(2000f);
      manager.setGravityForce(new Vector3f(0f,-4e-3f,0f));
    } else if ("LAVA".equalsIgnoreCase(examType)) {
      manager.setStartColor(new ColorRGBA(1f, .18f, 0.125f, 1));
      manager.setEndColor(new ColorRGBA(1f, .18f, 0.125f, 0));
      manager.setStartSize(40f);
      manager.setEndSize(40f);
      manager.setEmissionMaximumAngle(.418f);
      manager.setRandomMod(1.0f);
      manager.setParticlesSpeed(3.0f);
      manager.setParticlesMinimumLifeTime(2400f);
      manager.setGravityForce(new Vector3f(0f,-4e-3f,0f));
    } else if ("SMOKE".equalsIgnoreCase(examType)) {
      manager.setStartColor(new ColorRGBA(.375f, .375f, .375f, 1));
      manager.setEndColor(new ColorRGBA(.375f, .375f, .375f, 0));
      manager.setStartSize(20f);
      manager.setEndSize(40f);
      manager.setEmissionMaximumAngle(.331f);
      manager.setRandomMod(2.6f);
      manager.setParticlesSpeed(.9f);
      manager.setParticlesMinimumLifeTime(4000f);
      manager.setGravityForce(new Vector3f(0f,0f,0f));
    }
    manager.setEmissionDirection(new Vector3f(0f,1f,0f));
    manager.warmup();
    updateFromManager();
  }

  /**
   * updateExampleModel
   */
  private void updateExampleModel() {
    exampleModel.addElement("Fire");
    exampleModel.addElement("Fountain");
    exampleModel.addElement("Lava");
    exampleModel.addElement("Smoke");
//    exampleModel.addElement("Rain");
//    exampleModel.addElement("Snow");
  }

  /**
   * updateFromManager
   */
  public void updateFromManager() {
    startColorPanel.setBackground(makeColor(TestRenParticleGUI.manager.getStartColor(), false));
    endColorPanel.setBackground(makeColor(TestRenParticleGUI.manager.getEndColor(), false));
    startAlphaSpinner.setValue(new Integer(makeColor(TestRenParticleGUI.manager.getStartColor(), true).getAlpha()));
    endAlphaSpinner.setValue(new Integer(makeColor(TestRenParticleGUI.manager.getEndColor(), true).getAlpha()));
    updateColorLabels();
    startSizeSlider.setValue((int)(TestRenParticleGUI.manager.getStartSize() * 10));
    endSizeSlider.setValue((int)(TestRenParticleGUI.manager.getEndSize() * 10));
    updateSizeLabels();
    minAgeSlider.setValue((int)(TestRenParticleGUI.manager.getParticlesMinimumLifeTime()));
    updateAgeLabels();
    speedSlider.setValue((int)(TestRenParticleGUI.manager.getParticlesSpeed() * 10));
    updateSpeedLabels();
    gravXSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().x * 1000));
    gravYSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().y * 1000));
    gravZSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().z * 1000));
    emitXSlider.setValue((int)(TestRenParticleGUI.manager.getEmissionDirection().x * 10));
    emitYSlider.setValue((int)(TestRenParticleGUI.manager.getEmissionDirection().y * 10));
    emitZSlider.setValue((int)(TestRenParticleGUI.manager.getEmissionDirection().z * 10));
    angleSlider.setValue((int)(TestRenParticleGUI.manager.getEmissionMaximumAngle() * FastMath.RAD_TO_DEG));
    updateAngleLabels();
    randomSlider.setValue((int)(TestRenParticleGUI.manager.getRandomMod() * 10));
    updateRandomLabels();
    regenCode();
    validate();
  }

  /**
   * updateRandomLabels
   */
  private void updateRandomLabels() {
    int val = randomSlider.getValue();
    randomLabel.setText("Random Factor: "+val/10f);
  }

  /**
   * updateAngleLabels
   */
  private void updateAngleLabels() {
    int val = angleSlider.getValue();
    angleLabel.setText("Degrees Off Direction: "+val);
  }

  /**
   * updateSpeedLabels
   */
  private void updateSpeedLabels() {
    int val = speedSlider.getValue();
    speedLabel.setText("Speed Mod: "+val*10+"%");
  }

  /**
   * updateAgeLabels
   */
  private void updateAgeLabels() {
    int val = minAgeSlider.getValue();
    minAgeLabel.setText("Minimum Age: "+val+"ms");
  }

  /**
   * updateSizeLabels
   */
  private void updateSizeLabels() {
    int val = endSizeSlider.getValue();
    endSizeLabel.setText("End Size: "+val/10f);
    val = startSizeSlider.getValue();
    startSizeLabel.setText("Start Size: "+val/10f);
  }

  private String convColorToHex(Color c) {
      if (c == null) return null;
      String sRed = Integer.toHexString( c.getRed() );
      if (sRed.length() == 1) sRed = "0"+sRed;
      String sGreen = Integer.toHexString( c.getGreen() );
      if (sGreen.length() == 1) sGreen = "0"+sGreen;
      String sBlue = Integer.toHexString( c.getBlue() );
      if (sBlue.length() == 1) sBlue = "0"+sBlue;
      return "#"+sRed+sGreen+sBlue;
  }

  /**
   * updateColorLabels
   */
  private void updateColorLabels() {
    startColorHex.setText(convColorToHex(startColorPanel.getBackground()));
    endColorHex.setText(convColorToHex(endColorPanel.getBackground()));
  }

  private Color makeColor(ColorRGBA rgba, boolean useAlpha) {
    return new Color(rgba.r, rgba.g, rgba.b, (useAlpha ? rgba.a : 1f));
  }

  private ColorRGBA makeColorRGBA(Color color) {
    return new ColorRGBA(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f);
  }

  private void startColorPanel_mouseClicked(MouseEvent e) {
    TestRenParticleGUI.noUpdate = true;
    Color color = JColorChooser.showDialog(this, "Choose new start color:", startColorPanel.getBackground());
    if (color == null) return;
    ColorRGBA rgba = makeColorRGBA(color);
    rgba.a = (Integer.parseInt(startAlphaSpinner.getValue().toString()) / 255f);
    TestRenParticleGUI.manager.setStartColor(rgba);
    startColorPanel.setBackground(color);
    regenCode();
    updateColorLabels();
    TestRenParticleGUI.noUpdate = false;
  }

  private void endColorPanel_mouseClicked(MouseEvent e) {
    TestRenParticleGUI.noUpdate = true;
    Color color = JColorChooser.showDialog(this, "Choose new end color:", endColorPanel.getBackground());
    if (color == null) return;
    ColorRGBA rgba = makeColorRGBA(color);
    rgba.a = (Integer.parseInt(endAlphaSpinner.getValue().toString()) / 255f);
    TestRenParticleGUI.manager.setEndColor(rgba);
    endColorPanel.setBackground(color);
    regenCode();
    updateColorLabels();
    TestRenParticleGUI.noUpdate = false;
  }

  private void regenCode() {
    StringBuffer code = new StringBuffer();
    if (TestRenParticleGUI.manager == null) {
      codeTextArea.setText("");
      return;
    }
    code.append("RenParticleManager manager = new RenParticleManager("+TestRenParticleGUI.manager.getParticlesNumber()+", display.getRenderer().getCamera());\n");
    code.append("manager.setGravityForce("+codeString(TestRenParticleGUI.manager.getGravityForce())+");\n");
    code.append("manager.setEmissionDirection("+codeString(TestRenParticleGUI.manager.getEmissionDirection())+");\n");
    code.append("manager.setEmissionMaximumAngle("+TestRenParticleGUI.manager.getEmissionMaximumAngle()+"f);\n");
    code.append("manager.setParticlesSpeed("+TestRenParticleGUI.manager.getParticlesSpeed()+"f);\n");
    code.append("manager.setParticlesMinimumLifeTime("+TestRenParticleGUI.manager.getParticlesMinimumLifeTime()+"f);\n");
    code.append("manager.setStartSize("+TestRenParticleGUI.manager.getStartSize()+"f);\n");
    code.append("manager.setEndSize("+TestRenParticleGUI.manager.getEndSize()+"f);\n");
    code.append("manager.setStartColor("+codeString(TestRenParticleGUI.manager.getStartColor())+");\n");
    code.append("manager.setEndColor("+codeString(TestRenParticleGUI.manager.getEndColor())+");\n");
    code.append("manager.setRandomMod("+TestRenParticleGUI.manager.getRandomMod()+"f);\n");
    code.append("\n");
    code.append("Node myNode = new Node(\"Particle Nodes\")\n");
    code.append("myNode.setRenderState(YOUR TEXTURE STATE);\n");
    code.append("myNode.setRenderState(YOUR ALPHA STATE);\n");
    code.append("myNode.attachChild(manager.getParticles());\n");
    codeTextArea.setText(code.toString());
    codeTextArea.setCaretPosition(0);
  }

  private String codeString(ColorRGBA rgba) {
    StringBuffer code = new StringBuffer("new ColorRGBA(");
    code.append(rgba.r+"f, ");
    code.append(rgba.g+"f, ");
    code.append(rgba.b+"f, ");
    code.append(rgba.a+"f");
    code.append(")");
    return code.toString();
  }

  private String codeString(Vector3f vect) {
    StringBuffer code = new StringBuffer("new Vector3f(");
    code.append(vect.x+"f, ");
    code.append(vect.y+"f, ");
    code.append(vect.z+"f");
    code.append(")");
    return code.toString();
  }
}
