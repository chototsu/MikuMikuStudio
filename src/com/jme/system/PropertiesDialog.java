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

package com.jme.system;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.jme.util.LoggingSystem;

/**
 * <code>PropertiesDialog</code> provides an interface to make use of the
 * <code>PropertiesIO</code> class. It provides a simple clean method of
 * creating a properties file. The <code>PropertiesIO</code> is still created
 * by the client application, and passed during construction.
 * 
 * @see com.jme.system.PropertiesIO
 * @author Mark Powell
 * @version $Id: PropertiesDialog.java,v 1.4 2003-12-01 13:18:58 mojomonkey Exp $
 */
public class PropertiesDialog extends JDialog {

    //connection to properties file.
    private PropertiesIO source = null;

    //Title Image
    String imageFile = null;

    //UI components
    private JCheckBox fullscreenBox = null;
    private JComboBox displayResCombo = null;
    private JComboBox displayFreqCombo = null;
    private JComboBox rendererCombo = null;

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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Could not set" +                " native look and feel.");
        }

        if (null == source) {
            throw new JmeException("PropertyIO source cannot be null");
        }

        this.source = source;
        this.imageFile = imageFile;

        this.addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
               dispose();
               System.exit(0);
           } 
        });

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
     * 
     * <code>setImage</code> sets the background image of the dialog.
     * @param image the image file.
     */
    public void setImage(String image) {
        imageFile = image;
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

        displayResCombo = setUpResolutionChooser();
        displayFreqCombo = setUpFreqChooser();
        optionsPanel.add(displayResCombo);
        optionsPanel.add(displayFreqCombo);

        fullscreenBox = new JCheckBox("Fullscreen?");
        fullscreenBox.setSelected(source.getFullscreen());
        rendererCombo = setUpRendererChooser();
        optionsPanel.add(fullscreenBox);
        optionsPanel.add(rendererCombo);

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
        toFront();
    }

    /**
     * <code>saveCurrentSelection</code> saves the current selection as a
     * properties.cfg file.
     *
     */
    private void saveCurrentSelection() {
        String display = (String) displayResCombo.getSelectedItem();
        int width =
            Integer.parseInt(display.substring(0, display.indexOf("x")));
        display = display.substring(display.indexOf("x") + 1);
        int height =
            Integer.parseInt(display.substring(0, display.indexOf("x")));
        display = display.substring(display.indexOf("x") + 1);
        int depth =
            Integer.parseInt(display.substring(0));
        String freqString = (String) displayFreqCombo.getSelectedItem();
        int freq =
            Integer.parseInt(freqString.substring(0, freqString.indexOf(" ")));
        boolean fullscreen = fullscreenBox.isSelected();
        String renderer = (String)rendererCombo.getSelectedItem();

        //use the propertiesio class to save it.
        source.save(width, height, depth, freq, fullscreen, renderer);
    }

    /**
     * <code>setUpChooser</code> retrieves all available display modes and
     * places them in a <code>JComboBox</code>.
     * 
     * @return the combo box of display modes.
     */
    private JComboBox setUpResolutionChooser() {
        String[] modes =
            { "640x480x16", "800x600x16", "1024x768x16", "1280x1024x16", "1600x1200x16",
                "640x480x32", "800x600x32", "1024x768x32", "1280x1024x32", "1600x1200x32"   };
        JComboBox resolutionBox = new JComboBox(modes);

        resolutionBox.setSelectedItem(
            source.getWidth()
                + "x"
                + source.getHeight()
                + "x"
                + source.getDepth());

        return resolutionBox;
    }

    /**
     * 
     * <code>setUpFreqChooser</code> sets available display frequencys.
     * @return the combo box that contains the display frequencys.
     */
    private JComboBox setUpFreqChooser() {
        String modes[] = {"0 Hz (Linux)", "60 Hz", "70 Hz", "75 Hz", "80 Hz" };
        JComboBox freqBox = new JComboBox(modes);
        freqBox.setSelectedItem(source.getFreq() + " Hz");
        return freqBox;
    }
    
    /**
     * 
     * <code>setUpRendererChooser</code> sets the list of available 
     * renderers. This is obtained from the <code>DisplaySystem</code>
     * class.
     * @return the list of renderers.
     */
    private JComboBox setUpRendererChooser() {
        String modes[] = DisplaySystem.rendererNames;
        JComboBox nameBox = new JComboBox(modes);
        nameBox.setSelectedItem(source.getRenderer());
        return nameBox;
    }
}
