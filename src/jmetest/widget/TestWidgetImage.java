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
package jmetest.widget;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;

import com.jme.app.BaseGame;
import com.jme.image.Image;
import com.jme.input.AbstractInputHandler;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.image.WidgetImage;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.slider.WidgetHSlider;
import com.jme.widget.slider.WidgetVSlider;
import com.jme.widget.text.WidgetText;

/**
 * @author Mike Kienenberger
 */
public class TestWidgetImage extends BaseGame {

    class TestFrame extends WidgetAbstractFrame implements Observer {

        private WidgetImage northImageWidget = null;
        private WidgetHSlider northSlider = null;
        
        private WidgetImage westImageWidget = null;
        private WidgetVSlider westSlider = null;
        
        private WidgetPanel createNWPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage northwestImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_NORTHWEST, WidgetImage.SCALE_MODE_RELATIVE);
            northwestImageWidget.setHorizontalScale(0.5f);
            northwestImageWidget.setVerticalScale(0.5f);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(northwestImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE @ 50%");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createNorthPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            northImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_RELATIVE);
            northImageWidget.setHorizontalScale(0.5f);
            northImageWidget.setVerticalScale(1.0f);
            
            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(northImageWidget);

            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            northSlider = new WidgetHSlider(WidgetOrientationType.DOWN);
            northSlider.setBorder(new WidgetBorder(1, 1, 1, 1));
            northSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            northSlider.setMinimum(0.1f);
            northSlider.setValue(0.5f);
            northSlider.setMaximum(1.0f);
            northSlider.addValueChangeObserver(this);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(northSlider, WidgetBorderLayoutConstraint.SOUTH);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            update(null, northSlider);

            return mainPanel;
        }
        
        private WidgetPanel createNEPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage northeastImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_NONE);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(northeastImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_NONE");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createWestPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            westImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_ABSOLUTE);
            westImageWidget.setHorizontalScale(1.0f);
            westImageWidget.setVerticalScale(0.5f);
            
            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(westImageWidget);

            WidgetText label = new WidgetText("SCALE_MODE_ABSOLUTE");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            westSlider = new WidgetVSlider(WidgetOrientationType.LEFT);
            westSlider.setBorder(new WidgetBorder(1, 1, 1, 1));
            westSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            westSlider.setMinimum(0.01f);
            westSlider.setValue(0.5f);
            westSlider.setMaximum(1.0f);
            westSlider.addValueChangeObserver(this);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(westSlider, WidgetBorderLayoutConstraint.WEST);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            update(null, westSlider);

            return mainPanel;
        }
        
        private WidgetPanel createCenterPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage centerImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_CENTER, WidgetImage.SCALE_MODE_RELATIVE);
            centerImageWidget.setHorizontalScale(0.5f);
            centerImageWidget.setVerticalScale(0.5f);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(centerImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE @ 50%");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createEastPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage eastImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_EAST, WidgetImage.SCALE_MODE_RELATIVE);
            eastImageWidget.setHorizontalScale(0.5f);
            eastImageWidget.setVerticalScale(0.5f);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(eastImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE @ 50%");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createSWPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage southwestImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_SOUTHWEST, WidgetImage.SCALE_MODE_RELATIVE);
            southwestImageWidget.setHorizontalScale(0.5f);
            southwestImageWidget.setVerticalScale(0.5f);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(southwestImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE @ 50%");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createSouthPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage southImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_SOUTH, WidgetImage.SCALE_MODE_RELATIVE);
            southImageWidget.setHorizontalScale(0.5f);
            southImageWidget.setVerticalScale(0.5f);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(southImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_RELATIVE @ 50%");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        private WidgetPanel createSEPanel(java.awt.Image anImage)
        {
            Image jmeImage = TextureManager.loadImage(anImage, true);
            WidgetImage southeastImageWidget = new WidgetImage(jmeImage, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_SIZE_TO_FIT);

            WidgetPanel imagePanel = new WidgetPanel();
            imagePanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
            imagePanel.setLayout(new WidgetGridLayout(1, 1));
            imagePanel.add(southeastImageWidget);
            
            WidgetText label = new WidgetText("SCALE_MODE_SIZE_TO_FIT");
            label.setAlignment(WidgetAlignmentType.ALIGN_CENTER);

            WidgetPanel mainPanel = new WidgetPanel();
            mainPanel.setLayout(new WidgetBorderLayout());
            mainPanel.add(imagePanel, WidgetBorderLayoutConstraint.CENTER);
            mainPanel.add(label, WidgetBorderLayoutConstraint.NORTH);
            
            return mainPanel;
        }
        
        TestFrame(AbstractInputHandler ic) {
            super(ic);

            setLayout(new WidgetGridLayout(3, 3));

            java.awt.Image monkeyImageData = null;
            try
            {
                URL monkeyURL = TestWidgetImage.class.getClassLoader().getResource("jmetest/data/images/monkey.jpg");
                monkeyImageData = ImageIO.read(monkeyURL);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            add(createNWPanel(monkeyImageData));
            add(createNorthPanel(monkeyImageData));
            add(createNEPanel(monkeyImageData));
            add(createWestPanel(monkeyImageData));
            add(createCenterPanel(monkeyImageData));
            add(createEastPanel(monkeyImageData));
            add(createSWPanel(monkeyImageData));
            add(createSouthPanel(monkeyImageData));
            add(createSEPanel(monkeyImageData));
            
            doLayout();

            update(null, northSlider);
            update(null, westSlider);
        }

        /** <code>update</code>
         * @param o
         * @param arg
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        public void update(Observable o, Object obj) {

            if (obj == northSlider)
            {
                northImageWidget.setHorizontalScale((float)northSlider.getValue());
            }
            else if (obj == westSlider)
            {
                westImageWidget.setVerticalScale((float)westSlider.getValue());
            }
        }

    }

    private TestFrame frame;
    private Node scene;
    private Camera cam;
    private AbstractInputHandler input;

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        frame.handleInput();
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(frame);
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        try {

            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(
                properties.getWidth(),
                properties.getHeight(),
                properties.getDepth(),
                properties.getFreq(),
                properties.getFullscreen());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA background = new ColorRGBA(.5f, .5f, .5f, 1);
        display.getRenderer().setBackgroundColor(background);

        cam = display.getRenderer().getCamera(display.getWidth(), display.getHeight());

        display.getRenderer().setCamera(cam);

        display.setTitle("GUI Image Test");

        input = new WidgetMouseTestControllerBasic(this);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        scene = new Node("");

        frame = new TestFrame(input);

        frame.updateGeometricState(0.0f, true);

        scene.attachChild(frame);

        cam.update();

        scene.updateGeometricState(0.0f, true);
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {
        WidgetAbstractFrame.destroy();
        frame.init();
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {
        WidgetAbstractFrame.destroy();
    }

    public static void main(String[] args) {
        TestWidgetImage app = new TestWidgetImage();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        //app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

}
