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

package jmetest.effects;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Quaternion;
import com.jme.math.Rectangle;
import com.jme.math.Ring;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.RenderThreadActionQueue;
import com.jme.util.RenderThreadExecutable;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryExporter;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.awt.JMECanvas;
import com.jmex.awt.SimpleCanvasImpl;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleGeometry;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.ParticleLines;
import com.jmex.effects.particles.ParticlePoints;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

/**
 * <code>RenParticleControlFrame</code>
 *
 * @author Joshua Slack
 * @author Andrzej Kapolka - additions for multiple layers, save/load from jme format
 * @version $Id: RenParticleEditor.java,v 1.34 2006-07-05 13:21:45 renanse Exp $
 *
 */

public class RenParticleEditor extends JFrame {

    int width = 640, height = 480;
    public static Node particleNode;
    public static ParticleGeometry particleGeom;
    public static File newTexture = null;

    private static final long serialVersionUID = 1L;    
    private static final String[] EXAMPLE_NAMES = { "Fire", "Fountain",
        "Lava", "Smoke", "Jet", "Snow", "Rain", "Explosion", "Ground Fog" };
        
    MyImplementor impl;
    private CameraHandler camhand;
    private Canvas glCanvas;
    private Node root;
    private Geometry grid;
    
    // layer panel components
    LayerTableModel layerModel = new LayerTableModel();
    JTable layerTable = new JTable(layerModel);
    JButton deleteLayerButton;
    
    // appearance panel components
    JLabel countLabel;
    JComboBox geomTypeBox;
    JPanel startColorPanel = new JPanel();
    JPanel endColorPanel = new JPanel();
    JLabel startColorHex = new JLabel();
    JLabel endColorHex = new JLabel();
    JSpinner startAlphaSpinner = new JSpinner(
        new SpinnerNumberModel(255, 0, 255, 1));
    JSpinner endAlphaSpinner = new JSpinner(
        new SpinnerNumberModel(0, 0, 255, 1));
    JCheckBox additiveBlendingBox;
    ValuePanel startSizePanel =
        new ValuePanel("Start Size: ", "", 0f, Float.MAX_VALUE, 1f);
    ValuePanel endSizePanel =
        new ValuePanel("End Size: ", "", 0f, Float.MAX_VALUE, 1f);
    JLabel imageLabel = new JLabel();

    // origin panel components
    VectorPanel translationPanel =
        new VectorPanel(-Float.MAX_VALUE, Float.MAX_VALUE, 1f);
    VectorPanel rotationPanel =
        new VectorPanel(-180f, 180f, 1f);
    ValuePanel scalePanel =
        new ValuePanel("System Scale: ", "", 0f, Float.MAX_VALUE, 0.01f);
    JComboBox originTypeBox;
    JPanel originParamsPanel;
    JPanel pointParamsPanel;
    JPanel lineParamsPanel;
    ValuePanel lineLengthPanel =
        new ValuePanel("Length: ", "", 0f, Float.MAX_VALUE, 1f);
    JPanel rectParamsPanel;
    ValuePanel rectWidthPanel =
        new ValuePanel("Width: ", "", 0f, Float.MAX_VALUE, 1f);
    ValuePanel rectHeightPanel =
        new ValuePanel("Height: ", "", 0f, Float.MAX_VALUE, 1f);
    JPanel ringParamsPanel;
    ValuePanel ringInnerPanel =
        new ValuePanel("Inner Radius: ", "", 0f, Float.MAX_VALUE, 1f);
    ValuePanel ringOuterPanel =
        new ValuePanel("Outer Radius: ", "", 0f, Float.MAX_VALUE, 1f);
    
    // emission panel components
    JCheckBox rotateWithEmitterBox;
    UnitVectorPanel directionPanel = new UnitVectorPanel();
    ValuePanel minAnglePanel =
        new ValuePanel("Min Degrees Off Dir.: ", "", 0f, 180f, 1f);
    ValuePanel maxAnglePanel =
        new ValuePanel("Max Degrees Off Dir.: ", "", 0f, 180f, 1f);
    ValuePanel velocityPanel =
        new ValuePanel("Initial Velocity: ", "", 0f, Float.MAX_VALUE, 0.1f);
    ValuePanel spinPanel = new ValuePanel("Spin Speed: ", "",
        -Float.MAX_VALUE, Float.MAX_VALUE, 0.1f);
    
    // flow panel components
    JCheckBox rateBox;
    ValuePanel releaseRatePanel =
        new ValuePanel("Particles per second: ", "", 0, Integer.MAX_VALUE, 1);
    ValuePanel rateVarPanel = new ValuePanel("Variance: ", "%", 0f, 1f, 0.01f);
    JCheckBox spawnBox;
    Action spawnAction;
    
    // world panel components
    ValuePanel speedPanel =
        new ValuePanel("Speed Mod: ", "x", 0f, Float.MAX_VALUE, 0.01f);
    ValuePanel massPanel =
        new ValuePanel("Particle Mass: ", "", 0f, Float.MAX_VALUE, 0.1f);
    ValuePanel minAgePanel =
        new ValuePanel("Minimum Age: ", "ms", 0f, Float.MAX_VALUE, 10f);
    ValuePanel maxAgePanel =
        new ValuePanel("Maximum Age: ", "ms", 0f, Float.MAX_VALUE, 10f);    
    ValuePanel randomPanel =
        new ValuePanel("Random Factor: ", "", 0f, Float.MAX_VALUE, 0.1f);
        
    // influence panel components
    InfluenceListModel influenceModel = new InfluenceListModel();
    JList influenceList = new JList(influenceModel);
    JButton deleteInfluenceButton;
    JPanel influenceParamsPanel;
    JPanel windParamsPanel;
    ValuePanel windStrengthPanel =
        new ValuePanel("Strength: ", "", 0f, 100f, 0.1f);
    UnitVectorPanel windDirectionPanel = new UnitVectorPanel();
    JCheckBox windRandomBox;
    JPanel gravityParamsPanel;
    VectorPanel gravityInfluencePanel =
        new VectorPanel(-Float.MAX_VALUE, Float.MAX_VALUE, 0.1f);
    JPanel dragParamsPanel;
    ValuePanel dragCoefficientPanel =
        new ValuePanel("Drag Coefficient: ", "", 0f, Float.MAX_VALUE, 0.1f);
    JPanel vortexParamsPanel;
    ValuePanel vortexStrengthPanel =
        new ValuePanel("Strength: ", "", 0f, Float.MAX_VALUE, 0.1f);
    ValuePanel vortexDivergencePanel =
        new ValuePanel("Divergence: ", "", -90f, 90f, 1f);
    UnitVectorPanel vortexDirectionPanel = new UnitVectorPanel();
    JCheckBox vortexRandomBox;
    
    // examples panel components
    JList exampleList;
    JButton exampleButton;
    
    JFrame colorChooserFrame = new JFrame("Choose a color.");
    JColorChooser colorChooser = new JColorChooser();
    boolean colorstart = false;
  
    JFileChooser fileChooser = new JFileChooser(),
        textureChooser = new JFileChooser();
    File openFile;
    
    Preferences prefs = Preferences.userNodeForPackage(RenParticleEditor.class);
    
