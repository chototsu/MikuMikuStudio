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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import com.jme.app.BaseGame;
import com.jme.image.Image;
import com.jme.input.AbstractInputHandler;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.image.WidgetAnimatedImage;
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
 * <code>TestWidgetAnimatedImage</code>
 * @author Mike Kienenberger
 *
 * Demonstration of <code>WidgetAnimatedImage</code> with all variations of scaling and alignment.
 *
 * @since 0.6
 * @version $$Id: TestWidgetAnimatedImage.java,v 1.2 2004-04-29 13:30:34 mojomonkey Exp $$
 */
public class TestWidgetAnimatedImage extends BaseGame {

    class TestFrame extends WidgetAbstractFrame implements Observer {

        private WidgetHSlider northSlider = null;
        
        private WidgetVSlider westSlider = null;
        
        private WidgetAnimatedImage northwestImageWidget = null;
        private WidgetAnimatedImage northImageWidget = null;
        private WidgetAnimatedImage northeastImageWidget = null;
        private WidgetAnimatedImage westImageWidget = null;
        private WidgetAnimatedImage centerImageWidget = null;
        private WidgetAnimatedImage eastImageWidget = null;
        private WidgetAnimatedImage southwestImageWidget = null;
        private WidgetAnimatedImage southImageWidget = null;
        private WidgetAnimatedImage southeastImageWidget = null;

        private WidgetPanel createNWPanel(List anImageList)
        {
            northwestImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_NORTHWEST, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createNorthPanel(List anImageList)
        {
            northImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createNEPanel(List anImageList)
        {
            northeastImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_NONE);

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
        
        private WidgetPanel createWestPanel(List anImageList)
        {
            westImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_ABSOLUTE);
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
        
        private WidgetPanel createCenterPanel(List anImageList)
        {
            centerImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_CENTER, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createEastPanel(List anImageList)
        {
            eastImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_EAST, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createSWPanel(List anImageList)
        {
            southwestImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_SOUTHWEST, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createSouthPanel(List anImageList)
        {
            southImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_SOUTH, WidgetImage.SCALE_MODE_RELATIVE);
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
        
        private WidgetPanel createSEPanel(List anImageList)
        {
            southeastImageWidget = new WidgetAnimatedImage(anImageList, WidgetAlignmentType.ALIGN_NONE, WidgetImage.SCALE_MODE_SIZE_TO_FIT);

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

            int imageCount = 8;
            java.awt.Image awtImageArray[] = new java.awt.Image[imageCount];

            try
            {
                URL imageURL = TestWidgetAnimatedImage.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg");
                java.awt.Image awtImage = ImageIO.read(imageURL);
                
                awtImageArray[0] = awtImage;
                awtImageArray[1] = rotate(awtImageArray[0], 45);
                awtImageArray[2] = rotate(awtImageArray[1], 45);
                awtImageArray[3] = rotate(awtImageArray[2], 45);
                awtImageArray[4] = rotate(awtImageArray[3], 45);
                awtImageArray[5] = rotate(awtImageArray[4], 45);
                awtImageArray[6] = rotate(awtImageArray[5], 45);
                awtImageArray[7] = rotate(awtImageArray[6], 45);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            
            List imageList = new ArrayList();
            for (int imageIndex = 0; imageIndex < imageCount; ++imageIndex)
            {
                Image jmeImage = TextureManager.loadImage(awtImageArray[imageIndex], true);
                imageList.add(jmeImage);
            }

            List panelImageList[] = new List[9];
            for (int panelIndex = 0; panelIndex < 9; ++panelIndex)
            {
                panelImageList[panelIndex] = imageList;
            }

            add(createNWPanel(panelImageList[0]));
            add(createNorthPanel(panelImageList[1]));
            add(createNEPanel(panelImageList[2]));
            add(createWestPanel(panelImageList[3]));
            add(createCenterPanel(panelImageList[4]));
            add(createEastPanel(panelImageList[5]));
            add(createSWPanel(panelImageList[6]));
            add(createSouthPanel(panelImageList[7]));
            add(createSEPanel(panelImageList[8]));
            
            doLayout();

            update(null, northSlider);
            update(null, westSlider);
        }

        private java.awt.Image rotate(java.awt.Image image, int degreesToRotate) {
            // Obtain the image data.
            BufferedImage tex = null;
            try {

                tex = new BufferedImage(image.getWidth(null),
                        image.getHeight(null),
                        TextureManager.hasAlpha(image) ? BufferedImage.TYPE_4BYTE_ABGR
                                : BufferedImage.TYPE_3BYTE_BGR);

            } catch (IllegalArgumentException e) {
                LoggingSystem.getLogger().log(Level.WARNING,
                        "Problem creating buffered Image: " + e.getMessage());
                return null;
            }

            Graphics2D g = (Graphics2D) tex.getGraphics();
            g.drawImage(image, null, null);
            g.dispose();

            AffineTransform tx = new AffineTransform();
            tx.translate(-(tex.getWidth(null) / 2), -(tex.getHeight(null) / 2));
            tx.rotate(Math.toRadians(degreesToRotate), tex.getWidth(null), tex.getHeight(null));
            tx.translate((tex.getWidth(null) / 2), (tex.getHeight(null) / 2));
            AffineTransformOp op = new AffineTransformOp(tx,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            tex = op.filter(tex, null);
                    
            return tex;
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


        int updateCount = 0;
        /**
         * 
         */
        public void update()
        {
            updateCount++;
            updateCount = updateCount % 9;
            
            if (0 == (updateCount % 1)) if (null != northwestImageWidget)  northwestImageWidget.update();
            if (0 == (updateCount % 2)) if (null != northImageWidget)  northImageWidget.update();
            if (0 == (updateCount % 3)) if (null != northeastImageWidget)  northeastImageWidget.update();
            if (0 == (updateCount % 4)) if (null != westImageWidget)  westImageWidget.update();
            if (0 == (updateCount % 5)) if (null != centerImageWidget)  centerImageWidget.update();
            if (0 == (updateCount % 6)) if (null != eastImageWidget)  eastImageWidget.update();
            if (0 == (updateCount % 7)) if (null != southwestImageWidget)  southwestImageWidget.update();
            if (0 == (updateCount % 8)) if (null != southImageWidget)  southImageWidget.update();
            if (0 == (updateCount % 9)) if (null != southeastImageWidget)  southeastImageWidget.update();
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
        frame.update();
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
        TestWidgetAnimatedImage app = new TestWidgetAnimatedImage();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        //app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

}
