/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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

package jmetest.util;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.awt.JMECanvas;
import com.jme.util.awt.JMECanvasImplementor;
import com.jme.util.awt.SimpleCanvasImpl;

/**
 * <code>JMESwingTest</code> is a test demoing the JMEComponent and
 * HeadlessDelegate integration classes allowing jME generated graphics to be
 * displayed in a AWT/Swing interface.
 * 
 * Note the Repaint thread and how you grab a canvas and add an implementor to it.
 * 
 * @author Joshua Slack
 * @version $Id: JMESwingTest.java,v 1.9 2005-07-28 16:59:26 renanse Exp $
 */

public class JMESwingTest {

    int width = 640, height = 480;

    // Swing frame
    private SwingFrame frame;

    public JMESwingTest() {
        frame = new SwingFrame();
        // center the frame
        frame.setLocationRelativeTo(null);
        // show frame
        frame.setVisible(true);
    }

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
        JMESwingTest app = new JMESwingTest();
    }

    // **************** SWING FRAME ****************

    // Our custom Swing frame... Nothing really special here.
    class SwingFrame extends JFrame {
        private static final long serialVersionUID = 1L;

        JPanel contentPane;
        JPanel mainPanel = new JPanel();
        Canvas comp = null;
        JButton coolButton = new JButton();
        JButton uncoolButton = new JButton();
        JPanel spPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JTree jTree1 = new JTree();
        JCheckBox scaleBox = new JCheckBox("Scale GL Image");
        JPanel colorPanel = new JPanel();
        JLabel colorLabel = new JLabel("BG Color:");
        JMECanvasImplementor impl;

        // Construct the frame
        public SwingFrame() {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

            init();
            pack();


            // MAKE SURE YOU REPAINT SOMEHOW OR YOU WON'T SEE THE UPDATES...
            new Thread() {
                { setDaemon(true); }
                public void run() {
                    while (true) {
                        comp.repaint();
                        yield();
                    }
                }
            }.start();

            
        }

        // Component initialization
        private void init() {
            contentPane = (JPanel) this.getContentPane();
            contentPane.setLayout(new BorderLayout());

            mainPanel.setLayout(new GridBagLayout());

            setTitle("JME - SWING INTEGRATION TEST");

            // -------------GL STUFF------------------

            // make the canvas:
            comp = DisplaySystem.getDisplaySystem("lwjgl").createCanvas(width, height);

            // add a listener... if window is resized, we can do something about it.
            comp.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });

            // Important!  Here is where we add the guts to the panel:
            impl = new MyImplementor(width, height);
            ((JMECanvas) comp).setImplementor(impl);
            
            // -----------END OF GL STUFF-------------

            coolButton.setText("Cool Button");
            uncoolButton.setText("Uncool Button");

            colorPanel.setBackground(java.awt.Color.black);
            colorPanel.setToolTipText("Click here to change Panel BG color.");
            colorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            colorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    java.awt.Color color = JColorChooser.showDialog(
                            SwingFrame.this, "Choose new background color:",
                            colorPanel.getBackground());
                    if (color == null)
                        return;
                    colorPanel.setBackground(color);
                    comp.setBackground(color);
                }
            });

            scaleBox.setOpaque(false);
            scaleBox.setSelected(true);
            scaleBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (comp != null)
                        doResize();
                }
            });

            spPanel.setLayout(new BorderLayout());
            contentPane.add(mainPanel, BorderLayout.WEST);
            mainPanel.add(scaleBox,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(colorLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(colorPanel, new GridBagConstraints(0, 2, 1, 1, 0.0,
                    0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(5, 5, 0, 5), 25, 25));
            mainPanel.add(coolButton,
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(uncoolButton,
                    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(spPanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(5, 5, 0, 5), 0, 0));
            spPanel.add(scrollPane, BorderLayout.CENTER);
            
            scrollPane.setViewportView(jTree1);
            comp.setBounds(0, 0, width, height);
            contentPane.add(comp, BorderLayout.CENTER);
        }

        protected void doResize() {
            if (scaleBox != null && scaleBox.isSelected()) {
                impl.resizeCanvas(comp.getWidth(), comp.getHeight());
            } else {
                impl.resizeCanvas(width, height);
            }
        }

        // Overridden so we can exit when window is closed
        protected void processWindowEvent(WindowEvent e) {
            super.processWindowEvent(e);
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                System.exit(0);
            }
        }
    }

    
    // IMPLEMENTING THE SCENE:
    
    class MyImplementor extends SimpleCanvasImpl {

        private Quaternion rotQuat;
        private float angle = 0;
        private Vector3f axis;
        private Box box;
		long startTime = 0;
		long fps = 0;			

        public MyImplementor(int width, int height) {
            super(width, height);
        }

        public void simpleSetup() {

            // Normal Scene setup stuff...
            rotQuat = new Quaternion();
            axis = new Vector3f(1, 1, 0.5f);
            axis.normalizeLocal();

            Vector3f max = new Vector3f(5, 5, 5);
            Vector3f min = new Vector3f(-5, -5, -5);

            box = new Box("Box", min, max);
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            box.setLocalTranslation(new Vector3f(0, 0, -10));
            box.setRenderQueueMode(Renderer.QUEUE_SKIP);
            rootNode.attachChild(box);

            ColorRGBA[] colors = new ColorRGBA[24];
            for (int i = 0; i < 24; i++) {
                colors[i] = ColorRGBA.randomColor();
            }
            box.setColors(colors);

            TextureState ts = renderer.createTextureState();
            ts.setEnabled(true);
            ts.setTexture(TextureManager.loadTexture(JMESwingTest.class
                    .getClassLoader().getResource(
                            "jmetest/data/images/Monkey.jpg"),
                    Texture.MM_LINEAR, Texture.FM_LINEAR));

            rootNode.setRenderState(ts);
            startTime = System.currentTimeMillis() + 5000;
        };

        public void simpleUpdate() {
            // Code for rotating the box... no surprises here.
            if (tpf < 1) {
                angle = angle + (tpf * 25);
                if (angle > 360) {
                    angle = 0;
                }
            }
            rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
            box.setLocalRotation(rotQuat);
            
			if (startTime > System.currentTimeMillis()) {
				fps++;
			} else {
				long timeUsed = 5000 + (startTime - System.currentTimeMillis());
				startTime = System.currentTimeMillis() + 5000;
				System.out.println(fps + " frames in " + (float) (timeUsed / 1000f) + " seconds = "
						+ (fps / (timeUsed / 1000f))+" FPS (average)");
				fps = 0;
			}				
        }
    }
}