    /**
     * Main Entry point...
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new RenParticleEditor();
    }

  
    public RenParticleEditor() {
        try {
            init();
            // center the frame
            setLocationRelativeTo(null);

            // show frame
            setVisible(true);

            // init some location dependant sub frames
            initColorChooser();
            initFileChooser();
            initTextureChooser();
            
            while (glCanvas == null) ;

            // MAKE SURE YOU REPAINT SOMEHOW OR YOU WON'T SEE THE UPDATES...
            new Thread() {
                { setDaemon(true); }
                public void run() {
                    while (true) {
                        glCanvas.repaint();
                        try {
                            sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws Exception {
        updateTitle();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFont(new Font("Arial", 0, 12));
        
        setJMenuBar(createMenuBar());
        
        JTabbedPane tabbedPane = new JTabbedPane();                
        tabbedPane.add(createLayerPanel(), "Layers");
        tabbedPane.add(createAppearancePanel(), "Appearance");
        tabbedPane.add(createOriginPanel(), "Origin");
        tabbedPane.add(createEmissionPanel(), "Emission");
        tabbedPane.add(createFlowPanel(), "Flow");
        tabbedPane.add(createWorldPanel(), "World");
        tabbedPane.add(createInfluencePanel(), "Influences");
        tabbedPane.add(createExamplesPanel(), "Examples");
        tabbedPane.setPreferredSize(new Dimension(300, 10));
        
        getContentPane().add(tabbedPane, BorderLayout.WEST);
        getContentPane().add(getGlCanvas(), BorderLayout.CENTER);
        
        setSize(new Dimension(1024, 768));
    }
    
    private void updateTitle() {
        setTitle("Particle System Editor" +
            (openFile == null ? "" : (" - " + openFile)));
    }
    
    private JMenuBar createMenuBar() {
        Action newAction = new AbstractAction("New") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                createNewSystem();
            }
        };
        newAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);

        Action open = new AbstractAction("Open...") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                showOpenDialog();
            }
        };
        open.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);

        Action importAction = new AbstractAction("Import Layers...") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                showImportDialog();
            }
        };
        importAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
        
        AbstractAction save = new AbstractAction("Save") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                saveAs(openFile);
            }
        };
        save.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
        
        Action saveAs = new AbstractAction("Save As...") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                saveAs(null);
            }
        };
        saveAs.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);

        Action quit = new AbstractAction("Quit") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        quit.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
        
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(newAction);
        file.add(open);
        file.add(importAction);
        file.add(save);
        file.add(saveAs);
        file.addSeparator();
        file.add(quit);
        
        spawnAction = new AbstractAction("Force Spawn") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                for (Spatial child : particleNode.getChildren()) {
                    if (child instanceof ParticleGeometry) {
                        ((ParticleGeometry)child).forceRespawn();
                    }
                }
            }
        };
        spawnAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
        spawnAction.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
        
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        edit.add(spawnAction);
        
        Action showGrid = new AbstractAction("Show Grid") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                grid.setCullMode(grid.getCullMode() == SceneElement.CULL_ALWAYS ?
                    SceneElement.CULL_DYNAMIC : SceneElement.CULL_ALWAYS);
            }
        };
        showGrid.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
        
        Action changeBackground = new AbstractAction("Change Background Color...") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                showBackgroundDialog();
            }
        };
        changeBackground.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
        
        Action recenter = new AbstractAction("Recenter Camera") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                camhand.recenterCamera();
            }
        };
        recenter.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
        
        JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        JCheckBoxMenuItem sgitem = new JCheckBoxMenuItem(showGrid);
        sgitem.setSelected(true);
        view.add(sgitem);
        view.add(changeBackground);
        view.addSeparator();
        view.add(recenter);
        
        JMenuBar mbar = new JMenuBar();
        mbar.add(file);
        mbar.add(edit);
        mbar.add(view);
        return mbar;
    }
    
    private JPanel createLayerPanel() {
        JLabel layerLabel = createBoldLabel("Particle Layers:");
        
        layerTable.setColumnSelectionAllowed(false);
        layerTable.setRowSelectionAllowed(true);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int vwidth = layerTable.getTableHeader().getDefaultRenderer().
            getTableCellRendererComponent(layerTable, "Visible", false, false,
                -1, 1).getMinimumSize().width;
        TableColumn vcol = layerTable.getColumnModel().getColumn(1);
        vcol.setMinWidth(vwidth);
        vcol.setPreferredWidth(vwidth);
        vcol.setMaxWidth(vwidth);
        layerTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (layerTable.getSelectedRow() != -1) {
                    particleGeom = (ParticleGeometry)particleNode.getChild(
                        layerTable.getSelectedRow());
                    updateFromManager();
                }
            }
        });
        
        JButton newLayerButton = new JButton(new AbstractAction("New") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                int idx = particleNode.getQuantity();
                createNewLayer();
                layerModel.fireTableRowsInserted(idx, idx);
                layerTable.setRowSelectionInterval(idx, idx);
                deleteLayerButton.setEnabled(true);
            }
        });
        newLayerButton.setMargin(new Insets(2, 14, 2, 14));
        
        deleteLayerButton = new JButton(new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                deleteLayer();
            }
        });
        deleteLayerButton.setMargin(new Insets(2, 14, 2, 14));
        deleteLayerButton.setEnabled(false);
        
        JPanel layerPanel = new JPanel(new GridBagLayout());
        layerPanel.add(layerLabel, new GridBagConstraints(0, 0, 2, 1,
            1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(10, 10, 5, 10), 0, 0));
        layerPanel.add(new JScrollPane(layerTable), new GridBagConstraints(0,
            1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        layerPanel.add(newLayerButton, new GridBagConstraints(0, 2, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 10, 10, 10), 0, 0));
        layerPanel.add(deleteLayerButton, new GridBagConstraints(1, 2, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 10, 10, 10), 0, 0));
        return layerPanel;
    }
    
    private JPanel createAppearancePanel() {
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
            GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 10, 5, 10), 0, 0));
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
        
        JPanel geomPanel = new JPanel(new GridBagLayout());
        geomPanel.setBorder(createTitledBorder("PARTICLE GEOMETRY"));
        geomPanel.add(createBoldLabel("Type:"), new GridBagConstraints(0, 0,
            1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        geomPanel.add(geomTypeBox, new GridBagConstraints(1, 0, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));
        
        JLabel startColorLabel = createBoldLabel("Starting Color:"),
            colorLabel = createBoldLabel(">>"),
            endColorLabel = createBoldLabel("End Color:"),
            startAlphaLabel = new JLabel("alpha:"),
            endAlphaLabel = new JLabel("alpha:");
        startColorHex.setFont(new Font("Arial", Font.PLAIN, 10));
        startColorHex.setText("#FFFFFF");
        endColorHex.setFont(new Font("Arial", Font.PLAIN, 10));
        endColorHex.setText("#FFFFFF");
        
        startColorPanel.setBackground(Color.white);
        startColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        startColorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                startColorPanel_mouseClicked(e);
            }
        });
        endColorPanel.setBackground(Color.white);
        endColorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        endColorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                endColorPanel_mouseClicked(e);
            }
        });
        
        startAlphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.getStartColor().a =
                    ((Number)startAlphaSpinner.getValue()).intValue() / 255f;
            }
        });
        endAlphaSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.getEndColor().a =
                    ((Number)endAlphaSpinner.getValue()).intValue() / 255f;
            }
        });
        
        additiveBlendingBox = new JCheckBox(
            new AbstractAction("Additive Blending") {
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
                0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
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
                particleGeom.setStartSize(startSizePanel.getFloatValue());
            }
        });        
        endSizePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setEndSize(endSizePanel.getFloatValue());
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
        JButton changeTextureButton = new JButton(
            new AbstractAction("Browse...") {
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
                ((TextureState)particleGeom.getRenderState(
                    RenderState.RS_TEXTURE)).setTexture(null);
                imageLabel.setIcon(null);
            }
        });
        clearTextureButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearTextureButton.setMargin(new Insets(2, 2, 2, 2));
        
        imageLabel.setBackground(Color.lightGray);
        imageLabel.setMaximumSize(new Dimension(128, 128));
        imageLabel.setMinimumSize(new Dimension(0, 0));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(false);
        
        JPanel texturePanel = new JPanel(new GridBagLayout());
        texturePanel.setBorder(createTitledBorder("PARTICLE TEXTURE"));
        texturePanel.add(textureLabel, new GridBagConstraints(0, 0, 2, 1, 0.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(changeTextureButton, new GridBagConstraints(0, 1, 1,
            1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(clearTextureButton, new GridBagConstraints(1, 1, 1,
            1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        texturePanel.add(imageLabel, new GridBagConstraints(2, 0, 1, 2, 1.0,
            1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
                
        JPanel appPanel = new JPanel(new GridBagLayout());
        appPanel.add(countPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 10), 0, 0));
        appPanel.add(geomPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 10), 0, 0));
        appPanel.add(colorPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 5, 5), 0, 0));
        appPanel.add(sizePanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 5, 5, 10), 0, 0));
        appPanel.add(texturePanel, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 5), 0, 0));
        return appPanel;
    }
    
    private JPanel createOriginPanel() {
        JPanel transformPanel = new JPanel(new GridBagLayout());
        transformPanel.setBorder(createTitledBorder(" EMITTER TRANSFORM "));
        
        translationPanel.setBorder(createTitledBorder(" TRANSLATION "));
        translationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.getLocalTranslation().set(
                    translationPanel.getValue());
            }
        });
        
        rotationPanel.setBorder(createTitledBorder(" ROTATION "));
        rotationPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Vector3f val = rotationPanel.getValue().multLocal(
                    FastMath.DEG_TO_RAD);
                particleGeom.getLocalRotation().fromAngles(val.x, val.y,
                    val.z);
            }
        });
        
        scalePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setLocalScale(scalePanel.getFloatValue());
            }
        });
        
        transformPanel.add(translationPanel, new GridBagConstraints(0, 0, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        transformPanel.add(rotationPanel, new GridBagConstraints(1, 0, 1, 1,
            0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
        transformPanel.add(scalePanel, new GridBagConstraints(0, 1, 2, 1,
            1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
        
        originTypeBox = new JComboBox(new String[] {
            "Point", "Line", "Rectangle", "Ring" });
        originTypeBox.setBorder(createTitledBorder(" EMITTER TYPE "));
        originTypeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOriginParams();
            }
        });
        
        originParamsPanel = new JPanel(new BorderLayout());
        
        pointParamsPanel = createPointParamsPanel();
        lineParamsPanel = createLineParamsPanel();
        rectParamsPanel = createRectParamsPanel();
        ringParamsPanel = createRingParamsPanel();
        
        JPanel originPanel = new JPanel(new GridBagLayout());
        originPanel.add(transformPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 10, 10), 0, 0));
        originPanel.add(originTypeBox, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 10, 10), 0, 0));
        originPanel.add(originParamsPanel, new GridBagConstraints(0, 2, 1, 1,
            1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 5), 0, 0));
        return originPanel;
    }

    private JPanel createPointParamsPanel() {
        return new JPanel();
    }
    
    private JPanel createLineParamsPanel() {
        lineLengthPanel.setBorder(createTitledBorder(" LINE PARAMETERS "));
        lineLengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Line line = particleGeom.getLine();
                float val = lineLengthPanel.getFloatValue();
                line.getOrigin().set(-val/2, 0f, 0f);
                line.getDirection().set(val/2, 0f, 0f);
            }
        });
        return lineLengthPanel;
    }
    
    private JPanel createRectParamsPanel() {
        rectWidthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Rectangle rect = particleGeom.getRectangle();
                float width = rectWidthPanel.getFloatValue();
                rect.getA().x = -width/2;
                rect.getB().x = width/2;
                rect.getC().x = -width/2;
            }
        });
        rectHeightPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Rectangle rect = particleGeom.getRectangle();
                float height = rectHeightPanel.getFloatValue();
                rect.getA().z = -height/2;
                rect.getB().z = -height/2;
                rect.getC().z = height/2;
            }
        });
       
        JPanel rectParamsPanel = new JPanel(new GridBagLayout());
        rectParamsPanel.setBorder(createTitledBorder(" RECTANGLE PARAMETERS "));
        rectParamsPanel.add(rectWidthPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        rectParamsPanel.add(rectHeightPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        return rectParamsPanel;
    }
    
    private JPanel createRingParamsPanel() {
        ringInnerPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Ring ring = particleGeom.getRing();
                ring.setInnerRadius(ringInnerPanel.getFloatValue());
            }
        });
        ringOuterPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Ring ring = particleGeom.getRing();
                ring.setOuterRadius(ringOuterPanel.getFloatValue());
            }
        }); 
        
        JPanel ringParamsPanel = new JPanel(new GridBagLayout());
        ringParamsPanel.setBorder(createTitledBorder(" RING PARAMETERS "));
        ringParamsPanel.add(ringInnerPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        ringParamsPanel.add(ringOuterPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        return ringParamsPanel;
    }
    
    private JPanel createEmissionPanel() {
        rotateWithEmitterBox = new JCheckBox(
            new AbstractAction("Rotate With Emitter") {
            public void actionPerformed(ActionEvent e) {
                particleGeom.setRotateWithScene(rotateWithEmitterBox.isSelected());
            }
        });
        rotateWithEmitterBox.setFont(new Font("Arial", Font.BOLD, 12));
        
        directionPanel.setBorder(createTitledBorder("DIRECTION"));
        directionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (particleGeom != null) {
                    particleGeom.getEmissionDirection().set(
                        directionPanel.getValue());
                    particleGeom.updateRotationMatrix();
                }
            }
        });
        directionPanel.add(rotateWithEmitterBox, new GridBagConstraints(0, 2, 1, 1,
            1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        
        minAnglePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setMinimumAngle(
                    minAnglePanel.getFloatValue() * FastMath.DEG_TO_RAD);
            }
        });
        maxAnglePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setMaximumAngle(
                    maxAnglePanel.getFloatValue() * FastMath.DEG_TO_RAD);
            }
        });
        JPanel anglePanel = new JPanel(new GridBagLayout());
        anglePanel.setBorder(createTitledBorder("ANGLE"));
        anglePanel.add(minAnglePanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));
        anglePanel.add(maxAnglePanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 10), 0, 0));

        velocityPanel.setBorder(createTitledBorder("VELOCITY"));
        velocityPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setInitialVelocity(velocityPanel.getFloatValue());
            }
        });
        
        spinPanel.setBorder(createTitledBorder("PARTICLE SPIN"));
        spinPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setParticleSpinSpeed(spinPanel.getFloatValue());
            }
        });
        
        JPanel emitPanel = new JPanel(new GridBagLayout());
        emitPanel.add(directionPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 5, 5, 5), 0, 0));
        emitPanel.add(anglePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        emitPanel.add(velocityPanel, new GridBagConstraints(0, 2, 1, 1, 1.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        emitPanel.add(spinPanel, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        return emitPanel;
    }
    
    private JPanel createFlowPanel() {
        rateBox = new JCheckBox(new AbstractAction("Regulate Flow") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                particleGeom.getParticleController().setControlFlow(
                    rateBox.isSelected());
                updateRateLabels();
            }
        });
        
        releaseRatePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setReleaseRate(releaseRatePanel.getIntValue());
            }
        });
        
        rateVarPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setReleaseVariance(rateVarPanel.getFloatValue());
            }
        });

        JPanel ratePanel = new JPanel(new GridBagLayout());
        ratePanel.setBorder(createTitledBorder("RATE"));
        ratePanel.add(rateBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(10, 5, 5, 5), 0, 0));
        ratePanel.add(releaseRatePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        ratePanel.add(rateVarPanel, new GridBagConstraints(0, 2, 1, 1, 1.0,
            0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        
        spawnBox = new JCheckBox(
            new AbstractAction("Respawn 'dead' particles.") {
                private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                if (spawnBox.isSelected())
                    particleGeom.getParticleController().setRepeatType(
                        Controller.RT_WRAP);
                else
                    particleGeom.getParticleController().setRepeatType(
                        Controller.RT_CLAMP);
            }
        });
        spawnBox.setSelected(true);
        
        JButton spawnButton = new JButton(spawnAction);
        
        JPanel spawnPanel = new JPanel(new GridBagLayout());
        spawnPanel.setBorder(createTitledBorder("SPAWN"));
        spawnPanel.add(spawnBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(10, 10, 5, 10), 0, 0));
        spawnPanel.add(spawnButton, new GridBagConstraints(0, 1, 1, 1, 0.0,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 10, 10, 10), 0, 0));
        
        JPanel flowPanel = new JPanel(new GridBagLayout());
        flowPanel.add(ratePanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 5, 10), 0, 0));
        flowPanel.add(spawnPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
            GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 10, 10), 0, 0));
        return flowPanel;
    }
    
    private JPanel createWorldPanel() {
        speedPanel.setBorder(createTitledBorder("PARTICLE SPEED"));
        speedPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.getParticleController().setSpeed(
                    speedPanel.getFloatValue());
            }
        });
        
        massPanel.setBorder(createTitledBorder("PHYSICAL PROPERTIES"));
        massPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setParticleMass(massPanel.getFloatValue());
            }
        });
        
        minAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setMinimumLifeTime(minAgePanel.getFloatValue());
            }
        });
        maxAgePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setMaximumLifeTime(maxAgePanel.getFloatValue());
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
        
        randomPanel.setBorder(createTitledBorder("SYSTEM RANDOMNESS"));
        randomPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                particleGeom.setRandomMod(randomPanel.getFloatValue());
            }
        });
        
        JPanel worldPanel = new JPanel(new GridBagLayout());
        worldPanel.add(speedPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 5, 5), 0, 0));
        worldPanel.add(massPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(10, 10, 5, 5), 0, 0));
        worldPanel.add(agePanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 5, 5), 0, 0));
        worldPanel.add(randomPanel, new GridBagConstraints(0, 3, 1, 1, 1.0,
            1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
        return worldPanel;
    }
    
    private JPanel createInfluencePanel() {
        influenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        influenceList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int idx = influenceList.getSelectedIndex();
                deleteInfluenceButton.setEnabled(idx != -1);
                updateInfluenceParams();
            }
        });
        
        JButton newWindButton = new JButton(new AbstractAction("New Wind") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                particleGeom.addInfluence(
                    SimpleParticleInfluenceFactory.createBasicWind(
                        1f, new Vector3f(Vector3f.UNIT_X), true, true));
                int idx = particleGeom.getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newWindButton.setMargin(new Insets(2, 2, 2, 2));
        
        JButton newGravityButton = new JButton(new AbstractAction("New Gravity") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                particleGeom.addInfluence(
                    SimpleParticleInfluenceFactory.createBasicGravity(
                        new Vector3f(Vector3f.ZERO), true));
                int idx = particleGeom.getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newGravityButton.setMargin(new Insets(2, 2, 2, 2));

        JButton newDragButton = new JButton(new AbstractAction("New Drag") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                particleGeom.addInfluence(
                    SimpleParticleInfluenceFactory.createBasicDrag(1f));
                int idx = particleGeom.getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newDragButton.setMargin(new Insets(2, 2, 2, 2));
        
        JButton newVortexButton = new JButton(new AbstractAction("New Vortex") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                particleGeom.addInfluence(
                    SimpleParticleInfluenceFactory.createBasicVortex(
                        1f, 0f, new Line(new Vector3f(),
                            new Vector3f(Vector3f.UNIT_Y)), true, true));
                int idx = particleGeom.getInfluences().size() - 1;
                influenceModel.fireIntervalAdded(idx, idx);
                influenceList.setSelectedIndex(idx);
            }
        });
        newVortexButton.setMargin(new Insets(2, 2, 2, 2));
        
        deleteInfluenceButton = new JButton(new AbstractAction("Delete") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                int idx = influenceList.getSelectedIndex();
                particleGeom.getInfluences().remove(idx);
                influenceModel.fireIntervalRemoved(idx, idx);
                influenceList.setSelectedIndex(
                    idx >= particleGeom.getInfluences().size() ? idx - 1 : idx);
            }
        });
        deleteInfluenceButton.setMargin(new Insets(2, 2, 2, 2));
        deleteInfluenceButton.setEnabled(false);
        
        JPanel influenceListPanel = new JPanel(new GridBagLayout());
        influenceListPanel.setBorder(createTitledBorder("PARTICLE INFLUENCES"));
        influenceListPanel.add(influenceList, new GridBagConstraints(0, 0, 1, 3, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 10, 10, 5), 0, 0));
        influenceListPanel.add(newWindButton, new GridBagConstraints(1, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newGravityButton, new GridBagConstraints(2, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newDragButton, new GridBagConstraints(1, 1, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(newVortexButton, new GridBagConstraints(2, 1, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5), 0, 0));
        influenceListPanel.add(deleteInfluenceButton, new GridBagConstraints(1, 2, 2, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(5, 5, 10, 5), 0, 0));
        
        influenceParamsPanel = new JPanel(new BorderLayout());
        
        windParamsPanel = createWindParamsPanel();
        gravityParamsPanel = createGravityParamsPanel();
        dragParamsPanel = createDragParamsPanel();
        vortexParamsPanel = createVortexParamsPanel();
        
        JPanel influencePanel = new JPanel(new GridBagLayout());
        influencePanel.add(influenceListPanel, new GridBagConstraints(0, 0, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
        influencePanel.add(influenceParamsPanel, new GridBagConstraints(0, 1, 1, 1, 0.5,
            1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
            new Insets(5, 10, 10, 5), 0, 0));
        return influencePanel;
    }
    
    private JPanel createWindParamsPanel() {
        windDirectionPanel.setBorder(createTitledBorder(" DIRECTION "));
        windDirectionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicWind)influence).setWindDirection(
                    windDirectionPanel.getValue());
            }
        });
        windStrengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicWind)influence).setStrength(
                    windStrengthPanel.getFloatValue());
            }
        });
        windRandomBox = new JCheckBox(new AbstractAction("Vary Randomly") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicWind)influence).setRandom(
                    windRandomBox.isSelected());
            }
        });
        
        JPanel windParamsPanel = new JPanel(new GridBagLayout());
        windParamsPanel.setBorder(createTitledBorder(" WIND PARAMETERS "));
        windParamsPanel.add(windDirectionPanel, new GridBagConstraints(0, 0, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        windParamsPanel.add(windStrengthPanel, new GridBagConstraints(0, 1, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 10, 5), 0, 0));
        windParamsPanel.add(windRandomBox, new GridBagConstraints(0, 2, 1, 1, 0.5,
            0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 5, 10, 5), 0, 0));
        return windParamsPanel;
    }
    
    private JPanel createGravityParamsPanel() {
        gravityInfluencePanel.setBorder(createTitledBorder(" GRAVITY INFLUENCE "));
        gravityInfluencePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicGravity)influence).setGravityForce(
                    gravityInfluencePanel.getValue());
            }
        });
        return gravityInfluencePanel;
    }
    
    private JPanel createDragParamsPanel() {
        dragCoefficientPanel.setBorder(createTitledBorder(" DRAG PARAMETERS "));
        dragCoefficientPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicDrag)influence).setDragCoefficient(
                    dragCoefficientPanel.getFloatValue());
            }
        });
        return dragCoefficientPanel;
    }
    
    private JPanel createVortexParamsPanel() {
        vortexDirectionPanel.setBorder(createTitledBorder(" DIRECTION "));
        vortexDirectionPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicVortex)
                    influence).getAxis().setDirection(
                        vortexDirectionPanel.getValue());
            }
        });
        vortexStrengthPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicVortex)influence).setStrength(
                    vortexStrengthPanel.getFloatValue());
            }
        });
        vortexDivergencePanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicVortex)influence).setDivergence(
                    vortexDivergencePanel.getFloatValue() * FastMath.DEG_TO_RAD);
            }
        });
        vortexRandomBox = new JCheckBox(new AbstractAction("Vary Randomly") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                ParticleInfluence influence = particleGeom.getInfluences().get(
                    influenceList.getSelectedIndex());
                ((SimpleParticleInfluenceFactory.BasicVortex)influence).setRandom(
                    vortexRandomBox.isSelected());
            }
        });
        
        JPanel vortexParamsPanel = new JPanel(new GridBagLayout());
        vortexParamsPanel.setBorder(createTitledBorder(" VORTEX PARAMETERS "));
        vortexParamsPanel.add(vortexDirectionPanel, new GridBagConstraints(0,
            0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
        vortexParamsPanel.add(vortexStrengthPanel, new GridBagConstraints(0, 1,
            1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
        vortexParamsPanel.add(vortexDivergencePanel, new GridBagConstraints(0, 2,
            1, 1, 0.5, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 5), 0, 0));
        vortexParamsPanel.add(vortexRandomBox, new GridBagConstraints(0, 3, 1,
            1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 5, 10, 5), 0, 0));
        return vortexParamsPanel;
    }
    
    private JPanel createExamplesPanel() {
        JLabel examplesLabel = createBoldLabel("Prebuilt Examples:");
        
        exampleList = new JList(EXAMPLE_NAMES);
        exampleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (exampleList.getSelectedValue() instanceof String)
                    exampleButton.setEnabled(true);
                else
                    exampleButton.setEnabled(false);
            }
        });
        
        exampleButton = new JButton(new AbstractAction("Apply") {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) {
                applyExample();
            }
        });
        exampleButton.setMargin(new Insets(2, 14, 2, 14));
        exampleButton.setEnabled(false);

        JPanel examplesPanel = new JPanel(new GridBagLayout());
        examplesPanel.add(examplesLabel, new GridBagConstraints(0, 0, 1, 1,
            0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(10, 10, 5, 10), 0, 0));
        examplesPanel.add(new JScrollPane(exampleList), new GridBagConstraints(
            0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 10, 0, 10), 0, 0));
        examplesPanel.add(exampleButton, new GridBagConstraints(0, 2, 1, 1,
            0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(5, 10, 10, 10), 0, 0));
        return examplesPanel;
    }
    
    private JLabel createBoldLabel (String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        return label;
    }
    
    private TitledBorder createTitledBorder (String title)
    {
        TitledBorder border = new TitledBorder(" " + title + " ");
        border.setTitleFont(new Font("Arial", Font.PLAIN, 10));
        return border;
    }
    
    private void createNewSystem() {
        layerTable.clearSelection();
        particleNode.detachAllChildren();
        createNewLayer();
        layerModel.fireTableDataChanged();
        layerTable.setRowSelectionInterval(0, 0);
        deleteLayerButton.setEnabled(false);
        openFile = null;
        updateTitle();
    }
    
    private void showOpenDialog() {
        fileChooser.setSelectedFile(new File(""));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        prefs.put("particle_dir", file.getParent().toString());
        try {
            Object obj = BinaryImporter.getInstance().load(file);
            if (obj instanceof Node) {
                Node node = (Node)obj;
                for (int ii = node.getQuantity() - 1; ii >= 0; ii--) {
                    if (!(node.getChild(ii) instanceof ParticleGeometry)) {
                        node.detachChildAt(ii);
                    }
                }
                if (node.getQuantity() == 0) {
                    throw new Exception("Node contains no particle meshes");
                }
                layerTable.clearSelection();
                root.detachChild(particleNode);
                particleNode = node;
                root.attachChild(particleNode);
                deleteLayerButton.setEnabled(true);
                
            } else { // obj instanceof ParticleGeometry
                particleGeom = (ParticleGeometry)obj;
                layerTable.clearSelection();
                particleNode.detachAllChildren();
                particleNode.attachChild(particleGeom);   
                deleteLayerButton.setEnabled(false);
            }
            particleNode.updateRenderState();
            layerModel.fireTableDataChanged();
            layerTable.setRowSelectionInterval(0, 0);
            openFile = file;
            updateTitle();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't open '" + file +
                "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void showImportDialog() {
        fileChooser.setSelectedFile(new File(""));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        prefs.put("particle_dir", file.getParent().toString());
        try {
            Object obj = BinaryImporter.getInstance().load(file);
            int lidx = particleNode.getQuantity();
            if (obj instanceof Node) {
                Node node = (Node)obj;
                ArrayList<Spatial> meshes = new ArrayList<Spatial>();
                for (int ii = 0, nn = node.getQuantity(); ii < nn; ii++) {
                    if (node.getChild(ii) instanceof ParticleGeometry) {
                        meshes.add(node.getChild(ii));
                    }
                }
                if (meshes.size() == 0) {
                    throw new Exception("Node contains no particle meshes");
                }
                layerTable.clearSelection();
                for (Spatial mesh : meshes) {
                    particleNode.attachChild(mesh);
                }
                
            } else { // obj instanceof ParticleGeometry
                particleGeom = (ParticleGeometry)obj;
                layerTable.clearSelection();
                particleNode.attachChild(particleGeom);
            }
            particleNode.updateRenderState();
            layerModel.fireTableRowsInserted(lidx,
                particleNode.getQuantity() - 1);
            layerTable.setRowSelectionInterval(lidx, lidx);
            deleteLayerButton.setEnabled(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Couldn't open '" + file +
                "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void saveAs(File file) {
        if (file == null) {
            fileChooser.setSelectedFile(openFile == null ?
                new File("") : openFile);
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = fileChooser.getSelectedFile();
            prefs.put("particle_dir", file.getParent().toString());
        }
        try {
            BinaryExporter.getInstance().save(particleNode.getQuantity() > 1 ?
                particleNode : particleGeom, file);
            openFile = file;
            updateTitle();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Couldn't save '" + file +
                "': " + e, "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showBackgroundDialog()
    {
        final Color bg = JColorChooser.showDialog(this, "Choose Background Color",
            makeColor(impl.getRenderer().getBackgroundColor(), false));
        if (bg != null) {
            prefs.putInt("bg_color", bg.getRGB());
            RenderThreadActionQueue.addToQueue(new RenderThreadExecutable() {
                public void doAction() {       
                    impl.getRenderer().setBackgroundColor(makeColorRGBA(bg));
                }
            });
        }
    }
    
    private void createNewLayer() {
        particleGeom = ParticleFactory.buildParticles(createLayerName(), 300);
        particleGeom.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(
            new Vector3f(0,-3f,0), true));
        particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        particleGeom.setMaximumAngle(0.2268928f);
        particleGeom.getParticleController().setSpeed(1.0f);
        particleGeom.setMinimumLifeTime(2000.0f);
        particleGeom.setStartSize(10.0f);
        particleGeom.setEndSize(10.0f);
        particleGeom.setStartColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 1.0f));
        particleGeom.setEndColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 0.0f));
        particleGeom.setRandomMod(1.0f);
        particleGeom.warmUp(120);

        updateAlphaState(true);
        
        TextureState ts = impl.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(
            RenParticleEditor.class.getClassLoader().getResource(
                "jmetest/data/texture/flaresmall.jpg"),
            Texture.FM_LINEAR, Texture.FM_LINEAR));
        particleGeom.setRenderState(ts);
        
        particleNode.attachChild(particleGeom);
        particleGeom.updateRenderState();
    }
    
    private String createLayerName () {
        int max = -1;
        for (int ii = 0, nn = particleNode.getQuantity(); ii < nn; ii++) {
            String name = particleNode.getChild(ii).getName();
            if (name.startsWith("Layer #")) {
                try {
                    max = Math.max(max, Integer.parseInt(name.substring(7)));
                } catch (NumberFormatException e) {}
            }
        }
        return "Layer #" + (max + 1);
    }
    
    private void updateAlphaState(boolean additive) {
        AlphaState as = (AlphaState)particleGeom.getRenderState(
            RenderState.RS_ALPHA);
        if (as == null) {
            as = impl.getRenderer().createAlphaState();
            as.setBlendEnabled(true);
            as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
            as.setTestEnabled(true);
            as.setTestFunction(AlphaState.TF_GREATER);
            particleGeom.setRenderState(as);
            particleGeom.updateRenderState();
        }
        as.setDstFunction(additive ?
            AlphaState.DB_ONE : AlphaState.DB_ONE_MINUS_SRC_ALPHA);
    }
    
    private void deleteLayer() {
        int idx = layerTable.getSelectedRow(),
            sidx = (idx == particleNode.getQuantity() - 1) ? idx - 1 : idx;
        layerTable.clearSelection();
        particleNode.detachChildAt(idx);
        layerModel.fireTableRowsDeleted(idx, idx);
        layerTable.setRowSelectionInterval(sidx, sidx);
        
        if (particleNode.getQuantity() == 1) {
            deleteLayerButton.setEnabled(false);
        }
    }
    
    /**
     * applyExample
     */
    private void applyExample() {
        if (exampleList == null || exampleList.getSelectedValue() == null)
            return;
        String examType = exampleList.getSelectedValue().toString();
        particleGeom.clearInfluences();
        if ("FIRE".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.20943952f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 0.0f));
            particleGeom.setRandomMod(6.0f);
            particleGeom.getParticleController().setControlFlow(true);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.3f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("FOUNTAIN".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(
                new Vector3f(0,-3f,0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.2268928f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1300.0f);
            particleGeom.setMaximumLifeTime(1950.0f);
            particleGeom.setStartSize(10.0f);
            particleGeom.setEndSize(10.0f);
            particleGeom.setStartColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 0.0f));
            particleGeom.setRandomMod(1.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.1f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("LAVA".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(
                new Vector3f(0,-3f,0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(0.418f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(1057.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(1.0f, 0.18f, 0.125f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.18f, 0.125f, 0.0f));
            particleGeom.setRandomMod(2.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.1f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("SMOKE".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 0.6f, 0.0f));
            particleGeom.setMaximumAngle(0.36651915f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.2f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(32.5f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 0.0f));
            particleGeom.setRandomMod(0.1f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.58f);
            particleGeom.setParticleSpinSpeed(0.08f);
        } else if ("RAIN".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(
                new Vector3f(0,-3f,0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, -1.0f, 0.0f));
            particleGeom.setMaximumAngle(3.1415927f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.5f);
            particleGeom.setMinimumLifeTime(1626.0f);
            particleGeom.setMaximumLifeTime(2400.0f);
            particleGeom.setStartSize(9.1f);
            particleGeom.setEndSize(13.6f);
            particleGeom.setStartColor(new ColorRGBA(0.16078432f, 0.16078432f, 1.0f,
                    1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.16078432f, 0.16078432f, 1.0f,
                    0.15686275f));
            particleGeom.setRandomMod(0.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.58f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("SNOW".equalsIgnoreCase(examType)) {
            particleGeom.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(
                new Vector3f(0,-3f,0), true));
            particleGeom.setEmissionDirection(new Vector3f(0.0f, -1.0f, 0.0f));
            particleGeom.setMaximumAngle(1.5707964f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(0.2f);
            particleGeom.setMinimumLifeTime(1057.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(30.0f);
            particleGeom.setEndSize(30.0f);
            particleGeom.setStartColor(new ColorRGBA(0.3764706f, 0.3764706f,
                    0.3764706f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.3764706f, 0.3764706f,
                    0.3764706f, 0.1882353f));
            particleGeom.setRandomMod(1.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(0.59999996f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("JET".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(-1.0f, 0.0f, 0.0f));
            particleGeom.setMaximumAngle(0.034906585f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.0f);
            particleGeom.setMinimumLifeTime(100.0f);
            particleGeom.setMaximumLifeTime(150.0f);
            particleGeom.setStartSize(6.6f);
            particleGeom.setEndSize(30.0f);
            particleGeom.setStartColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.6f, 0.2f, 0.0f, 0.0f));
            particleGeom.setRandomMod(10.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.4599999f);
            particleGeom.getParticleController().setRepeatType(Controller.RT_WRAP);
        } else if ("EXPLOSION".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
            particleGeom.setMaximumAngle(3.1415927f);
            particleGeom.setMinimumAngle(0);
            particleGeom.getParticleController().setSpeed(1.4f);
            particleGeom.setMinimumLifeTime(1000.0f);
            particleGeom.setMaximumLifeTime(1500.0f);
            particleGeom.setStartSize(40.0f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f,
                    0.0f));
            particleGeom.setRandomMod(0.0f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.getParticleController().setRepeatType(Controller.RT_CLAMP);
        } else if ("GROUND FOG".equalsIgnoreCase(examType)) {
            particleGeom.setEmissionDirection(new Vector3f(0.0f, 0.3f, 0.0f));
            particleGeom.setMaximumAngle(1.5707964f);
            particleGeom.setMinimumAngle(1.5707964f);
            particleGeom.getParticleController().setSpeed(0.5f);
            particleGeom.setMinimumLifeTime(1774.0f);
            particleGeom.setMaximumLifeTime(2800.0f);
            particleGeom.setStartSize(35.4f);
            particleGeom.setEndSize(40.0f);
            particleGeom.setStartColor(new ColorRGBA(0.87058824f, 0.87058824f, 0.87058824f, 1.0f));
            particleGeom.setEndColor(new ColorRGBA(0.0f, 0.8f, 0.8f, 0.0f));
            particleGeom.setRandomMod(0.3f);
            particleGeom.getParticleController().setControlFlow(false);
            particleGeom.setReleaseRate(300);
            particleGeom.setReleaseVariance(0.0f);
            particleGeom.setInitialVelocity(1.0f);
            particleGeom.setParticleSpinSpeed(0.0f);
        }

        particleGeom.warmUp(120);
        updateFromManager();
    }

    /**
     * updateFromManager
     */
    public void updateFromManager() {
        // update appearance controls
        updateCountLabels();
        geomTypeBox.setSelectedIndex(particleGeom.getParticleType());
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
        Texture tex = ((TextureState)particleGeom.getRenderState(
            RenderState.RS_TEXTURE)).getTexture();
        try {
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
            System.err.println("image: "+tex+" : "+tex.getImageLocation());
            e.printStackTrace();
        }

        // update origin controls
        translationPanel.setValue(particleGeom.getLocalTranslation());
        float[] angles = particleGeom.getLocalRotation().toAngles(null);
        rotationPanel.setValue(new Vector3f(angles[0], angles[1],
            angles[2]).multLocal(FastMath.RAD_TO_DEG));
        scalePanel.setValue(particleGeom.getLocalScale().x);
        
        switch (particleGeom.getEmitType()) {
            case ParticleGeometry.ET_POINT:
                originTypeBox.setSelectedItem("Point");
                break;
            case ParticleGeometry.ET_LINE:
                originTypeBox.setSelectedItem("Line");
                break;
            case ParticleGeometry.ET_RECTANGLE:
                originTypeBox.setSelectedItem("Rectangle");
                break;
            case ParticleGeometry.ET_RING:
                originTypeBox.setSelectedItem("Ring"); 
                break;
        } 
        updateOriginParams();
        
        // update emission controls
        rotateWithEmitterBox.setSelected(particleGeom.isRotateWithScene());
        directionPanel.setValue(particleGeom.getEmissionDirection());
        minAnglePanel.setValue(particleGeom.getMinimumAngle() * FastMath.RAD_TO_DEG);
        maxAnglePanel.setValue(particleGeom.getMaximumAngle() * FastMath.RAD_TO_DEG);
        velocityPanel.setValue(particleGeom.getInitialVelocity());
        spinPanel.setValue(particleGeom.getParticleSpinSpeed());
        
        // update flow controls
        rateBox.setSelected(particleGeom.getParticleController().isControlFlow());
        releaseRatePanel.setValue(particleGeom.getReleaseRate());
        rateVarPanel.setValue(particleGeom.getReleaseVariance());
        updateRateLabels();
        spawnBox.setSelected(particleGeom.getParticleController().getRepeatType() ==
            Controller.RT_WRAP);
        
        // update world controls
        speedPanel.setValue(particleGeom.getParticleController().getSpeed());
        massPanel.setValue(particleGeom.getParticle(0).getMass());
        minAgePanel.setValue(particleGeom.getMinimumLifeTime());
        maxAgePanel.setValue(particleGeom.getMaximumLifeTime());
        randomPanel.setValue(particleGeom.getRandomMod());
        
        // update influence controls
        influenceList.clearSelection();
        int fcount = (particleGeom.getInfluences() == null) ?
            0 : particleGeom.getInfluences().size();
        influenceModel.fireContentsChanged(0, fcount - 1);
        
        validate();
    }

    public void changeParticleType(int newType) {
        int oldType = particleGeom.getParticleType();
        if (newType == oldType) {
            return;
        }
        ParticleGeometry oldGeom = particleGeom, newGeom;
        if (newType == ParticleGeometry.PT_POINT) {
            newGeom = ParticleFactory.buildPointParticles(oldGeom.getName(),
                oldGeom.getNumParticles());
        } else if (newType == ParticleGeometry.PT_LINE) {
            newGeom = ParticleFactory.buildLineParticles(oldGeom.getName(),
                oldGeom.getNumParticles());
        } else {
            newGeom = ParticleFactory.buildParticles(oldGeom.getName(),
                oldGeom.getNumParticles(), newType);
        }
        // copy appearance parameters
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
        newGeom.setRandomMod(oldGeom.getRandomMod());
        
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
        
        particleNode.getChildren().set(
            particleNode.getChildren().indexOf(oldGeom), newGeom);
        particleGeom = newGeom;
        particleGeom.updateRenderState();
    }
    
    /**
     * updateManager
     * 
     * @param particles
     *            number of particles to reset manager with.
     */
    public void resetManager(int particles) {
        particleGeom.recreate(particles);
        validate();
    }

    /**
     * updateOriginParams
     */
    private void updateOriginParams() {
        originParamsPanel.removeAll();
        String type = (String)originTypeBox.getSelectedItem();
        if (type.equals("Point")) {
            particleGeom.setEmitType(ParticleGeometry.ET_POINT);
            originParamsPanel.add(pointParamsPanel);
            
        } else if (type.equals("Line")) {
            particleGeom.setEmitType(ParticleGeometry.ET_LINE);
            Line line = particleGeom.getLine();
            if (line == null) {
                particleGeom.setGeometry(line = new Line());
            }
            lineLengthPanel.setValue(line.getOrigin().distance(
                line.getDirection()));
            originParamsPanel.add(lineParamsPanel);
            
        } else if (type.equals("Rectangle")) {
            particleGeom.setEmitType(ParticleGeometry.ET_RECTANGLE);
            Rectangle rect = particleGeom.getRectangle();
            if (rect == null) {
                particleGeom.setGeometry(rect = new Rectangle());
            }
            rectWidthPanel.setValue(rect.getA().distance(rect.getB()));
            rectHeightPanel.setValue(rect.getA().distance(rect.getC()));
            originParamsPanel.add(rectParamsPanel);
            
        } else if (type.equals("Ring")) {
            particleGeom.setEmitType(ParticleGeometry.ET_RING);
            Ring ring = particleGeom.getRing();
            if (ring == null) {
                particleGeom.setGeometry(ring = new Ring());
            }
            ringInnerPanel.setValue(ring.getInnerRadius());
            ringOuterPanel.setValue(ring.getOuterRadius());
            originParamsPanel.add(ringParamsPanel);
        }
        originParamsPanel.getParent().validate();
        originParamsPanel.getParent().repaint();
    }
    
    /**
     * updateInfluenceParams
     */
    private void updateInfluenceParams() {
        influenceParamsPanel.removeAll();
        int idx = influenceList.getSelectedIndex();
        if (idx == -1) {
            influenceParamsPanel.validate();
            return;
        }
        ParticleInfluence influence = particleGeom.getInfluences().get(idx);
        if (influence instanceof SimpleParticleInfluenceFactory.BasicWind) {
            SimpleParticleInfluenceFactory.BasicWind wind =
                (SimpleParticleInfluenceFactory.BasicWind)influence;
            windDirectionPanel.setValue(wind.getWindDirection());
            windStrengthPanel.setValue(wind.getStrength());
            windRandomBox.setSelected(wind.isRandom());
            influenceParamsPanel.add(windParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicGravity) {
            gravityInfluencePanel.setValue(
                ((SimpleParticleInfluenceFactory.BasicGravity)influence).getGravityForce());
            influenceParamsPanel.add(gravityParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicDrag) {
            dragCoefficientPanel.setValue(
                ((SimpleParticleInfluenceFactory.BasicDrag)influence).getDragCoefficient());
            influenceParamsPanel.add(dragParamsPanel);
            
        } else if (influence instanceof SimpleParticleInfluenceFactory.BasicVortex) {
            SimpleParticleInfluenceFactory.BasicVortex vortex =
                (SimpleParticleInfluenceFactory.BasicVortex)influence;
            vortexDirectionPanel.setValue(vortex.getAxis().getDirection());
            vortexStrengthPanel.setValue(vortex.getStrength());
            vortexDivergencePanel.setValue(vortex.getDivergence() * FastMath.RAD_TO_DEG);
            vortexRandomBox.setSelected(vortex.isRandom());
            influenceParamsPanel.add(vortexParamsPanel);
        }
        influenceParamsPanel.getParent().validate();
        influenceParamsPanel.getParent().repaint();
    }
    
    /**
     * updateRateLabels
     */
    private void updateRateLabels() {
        releaseRatePanel.setEnabled(rateBox.isSelected());
        rateVarPanel.setEnabled(rateBox.isSelected());
    }

    /**
     * updateCountLabels
     */
    private void updateCountLabels() {
        int val = particleGeom.getNumParticles();
        countLabel.setText("Particles: " + val);
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
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }
    
    private void startColorPanel_mouseClicked(MouseEvent e) {
        if (!colorChooserFrame.isVisible()) {
            colorstart = true;
            colorChooserFrame.setVisible(true);
        }
    }

    private void endColorPanel_mouseClicked(MouseEvent e) {
        if (!colorChooserFrame.isVisible()) {
            colorstart = false;
            colorChooserFrame.setVisible(true);
        }
    }
        
    private void initColorChooser() {
        colorChooser.setColor(endColorPanel.getBackground());
        colorChooserFrame.setLayout(new BorderLayout());
        colorChooserFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        colorChooserFrame.add(colorChooser, BorderLayout.CENTER);

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
                    particleGeom.setStartColor(rgba);
                    startColorPanel.setBackground(color);
                } else {
                    rgba.a = (Integer.parseInt(endAlphaSpinner.getValue()
                            .toString()) / 255f);
                    particleGeom.setEndColor(rgba);
                    endColorPanel.setBackground(color);
                }
                updateColorLabels();
                colorChooserFrame.setVisible(false);
            }
         });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setOpaque(true);
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               colorChooserFrame.setVisible(false);
           }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        colorChooserFrame.add(buttonPanel, BorderLayout.SOUTH);
        colorChooserFrame.setSize(colorChooserFrame.getPreferredSize());
        colorChooserFrame.setLocationRelativeTo(null);
    }
    
    private void initFileChooser() {
        String pdir = prefs.get("particle_dir", null);
        if (pdir != null) {
            fileChooser.setCurrentDirectory(new File(pdir));
        }
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() ||
                    f.toString().toLowerCase().endsWith(".jme");
            }
            public String getDescription() {
                return "JME Files (*.jme)";
            }
        });
    }
    
