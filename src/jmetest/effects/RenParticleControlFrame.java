package jmetest.effects;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.jme.renderer.ColorRGBA;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

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

  public RenParticleControlFrame() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    colorBorder = new TitledBorder("");
    sizeBorder = new TitledBorder("");
    ageBorder = new TitledBorder(" PARTICLE AGE ");
    speedBorder = new TitledBorder(" PARTICLE SPEED ");
    textureBorder = new TitledBorder(" PARTICLE TEXTURE ");
    gravityBorder = new TitledBorder(" GRAVITY ");
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
    speedSlider.setMaximum(500);
    speedSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = speedSlider.getValue();
        TestRenParticleGUI.manager.setParticlesSpeed((float)val/1000f);
        updateSpeedLabels();
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
    gravXSlider.setMajorTickSpacing(20);
    gravXSlider.setMaximum(100);
    gravXSlider.setMinimum(-100);
    gravXSlider.setMinorTickSpacing(5);
    gravXSlider.setPaintLabels(true);
    gravXSlider.setPaintTicks(true);
    gravXSlider.setPaintTrack(true);
    gravXSlider.setValue(0);
    gravXSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravXSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().x = (float)val * 0.01f;
      }
    });

    gravYLabel.setText("Y");
    gravYSlider.setOrientation(JSlider.VERTICAL);
    gravYSlider.setInverted(false);
    gravYSlider.setMajorTickSpacing(20);
    gravYSlider.setMaximum(100);
    gravYSlider.setMinimum(-100);
    gravYSlider.setMinorTickSpacing(5);
    gravYSlider.setPaintLabels(true);
    gravYSlider.setPaintTicks(true);
    gravYSlider.setPaintTrack(true);
    gravYSlider.setValue(0);
    gravYSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravYSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().y = (float)val * 0.01f;
      }
    });

    gravZLabel.setText("Z");
    gravZSlider.setOrientation(JSlider.VERTICAL);
    gravZSlider.setInverted(false);
    gravZSlider.setMajorTickSpacing(20);
    gravZSlider.setMaximum(100);
    gravZSlider.setMinimum(-100);
    gravZSlider.setMinorTickSpacing(5);
    gravZSlider.setPaintLabels(true);
    gravZSlider.setPaintTicks(true);
    gravZSlider.setPaintTrack(true);
    gravZSlider.setValue(0);
    gravZSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = gravZSlider.getValue();
        if (TestRenParticleGUI.manager != null)
          TestRenParticleGUI.manager.getGravityForce().z = (float)val * 0.01f;
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
      }
    });

    this.getContentPane().add(mainTabbedPane1, BorderLayout.CENTER);
    mainTabbedPane1.add(appPanel,   "Appearance");
    mainTabbedPane1.add(emitPanel,     "Emission");
    mainTabbedPane1.add(worldPanel,    "World");

    worldPanel.add(speedPanel,       new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 5), 0, 0));

    startAlphaSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        TestRenParticleGUI.manager.getStartColor().a = (Integer.parseInt(startAlphaSpinner.getValue().toString()) / 255f);
      }
    });

    endAlphaSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        TestRenParticleGUI.manager.getEndColor().a = (Integer.parseInt(endAlphaSpinner.getValue().toString()) / 255f);
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
    speedPanel.add(speedSlider,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
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
    worldPanel.add(agePanel,     new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 10, 5), 0, 0));
    agePanel.add(minAgeLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 0, 0));
    agePanel.add(minAgeSlider, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

    setSize(new Dimension(450, 320));
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
    speedSlider.setValue((int)(TestRenParticleGUI.manager.getParticlesSpeed() * 1000));
    updateSpeedLabels();
    gravXSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().x * 100));
    gravYSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().y * 100));
    gravZSlider.setValue((int)(TestRenParticleGUI.manager.getGravityForce().z * 100));
    validate();
  }

  /**
   * updateSpeedLabels
   */
  private void updateSpeedLabels() {
    int val = speedSlider.getValue();
    speedLabel.setText("Speed Mod: "+val+"%");
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

  void startColorPanel_mouseClicked(MouseEvent e) {
    TestRenParticleGUI.noUpdate = true;
    Color color = JColorChooser.showDialog(this, "Choose new start color:", startColorPanel.getBackground());
    if (color == null) return;
    ColorRGBA rgba = makeColorRGBA(color);
    rgba.a = (Integer.parseInt(startAlphaSpinner.getValue().toString()) / 255f);
    TestRenParticleGUI.manager.setStartColor(rgba);
    startColorPanel.setBackground(color);
    updateColorLabels();
    TestRenParticleGUI.noUpdate = false;
  }

  void endColorPanel_mouseClicked(MouseEvent e) {
    TestRenParticleGUI.noUpdate = true;
    Color color = JColorChooser.showDialog(this, "Choose new end color:", endColorPanel.getBackground());
    if (color == null) return;
    ColorRGBA rgba = makeColorRGBA(color);
    rgba.a = (Integer.parseInt(endAlphaSpinner.getValue().toString()) / 255f);
    TestRenParticleGUI.manager.setEndColor(rgba);
    endColorPanel.setBackground(color);
    updateColorLabels();
    TestRenParticleGUI.noUpdate = false;
  }
  void minAgeSlider_stateChanged(ChangeEvent e) {

  }

}
