/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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

package jme.system;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.lwjgl.Display;
import org.lwjgl.DisplayMode;

import jme.exception.MonkeyRuntimeException;

/**
 * <code>PropertiesDialog</code> provides an interface to make use of the
 * <code>PropertiesIO</code> class. It provides a simple clean method of
 * creating a properties file. The <code>PropertiesIO</code> is still created
 * by the client application, and passed during construction.
 * 
 * @see jme.system.PropertiesIO
 * @author Mark Powell
 * @version 0.1.0
 */
public class PropertiesDialog extends JDialog {

    //connection to properties file.
    private PropertiesIO source = null;
    
    //Title Image
    String imageFile = null;
    
    //UI components
    private JCheckBox fullscreenBox = null;
    private JComboBox displayCombo = null;
    
    //flag to denote if the dialog has finished being used.
    private boolean done = false;

    /**
     * Constructor builds the interface for the <code>PropertiesDialog</code>.
     * 
     * @param source the <code>PropertiesIO</code> object to use for working
     *      with the properties file.
     * @param imageFile the file to use as the title of the dialog. Null will
     *      result in now picture being used.
     * 
     * @throws MonkeyRuntimeException if the source is null.
     */
    public PropertiesDialog(PropertiesIO source, String imageFile) {

        if (null == source) {
            throw new MonkeyRuntimeException("PropertyIO source cannot be null");
        }
        
        this.source = source;
        this.imageFile = imageFile;
        
        init();
    }
    
    /**
     * <code>isDone</code> returns the status of the dialog. If true, the
     * application has either been used to change/set the properties file, or
     * has been closed.
     * 
     * @return true if the dialog is closed, false if it is still up.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * <code>init</code> creates the components to use the dialog.
     *
     */
    private void init() {
        this.setTitle("Select Display Settings");
        
        //The panels...
        JPanel mainPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel optionsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        //The buttons...  
        JButton ok = new JButton("Ok");
        JButton cancel = new JButton("Cancel");
        
        mainPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new BorderLayout());

        centerPanel.add(
            new JLabel(new ImageIcon(imageFile)),
            BorderLayout.NORTH);

        displayCombo = setUpChooser();
        optionsPanel.add(displayCombo);

        fullscreenBox = new JCheckBox("Fullscreen?");
        fullscreenBox.setSelected(source.getFullscreen());
        optionsPanel.add(fullscreenBox);

        centerPanel.add(optionsPanel, BorderLayout.SOUTH);

        //Set the button action listeners. Cancel disposes without saving,
        //ok saves.
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCurrentSelection();
                dispose();
                done = true;
            }
        });

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);

        pack();

        int x, y;
        x =
            (Toolkit.getDefaultToolkit().getScreenSize().width
                - this.getWidth())
                / 2;
        y =
            (Toolkit.getDefaultToolkit().getScreenSize().height
                - this.getHeight())
                / 2;
        this.setLocation(x, y);

        show();
    }

    /**
     * <code>saveCurrentSelection</code> saves the current selection as a
     * properties.cfg file.
     *
     */
    private void saveCurrentSelection() {
        String display = (String)displayCombo.getSelectedItem();
        int width =
            Integer.parseInt(display.substring(0, display.indexOf("x")));
        display = display.substring(display.indexOf("x") + 1);
        int height =
            Integer.parseInt(display.substring(0, display.indexOf("x")));
        display = display.substring(display.indexOf("x") + 1);
        int depth = Integer.parseInt(display.substring(0, display.indexOf("x")));
        display = display.substring(display.indexOf("x") + 1);
        int freq = Integer.parseInt(display);
        boolean fullscreen = fullscreenBox.isSelected();

        //use the propertiesio class to save it.
        source.save(width, height, depth, freq, fullscreen);
    }

    /**
     * <code>setUpChooser</code> retrieves all available display modes and
     * places them in a <code>JComboBox</code>.
     * 
     * @return the combo box of display modes.
     */
    private JComboBox setUpChooser() {
        TreeSet uniqueModes = new TreeSet();
        DisplayMode[] modes = Display.getAvailableDisplayModes();
        JComboBox comboBox = new JComboBox();

        String displayString;
        for (int i = 0; i < modes.length; i++) {
            displayString =
                modes[i].width + "x" + modes[i].height + "x" + modes[i].bpp + 
                "x" + modes[i].freq;
            if (!uniqueModes.contains(displayString)) {
                uniqueModes.add(displayString);
            }
        }

        Iterator i = uniqueModes.iterator();
        while (i.hasNext()) {
            comboBox.addItem(i.next());
        }
        
        comboBox.setSelectedItem(
            source.getWidth()
                + "x"
                + source.getHeight()
                + "x"
                + source.getDepth()
                + "x"
                + source.getFreq());

        return comboBox;
    }
}