    private void initTextureChooser() {
        String tdir = prefs.get("texture_dir", null);
        if (tdir != null) {
            textureChooser.setCurrentDirectory(new File(tdir));
        }
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
        resetManager(particles);
        updateCountLabels();
    }

    private void changeTexture() {
        try {
            int result = textureChooser.showOpenDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File textFile = textureChooser.getSelectedFile();
            prefs.put("texture_dir", textFile.getParent().toString());

            newTexture = textFile;

            ImageIcon icon = new ImageIcon(
                getToolkit().createImage(textFile.getAbsolutePath()));
            imageLabel.setIcon(icon);
            validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -------------GL STUFF------------------

            // make the canvas:
            glCanvas = DisplaySystem.getDisplaySystem("LWJGL").createCanvas(width, height);

            // add a listener... if window is resized, we can do something about it.
            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });
            
            camhand = new CameraHandler();
            
            glCanvas.addMouseWheelListener(camhand);
            glCanvas.addMouseListener(camhand);
            glCanvas.addMouseMotionListener(camhand);

            // Important!  Here is where we add the guts to the canvas:
            impl = new MyImplementor(width, height);

            ((JMECanvas) glCanvas).setImplementor(impl);
            
            // -----------END OF GL STUFF-------------
        }
        return glCanvas;
    }

    class CameraHandler extends MouseAdapter
        implements MouseMotionListener, MouseWheelListener {
        Point last = new Point(0,0);
        Vector3f focus = new Vector3f();
        private Vector3f vector = new Vector3f();
        private Quaternion rot = new Quaternion();
        
        public void mouseDragged(final MouseEvent arg0) {
            RenderThreadExecutable exe = new RenderThreadExecutable() {
                public void doAction() {
                    int difX = last.x - arg0.getX();
                    int difY = last.y - arg0.getY();
                    int mult = arg0.isShiftDown() ? 10 : 1;
                    last.x = arg0.getX();
                    last.y = arg0.getY();
                    
                    int mods = arg0.getModifiers();
                    if ((mods & InputEvent.BUTTON1_MASK) != 0) {
                        rotateCamera(Vector3f.UNIT_Y, difX * 0.0025f);
                        rotateCamera(impl.getRenderer().getCamera().getLeft(),
                            -difY * 0.0025f);
                    }
                    if ((mods & InputEvent.BUTTON2_MASK) != 0 && difY != 0) {
                        zoomCamera(difY * mult);
                    }
                    if ((mods & InputEvent.BUTTON3_MASK) != 0) {
                        panCamera(-difX, -difY);
                    }
                }
            };
            RenderThreadActionQueue.addToQueue(exe);
        }
        public void mouseMoved(MouseEvent arg0) {}

        public void mousePressed(MouseEvent arg0) {
            last.x = arg0.getX();
            last.y = arg0.getY();
        }

        public void mouseWheelMoved(final MouseWheelEvent arg0) {
            RenderThreadExecutable exe = new RenderThreadExecutable() {
                public void doAction() {
                    zoomCamera(arg0.getWheelRotation() *
                        (arg0.isShiftDown() ? -100 : -20));
                }
            };
            RenderThreadActionQueue.addToQueue(exe);
        }
     
        public void recenterCamera() {
            RenderThreadActionQueue.addToQueue(new RenderThreadExecutable() {
                public void doAction() {
                    Camera cam = impl.getRenderer().getCamera();
                    Vector3f.ZERO.subtract(focus, vector);
                    cam.getLocation().addLocal(vector);
                    focus.addLocal(vector);
                    cam.onFrameChange();     
                }
            });
        }
        
        private void rotateCamera(Vector3f axis, float amount) {
            Camera cam = impl.getRenderer().getCamera();
            if (axis.equals(cam.getLeft())) {
                float elevation = -FastMath.asin(cam.getDirection().y);
                amount = Math.min(Math.max(elevation + amount,
                    -FastMath.HALF_PI), FastMath.HALF_PI) - elevation;
            }
            rot.fromAngleAxis(amount, axis);
            cam.getLocation().subtract(focus, vector);
            rot.mult(vector, vector);
            focus.add(vector, cam.getLocation());
            rot.mult(cam.getLeft(), cam.getLeft());
            rot.mult(cam.getUp(), cam.getUp());
            rot.mult(cam.getDirection(), cam.getDirection());
            cam.normalize();
            cam.onFrameChange();
        }
        
        private void panCamera(float left, float up) {
            Camera cam = impl.getRenderer().getCamera();
            cam.getLeft().mult(left, vector);
            vector.scaleAdd(up, cam.getUp(), vector);
            cam.getLocation().addLocal(vector);
            focus.addLocal(vector);
            cam.onFrameChange();
        }
        
        private void zoomCamera(float amount) {
            Camera cam = impl.getRenderer().getCamera();
            float dist = cam.getLocation().distance(focus);
            amount = dist - Math.max(0f, dist - amount);
            cam.getLocation().scaleAdd(amount, cam.getDirection(),
                cam.getLocation());
            cam.onFrameChange();
        }
    }
    
    protected void doResize() {
        if (impl != null) {
            impl.resizeCanvas(glCanvas.getWidth(), glCanvas.getHeight());
            if (impl.getCamera() != null) {
                RenderThreadExecutable exe = new RenderThreadExecutable() {
                    public void doAction() {
                        impl.getCamera().setFrustumPerspective(45.0f,
                                (float) glCanvas.getWidth()
                                        / (float) glCanvas.getHeight(),
                                1, 10000);
                    }
                };
                RenderThreadActionQueue.addToQueue(exe);
            }
        }
    }

    class LayerTableModel extends AbstractTableModel {
        
        private static final long serialVersionUID = 1L;

        public int getRowCount() {
            return particleNode == null ? 0 : particleNode.getQuantity();
        }
        
        public int getColumnCount() {
            return 2;
        }
        
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? "Name" : "Visible";
        }
        
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : Boolean.class;
        }
        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            ParticleGeometry pmesh = (ParticleGeometry)particleNode.getChild(rowIndex);
            return (columnIndex == 0) ? pmesh.getName() : Boolean.valueOf(
                pmesh.getCullMode() != SceneElement.CULL_ALWAYS);
        }
        
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            ParticleGeometry pmesh = (ParticleGeometry)particleNode.getChild(rowIndex);
            if (columnIndex == 0) {
                pmesh.setName((String)aValue);
            } else {
                pmesh.setCullMode(((Boolean)aValue).booleanValue() ?
                    SceneElement.CULL_DYNAMIC : SceneElement.CULL_ALWAYS);
            }
        }
    }
    
    class InfluenceListModel extends AbstractListModel {
        
        private static final long serialVersionUID = 1L;
        
        public int getSize() {
            return (particleGeom == null || particleGeom.getInfluences() == null) ?
                0 : particleGeom.getInfluences().size();
        }
        
        public Object getElementAt(int index) {
            ParticleInfluence pf = particleGeom.getInfluences().get(index);
            if (pf instanceof SimpleParticleInfluenceFactory.BasicWind) {
                return "Wind";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicGravity) {
                return "Gravity";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicDrag) {
                return "Drag";
            } else if (pf instanceof SimpleParticleInfluenceFactory.BasicVortex) {
                return "Vortex";
            } else {
                return "???";
            }
        }
        
        public void fireContentsChanged(int idx0, int idx1) {
            super.fireContentsChanged(this, idx0, idx1);
        }
        
        public void fireIntervalAdded(int idx0, int idx1) {
            super.fireIntervalAdded(this, idx0, idx1);
        }
        
        public void fireIntervalRemoved(int idx0, int idx1) {
            super.fireIntervalRemoved(this, idx0, idx1);
        }
    }

    class VectorPanel extends JPanel
        implements ChangeListener {
    
        private static final long serialVersionUID = 1L;
        
        private ValuePanel xPanel, yPanel, zPanel;
        private ArrayList<ChangeListener> changeListeners =
            new ArrayList<ChangeListener>();
        private boolean setting;
        
        public VectorPanel(float min, float max, float step) {
            super(new GridBagLayout());
            
            xPanel = new ValuePanel("X: ", "", min, max, step);
            xPanel.addChangeListener(this);
            
            yPanel = new ValuePanel("Y: ", "", min, max, step);
            yPanel.addChangeListener(this);
            
            zPanel = new ValuePanel("Z: ", "", min, max, step);
            zPanel.addChangeListener(this);
            
            add(xPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
            add(yPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
            add(zPanel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        }
        
        public void setValue(Vector3f value) {
            setting = true;
            xPanel.setValue(value.x);
            yPanel.setValue(value.y);
            zPanel.setValue(value.z);
            setting = false;
        }
        
        public Vector3f getValue() {
            return new Vector3f(xPanel.getFloatValue(), yPanel.getFloatValue(),
                zPanel.getFloatValue());
        }
        
        public void addChangeListener(ChangeListener l) {
            changeListeners.add(l);
        }
        
        public void stateChanged(ChangeEvent e) {
            if (!setting) {
                for (ChangeListener l : changeListeners) {
                    l.stateChanged(e);
                }
            }
        }
    }
    
    class UnitVectorPanel extends JPanel
        implements ChangeListener {
        
        private static final long serialVersionUID = 1L;
        
        private ValuePanel azimuthPanel = new ValuePanel("Azimuth: ", "", -180f, +180f, 1f);
        private ValuePanel elevationPanel = new ValuePanel("Elevation: ", "", -90f, +90f, 1f);
        private ArrayList<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
        private boolean setting;
        private Vector3f vector = new Vector3f();
        
        public UnitVectorPanel() {
            super(new GridBagLayout());
            azimuthPanel.addChangeListener(this);
            elevationPanel.addChangeListener(this);
            
            add(azimuthPanel, new GridBagConstraints(0, 0, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
            add(elevationPanel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        }
        
        public void setValue(Vector3f value) {
            FastMath.cartesianToSpherical(value, vector);
            setting = true;
            azimuthPanel.setValue(vector.y * FastMath.RAD_TO_DEG);
            elevationPanel.setValue(vector.z * FastMath.RAD_TO_DEG);
            setting = false;
        }
        
        public Vector3f getValue() {
            vector.set(1f, azimuthPanel.getFloatValue() * FastMath.DEG_TO_RAD,
                elevationPanel.getFloatValue() * FastMath.DEG_TO_RAD);
            Vector3f result = new Vector3f();
            FastMath.sphericalToCartesian(vector, result);
            return result;
        }
        
        public void addChangeListener(ChangeListener l) {
            changeListeners.add(l);
        }
        
        public void stateChanged(ChangeEvent e) {
            if (!setting) {
                for (ChangeListener l : changeListeners) {
                    l.stateChanged(e);
                }
            }
        }
    }
    
    class ValuePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        
        public ValueSpinner spinner;
        
        private JLabel plabel, slabel;
        private float scale;
        private NumberFormat format;
        
        public ValuePanel(String prefix, String suffix, float min, float max,
            float step) {
            add(plabel = createBoldLabel(prefix));
            add(spinner = new ValueSpinner(min, max, step));
            add(slabel = createBoldLabel(suffix));
        }
        
        public ValuePanel(String prefix, String suffix, int min, int max,
            int step) {
            add(plabel = createBoldLabel(prefix));
            add(spinner = new ValueSpinner(min, max, step));
            add(slabel = createBoldLabel(suffix));
        }
        
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            plabel.setEnabled(enabled);
            spinner.setEnabled(enabled);
            slabel.setEnabled(enabled);
        }
        
        public void setValue(float value) {
            spinner.setValue(Float.valueOf(value));
        }
        
        public void setValue(int value) {
            spinner.setValue(Integer.valueOf(value));
        }
        
        public float getFloatValue() {
            return ((Number)spinner.getValue()).floatValue();
        }
        
        public int getIntValue() {
            return ((Number)spinner.getValue()).intValue();
        }
        
        public void addChangeListener(ChangeListener l) {
            spinner.addChangeListener(l);
        }
    }
    
    class ValueSpinner extends JSpinner {
    
        public ValueSpinner(float minimum, float maximum, float stepSize) {
            this(Float.valueOf(minimum), Float.valueOf(maximum),
                Float.valueOf(stepSize));
            ((NumberEditor)getEditor()).getFormat().setMinimumFractionDigits(
                (int)FastMath.log(1f/stepSize, 10f));
        }
        
        public ValueSpinner(int minimum, int maximum, int stepSize) {
            this(Integer.valueOf(minimum), Integer.valueOf(maximum),
                Integer.valueOf(stepSize));
        }
        
        public ValueSpinner(Number minimum, Number maximum, Number stepSize) {
            super(new SpinnerNumberModel(minimum, (Comparable)minimum,
                (Comparable)maximum, stepSize));
            MouseInputAdapter mia = new MouseInputAdapter() {
                public void mousePressed(MouseEvent e) {
                    _last.setLocation(e.getPoint());
                } 
                public void mouseDragged(MouseEvent e) {
                    int delta = (e.getX() - _last.x) + (_last.y - e.getY());
                    _last.setLocation(e.getPoint());
                    for (int ii = 0, nn = Math.abs(delta); ii < nn; ii++) {
                        Object next = (delta > 0) ? getModel().getNextValue() :
                            getModel().getPreviousValue();
                        if (next != null) {
                            getModel().setValue(next);
                        }
                    }
                }
                protected Point _last = new Point();
            };
            setEditor(new NumberEditor(this) {
                public Dimension preferredLayoutSize(Container parent) {
                    Dimension d = super.preferredLayoutSize(parent);
                    d.width = Math.max(Math.min(d.width, 50), 65);
                    return d;
                }
            });
            addMouseInputListener(this, mia);
        }
        
        protected void addMouseInputListener(Container c, MouseInputAdapter mia) {
            for (int ii = 0, nn = c.getComponentCount(); ii < nn; ii++) {
                Component comp = c.getComponent(ii);
                if (comp instanceof JButton) {
                    comp.addMouseListener(mia);
                    comp.addMouseMotionListener(mia);
                    
                } else if (comp instanceof Container) {
                    addMouseInputListener((Container)comp, mia);
                }
            }
        }
    }
    
    // IMPLEMENTING THE SCENE:

    class MyImplementor extends SimpleCanvasImpl {

        private static final int GRID_LINES = 51;
        private static final float GRID_SPACING = 100f;

        /**
         * The root node of our text.
         */
        protected Node fpsNode;

        /**
         * Displays all the lovely information at the bottom.
         */
        protected Text fps;
        
        /**
         * This is used to recieve getStatistics calls.
         */
        protected StringBuffer tempBuffer = new StringBuffer();

        /**
         * This is used to display print text.
         */
        protected StringBuffer updateBuffer = new StringBuffer( 30 );

        public MyImplementor(int width, int height) {
            super(width, height);
        }

        public void simpleSetup() {
            Color bg = new Color(prefs.getInt("bg_color", 0));
            renderer.setBackgroundColor(makeColorRGBA(bg));
            cam.setFrustumPerspective(45.0f,
                    (float) glCanvas.getWidth()
                            / (float) glCanvas.getHeight(),
                    1, 10000);

            Vector3f loc = new Vector3f(0, 850, -850);
            Vector3f left = new Vector3f(1, 0, 0);
            Vector3f up = new Vector3f(0, 0.7071f, 0.7071f);
            Vector3f dir = new Vector3f(0, -0.7071f, 0.7071f);
            cam.setFrame(loc, left, up, dir);
            
            root = rootNode;
            
            // Then our font Text object.
            /** This is what will actually have the text at the bottom. */
            fps = Text.createDefaultTextLabel( "FPS label" );
            fps.setCullMode( SceneElement.CULL_NEVER );
            fps.setTextureCombineMode( TextureState.REPLACE );

            // Finally, a stand alone node (not attached to root on purpose)
            fpsNode = new Node( "FPS node" );
            fpsNode.setRenderState( fps.getRenderState( RenderState.RS_ALPHA ) );
            fpsNode.setRenderState( fps.getRenderState( RenderState.RS_TEXTURE ) );
            fpsNode.attachChild( fps );
            fpsNode.setCullMode( SceneElement.CULL_NEVER );

            renderer.enableStatistics(true);
            
            root.attachChild(grid = createGrid());
            grid.updateRenderState();
            
            particleNode = new Node("particles");
            root.attachChild(particleNode);

            ZBufferState zbuf = renderer.createZBufferState();
            zbuf.setWritable( false );
            zbuf.setEnabled( true );
            zbuf.setFunction( ZBufferState.CF_LEQUAL );

            particleNode.setRenderState(zbuf);
            particleNode.updateRenderState();
            
            fpsNode.updateGeometricState(0, true);
            fpsNode.updateRenderState();
            
            createNewSystem();
            
        };

        public void simpleUpdate() {
            while (!RenderThreadActionQueue.isEmpty()) {
                RenderThreadActionQueue.processQueueItem();
            }
            
            if (newTexture != null) {
                loadApplyTexture();
            }

            updateBuffer.setLength( 0 );
            updateBuffer.append( "FPS: " ).append( (int) timer.getFrameRate() ).append(
                    " - " );
            updateBuffer.append( renderer.getStatistics( tempBuffer ) );
            /** Send the fps to our fps bar at the bottom. */
            fps.print( updateBuffer );
        }
        
        @Override
        public void simpleRender() {
            fpsNode.draw(renderer);
            renderer.clearStatistics();
        }
        
        private Geometry createGrid() {
            Vector3f[] vertices = new Vector3f[GRID_LINES * 2 * 2];
            float edge = GRID_LINES/2 * GRID_SPACING;
            for (int ii = 0, idx = 0; ii < GRID_LINES; ii++) {
                float coord = (ii - GRID_LINES/2) * GRID_SPACING;
                vertices[idx++] = new Vector3f(-edge, 0f, coord);
                vertices[idx++] = new Vector3f(+edge, 0f, coord);
                vertices[idx++] = new Vector3f(coord, 0f, -edge);
                vertices[idx++] = new Vector3f(coord, 0f, +edge);
            }
            Geometry grid = new com.jme.scene.Line(
                "grid", vertices, null, null, null);
            grid.getBatch(0).getDefaultColor().set(ColorRGBA.darkGray);
            return grid;
        }

        private void loadApplyTexture() {
            TextureState ts = (TextureState)particleGeom.getRenderState(RenderState.RS_TEXTURE);
            TextureManager.clearCache();
            ts.setTexture(
                    TextureManager.loadTexture(
                            newTexture.getAbsolutePath(),
                            Texture.MM_LINEAR,
                            Texture.FM_LINEAR));
            ts.setEnabled(true);
            particleGeom.setRenderState(ts);
            particleGeom.updateRenderState();
            newTexture = null;
        }
    }
}
