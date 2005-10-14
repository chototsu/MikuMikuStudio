/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package jmetest.input;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.system.DisplaySystem;
import com.jmex.awt.JMECanvas;
import com.jmex.awt.input.AWTKeyInput;
import com.jmex.awt.input.AWTMouseInput;

public class RenControlEditor extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JSpinner maxRollSpinner;
    private JSpinner minRollSpinner;
    private JSpinner ascentSpinner;
    private Canvas glCanvas;
    int width = 640, height = 480;
    private ControlImplementor impl;
    
    private Dimension MIN_DIMENSION = new Dimension(400, 300);
    
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
            JFrame.setDefaultLookAndFeelDecorated(true);
            new RenControlEditor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RenControlEditor() {
        setTitle("RenControlEditor - 3rd Person");
        setBounds(100, 100, 702, 409);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String vers = System.getProperty("os.name").toLowerCase();
        boolean isMac = false;
        if (vers.indexOf("mac") != -1) {
           isMac = true;
        }
        
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        final JPanel testPanel = new JPanel();
        testPanel.setPreferredSize(new Dimension(50, 50));
        testPanel.setMinimumSize(new Dimension(100, 100));
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_2.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_2.fill = GridBagConstraints.BOTH;
        gridBagConstraints_2.weighty = 1.0;
        gridBagConstraints_2.weightx = .7;
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 0;
        panel.add(testPanel, gridBagConstraints_2);
        testPanel.setLayout(new BorderLayout());
        testPanel.add(getGlCanvas(), BorderLayout.CENTER);
        testPanel.setBorder(new TitledBorder(null, "Test Here", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(SwingConstants.RIGHT);
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_3.fill = GridBagConstraints.BOTH;
        gridBagConstraints_3.weightx = .3;
        gridBagConstraints_3.weighty = 1.0;
        gridBagConstraints_3.insets = new Insets(4, 4, 0, 4);
        gridBagConstraints_3.gridx = 1;
        gridBagConstraints_3.gridy = 0;
        panel.add(tabbedPane, gridBagConstraints_3);
        tabbedPane.setMinimumSize(new Dimension(200, 100));
        tabbedPane.setPreferredSize(new Dimension(200, 100));
        tabbedPane.setBorder(new TitledBorder(null, "Params", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final JPanel panel_3 = new JPanel();
        panel_3.setLayout(new GridBagLayout());
//        if (!isMac) { // we need to use vertical label
//            VTextIcon icon = new VTextIcon(tabbedPane, "Camera");
//            tabbedPane.addTab(null, icon, panel_3, null);
//        } else
            tabbedPane.addTab("Camera", null, panel_3, null);

        final JLabel maxAscentLabel = new JLabel();
        maxAscentLabel.setText("Max ascent:");
        final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
        gridBagConstraints_4.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_4.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_4.gridy = 0;
        gridBagConstraints_4.gridx = 0;
        panel_3.add(maxAscentLabel, gridBagConstraints_4);

        ascentSpinner = new JSpinner();
        ascentSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.err.println("hi!" +ascentSpinner.getValue());
            }
        });
        final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
        gridBagConstraints_5.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_5.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_5.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_5.gridy = 1;
        gridBagConstraints_5.gridx = 0;
        panel_3.add(ascentSpinner, gridBagConstraints_5);

        final JLabel oLabel = new JLabel();
        oLabel.setFont(new Font("", Font.PLAIN, 9));
        final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
        gridBagConstraints_8.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_8.gridy = 1;
        gridBagConstraints_8.gridx = 1;
        panel_3.add(oLabel, gridBagConstraints_8);
        oLabel.setText("o");

        final JLabel minRollinLabel = new JLabel();
        minRollinLabel.setText("Min Rollin:");
        final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
        gridBagConstraints_6.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_6.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_6.gridy = 2;
        gridBagConstraints_6.gridx = 0;
        panel_3.add(minRollinLabel, gridBagConstraints_6);

        minRollSpinner = new JSpinner();
        final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
        gridBagConstraints_7.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_7.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_7.gridy = 3;
        gridBagConstraints_7.gridx = 0;
        panel_3.add(minRollSpinner, gridBagConstraints_7);

        final JLabel maxRollinLabel = new JLabel();
        maxRollinLabel.setText("Max Rollin:");
        final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
        gridBagConstraints_9.insets = new Insets(4, 4, 0, 0);
        gridBagConstraints_9.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_9.gridy = 2;
        gridBagConstraints_9.gridx = 2;
        panel_3.add(maxRollinLabel, gridBagConstraints_9);

        maxRollSpinner = new JSpinner();
        final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
        gridBagConstraints_10.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_10.insets = new Insets(0, 4, 0, 0);
        gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_10.gridy = 3;
        gridBagConstraints_10.gridx = 2;
        panel_3.add(maxRollSpinner, gridBagConstraints_10);

        final JPanel panel_4 = new JPanel();
//        if (!isMac) { // we need to use vertical label
//            VTextIcon icon = new VTextIcon(tabbedPane, "Springs");
//            tabbedPane.addTab(null, icon, panel_4, null);
//        } else
            tabbedPane.addTab("Springs", null, panel_4, null);

        final JPanel panel_5 = new JPanel();
//            if (!isMac) { // we need to use vertical label
//                VTextIcon icon = new VTextIcon(tabbedPane, "Movement");
//                tabbedPane.addTab(null, icon, panel_5, null);
//            } else
            tabbedPane.addTab("Movement", null, panel_5, null);

        final JPanel panel_6 = new JPanel();
//          if (!isMac) { // we need to use vertical label
//              VTextIcon icon = new VTextIcon(tabbedPane, "Examples");
//              tabbedPane.addTab(null, icon, panel_6, null);
//          } else
          tabbedPane.addTab("Examples", null, panel_6, null);

      final JPanel panel_7 = new JPanel();
//        if (!isMac) { // we need to use vertical label
//            VTextIcon icon = new VTextIcon(tabbedPane, "File");
//            tabbedPane.addTab(null, icon, panel_7, null);
//        } else
        tabbedPane.addTab("File", null, panel_7, null);
            
        final JPanel statPanel = new JPanel();
        final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
        gridBagConstraints_11.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints_11.gridwidth = 2;
        gridBagConstraints_11.weightx = 1.0;
        gridBagConstraints_11.fill = GridBagConstraints.BOTH;
        gridBagConstraints_11.gridx = 0;
        gridBagConstraints_11.gridy = 1;
        panel.add(statPanel, gridBagConstraints_11);
        final FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(2);
        flowLayout.setVgap(2);
        flowLayout.setAlignment(FlowLayout.LEFT);
        statPanel.setLayout(flowLayout);

        final JLabel hitescToLabel = new JLabel();
        hitescToLabel.setText("Click and drag in the Test area to control.  WASD controls target.");
        statPanel.add(hitescToLabel);
        
        final JScrollPane scrollPane = new JScrollPane(panel);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // center the frame
        setLocationRelativeTo(null);
        // show frame
        setVisible(true);

        while (glCanvas == null || impl.startTime == 0) ;

        // MAKE SURE YOU REPAINT SOMEHOW OR YOU WON'T SEE THE UPDATES...
        new Thread() {
            { setDaemon(true); }
            public void run() {
                while (true) {
                    glCanvas.repaint();
                    yield();
                }
            }
        }.start();

//        // event test - does print event only
//        MouseInput.get().addListener( new MouseInputListener() {
//            public void onButton( int button, boolean pressed, int x, int y ) {
//                System.out.println( "button " + button + " " + (pressed?"pressed":"released") );
//            }
//
//            public void onWheel( int wheelDelta, int x, int y ) {
//                System.out.println( "wheel scrolled " + wheelDelta );
//            }
//
//            public void onMove( int xDelta, int yDelta, int newX, int newY ) {
//                System.out.println( "mouse moved by ("+xDelta+";"+yDelta+")");
//            }
//        } );

        // force a resize to ensure proper canvas size.
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight()+1);
        glCanvas.setSize(glCanvas.getWidth(), glCanvas.getHeight()-1);
    }
    
    public Dimension getMinimumSize() {
        return MIN_DIMENSION;
    }

    protected Canvas getGlCanvas() {
        if (glCanvas == null) {

            // -------------GL STUFF------------------

            // make the canvas:
            glCanvas = DisplaySystem.getDisplaySystem("LWJGL").createCanvas(width, height);

            // add a listener... if window is resized, we can do something about it.
            glCanvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    impl.resizeCanvas(glCanvas.getSize().width, glCanvas.getSize().height);
                }
            });
            glCanvas.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent arg0) {
                    ((AWTKeyInput)KeyInput.get()).setEnabled(true);
                    ((AWTMouseInput)MouseInput.get()).setEnabled(true);
                }

                public void focusLost(FocusEvent arg0) {
                    ((AWTKeyInput)KeyInput.get()).setEnabled(false);
                    ((AWTMouseInput)MouseInput.get()).setEnabled(false);
                }
                
            });
            
            // We are going to use jme's Input systems, so enable updating.
            ((JMECanvas)glCanvas).setUpdateInput(true);

            KeyInput.setProvider("AWT");
            ((AWTKeyInput)KeyInput.get()).setEnabled(false);
            KeyListener kl = (KeyListener)KeyInput.get();
            
            glCanvas.addKeyListener(kl);

            MouseInput.setProvider("AWT");
            ((AWTMouseInput)MouseInput.get()).setEnabled(false);
            ((AWTMouseInput)MouseInput.get()).setDragOnly(true);
            ((AWTMouseInput)MouseInput.get()).setRelativeDelta(glCanvas);
            glCanvas.addMouseListener((MouseListener)MouseInput.get());
            glCanvas.addMouseWheelListener((MouseWheelListener)MouseInput.get());
            glCanvas.addMouseMotionListener((MouseMotionListener)MouseInput.get());

            // Important!  Here is where we add the guts to the canvas:
            impl = new ControlImplementor(width, height);
            ((JMECanvas) glCanvas).setImplementor(impl);
            
            // -----------END OF GL STUFF-------------
        }
        return glCanvas;
    }

}
