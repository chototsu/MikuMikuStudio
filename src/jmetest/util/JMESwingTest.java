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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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

import com.jme.app.SimpleHeadlessApp;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.HeadlessDelegate;
import com.jme.util.JMEComponent;
import com.jme.util.TextureManager;

/**
 * <code>JMESwingTest</code> is a test demoing the JMEComponent and
 * HeadlessDelegate integration classes allowing jME generated
 * graphics to be displayed in a AWT/Swing interface.
 *
 * @author Joshua Slack
 * @version $Id: JMESwingTest.java,v 1.4 2005-01-03 19:00:15 renanse Exp $
 */

public class JMESwingTest extends SimpleHeadlessApp {

	// Items for scene
	private Box box;
	private Quaternion rotQuat;
	private float angle = 0;
	private Vector3f axis;

	// Swing frame
	private SwingFrame frame;

	public JMESwingTest() {
		frame = new SwingFrame();
		frame.validate();

		//Center the window
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
	}


	/**
	 * Main Entry point...
	 * @param args String[]
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		JMESwingTest app = new JMESwingTest();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	float lastCopied = 0;
	public boolean added = false;
	protected void simpleUpdate() {
			// Code for rotating the box...  no surprises here.
			if (tpf < 1) {
				angle = angle + (tpf * 25);
				if (angle > 360) {
					angle = 0;
				}
			}
			rotQuat.fromAngleAxis(angle * FastMath.DEG_TO_RAD, axis);
			box.setLocalRotation(rotQuat);

			// ************* NEW FOR HEADLESS/SWING *************
			// New code added to relegate number of times
			// the scene is copied to the JMEComponent.  Only copy if at least
			// 1/100th of a second has passed.  Not necessary, but definitely
			// recommended.  Tweak "at least" value as you wish.
			//
			// Note: This is a good place to do the copy because it is right before
			// the render method which means the previous render is definitely
			// done and flushed and the new render has not been called to clear the
			// buffers.
			lastCopied += tpf;
			if (lastCopied > .01f) {
				HeadlessDelegate.copyContents(frame.comp);
				lastCopied = 0;
			}
			// ************* /NEW FOR HEADLESS/SWING *************
		}

		protected void simpleRender() {
			// ************* NEW FOR HEADLESS/SWING *************
			// tell the delegate we got new contents from GL and to hold back
			// from rendering until set back to true.
			HeadlessDelegate.setNeedsRender(frame.comp, false);
			// ************* /NEW FOR HEADLESS/SWING *************
		}

		protected void simpleInitGame() {
			// Normal Scene setup stuff...
			rotQuat = new Quaternion();
			axis = new Vector3f(1, 1, 0.5f);
			display.setTitle("Vertex Colors");
			lightState.setEnabled(false);
			input.clearMouseActions();

			Vector3f max = new Vector3f(5, 5, 5);
			Vector3f min = new Vector3f( -5, -5, -5);

			box = new Box("Box", min, max);
			box.setModelBound(new BoundingBox());
			box.updateModelBound();
			box.setLocalTranslation(new Vector3f(0, 0, -10));
			rootNode.attachChild(box);

			ColorRGBA[] colors = new ColorRGBA[24];
			for (int i = 0; i < 24; i++) {
				colors[i] = ColorRGBA.randomColor();
			}
			box.setColors(colors);

			TextureState ts = display.getRenderer().createTextureState();
			ts.setEnabled(true);
			ts.setTexture(
					TextureManager.loadTexture(
					JMESwingTest.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.jpg"),
					Texture.MM_LINEAR,
					Texture.FM_LINEAR,
					true));

			rootNode.setRenderState(ts);

			// ************* NEW FOR HEADLESS/SWING *************
			// New code setting up the JMEComponent and adding it to the frame.
			frame.comp = new JMEComponent(display.getWidth(), display.getHeight());
			frame.glPanel.add(frame.comp, BorderLayout.CENTER);

			// New code setting the options on the JMEComponent...
			// Scale the GL image to the size of the component
			frame.comp.setScaled(true);
			// Refresh the screen image every 50 ms.  (I like to use twice the value
			// in the lastCopied at least setting.)
			frame.comp.setRefreshRate(50);
			// Set the background color of the component.
			frame.comp.setBackground(java.awt.Color.black);

			// Now add our JMEComponent to the HeadlessDelegate so it
			// can be properly tracked and updated as needed.
			HeadlessDelegate.add(display.getRenderer(), frame.comp);

			// Now validate frame to get the component properly sized and showing.
			frame.validate();
			// ************* /NEW FOR HEADLESS/SWING *************
		}
	}


// **************** SWING FRAME ****************


// Our custom Swing frame...  Nothing really special here.
	class SwingFrame extends JFrame {
		JPanel contentPane;
		JPanel mainPanel = new JPanel();
		JMEComponent comp = null;
		JButton coolButton = new JButton();
		JButton uncoolButton = new JButton();
		JPanel spPanel = new JPanel();
		JPanel glPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		JTree jTree1 = new JTree();
		JCheckBox scaleBox = new JCheckBox("Scale GL Image");
		JPanel colorPanel = new JPanel();
		JLabel colorLabel = new JLabel("BG Color:");

		//Construct the frame
		public SwingFrame() {
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			init();
			pack();
		}

		//Component initialization
		private void init() {
			contentPane = (JPanel) this.getContentPane();
			contentPane.setLayout(new BorderLayout());

			mainPanel.setLayout(new GridBagLayout());

			setTitle("JME - SWING INTEGRATION TEST");

			coolButton.setText("Cool Button");
			uncoolButton.setText("Uncool Button");

			colorPanel.setBackground(java.awt.Color.black);
			colorPanel.setToolTipText("Click here to change Panel BG color.");
			colorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
			colorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					java.awt.Color color = JColorChooser.showDialog(SwingFrame.this, "Choose new background color:",
																								 colorPanel.getBackground());
					if (color == null) return;
					colorPanel.setBackground(color);
					comp.setBackground(color);
				}
			});

			scaleBox.setOpaque(false);
			scaleBox.setSelected(true);
			scaleBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (comp != null) comp.setScaled(scaleBox.isSelected());
				}
			});

			spPanel.setLayout(new BorderLayout());
			contentPane.add(mainPanel, BorderLayout.WEST);
			mainPanel.add(scaleBox,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
					,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
			mainPanel.add(colorLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
					,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
			mainPanel.add(colorPanel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
					,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 25, 25));
			mainPanel.add(coolButton,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
					,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
			mainPanel.add(uncoolButton,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
					,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
			mainPanel.add(spPanel,       new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0
					,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 0, 5), 0, 0));
			spPanel.add(scrollPane,  BorderLayout.CENTER);
			scrollPane.setViewportView(jTree1);
			glPanel.setLayout(new BorderLayout());
			glPanel.setPreferredSize(new Dimension(640,480));
			contentPane.add(glPanel, BorderLayout.CENTER);
		}

		//Overridden so we can exit when window is closed
		protected void processWindowEvent(WindowEvent e) {
			super.processWindowEvent(e);
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				System.exit(0);
			}
		}
	}
