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
package com.jme.widget.panel;

import java.util.Observable;
import java.util.Observer;

import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetExpander;
import com.jme.widget.WidgetInsets;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.layout.WidgetLayoutManager;
import com.jme.widget.scroller.WidgetHScroller;
import com.jme.widget.scroller.WidgetVScroller;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetScrollPanel extends WidgetAbstractContainer implements Observer {

    protected WidgetPanel panel;
    private WidgetVScroller vScroller;
    private WidgetHScroller hScroller;

    private boolean showVScroll = true;
    private boolean showHScroll = true;

    private boolean changingVScroller = false;
    private boolean changingHScroller = false;

    public WidgetScrollPanel() {
        super();

        init();
    }

    public WidgetScrollPanel(int width, int height) {
        super(width, height);

        init();
    }

    private void init() {
        super.setLayout(new WidgetBorderLayout());

        panel = new WidgetPanel();

        panel.setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST));
        super.add(panel, WidgetBorderLayoutConstraint.CENTER);

        vScroller = new WidgetVScroller();
        vScroller.setVisible(false);
        vScroller.addScrollChangeObserver(this);

        hScroller = new WidgetHScroller();
        hScroller.setVisible(false);
        hScroller.addScrollChangeObserver(this);

    }

    public void add(Widget w, Object constraints) {
        panel.add(w, constraints);
    }

    public void add(Widget w) {
        panel.add(w);
    }

    public void remove(int w) {
        panel.remove(w);
    }

    public void remove(Widget w) {
        panel.remove(w);
    }

    public void removeAll() {
        panel.removeAll();
    }

    public void update(Observable o, Object arg) {

        panel.setPanOffset(-hScroller.getOffset(), vScroller.getOffset());

    }

    public void doLayout() {
        if (isVisible() == false)
            return;

        WidgetBoundingRectangle r;

        super.doLayout();

        if (this.showVScroll) {

            if (changingVScroller == false) {

                changingVScroller = true;

                super.remove(vScroller);

                super.add(vScroller, WidgetBorderLayoutConstraint.EAST);
                vScroller.setVisible(true);

                super.doLayout();

                r = panel.getExtents();

                if (r.getHeight() > panel.getHeight()) {

                    vScroller.setRangeExtents(r.getHeight() + vScroller.getButtonSize(), panel.getHeight());

                } else {
                    super.remove(vScroller);
                    vScroller.setVisible(false);
                    vScroller.setOffset(0);
                    super.doLayout();
                }

                changingVScroller = false;
            }
        }

        if (showHScroll) {

            if (changingHScroller == false) {

                changingHScroller = true;

                super.remove(hScroller);

                super.add(hScroller, WidgetBorderLayoutConstraint.SOUTH);

                if (vScroller.isVisible()) {
                    hScroller.setRightButtonExpander(new WidgetExpander(0, 0, 0, hScroller.getButtonSize()));
                } else {
                    hScroller.setRightButtonExpander(new WidgetExpander(0, 0, 0, 0));
                }

                hScroller.setVisible(true);

                super.doLayout();

                r = panel.getExtents();

                if (r.getWidth() > panel.getWidth()) {

                    hScroller.setRangeExtents((int) r.getWidth(), panel.getWidth());

                } else {

                    super.remove(hScroller);
                    hScroller.setVisible(false);
                    hScroller.setOffset(0);
                    super.doLayout();
                }

                changingHScroller = false;
            }
        }

        updateWorldBound();
    }

    public void setLayout(WidgetLayoutManager layout) {
        panel.setLayout(layout);
    }

    public WidgetLayoutManager getLayout() {
        return panel.getLayout();
    }

    /** <code>setInsets</code> 
     * @param insets
     * @see com.jme.widget.WidgetAbstractContainer#setInsets(com.jme.widget.WidgetInsets)
     */
    public void setInsets(WidgetInsets insets) {
        panel.setInsets(insets);
    }

    public boolean isShowVScroll() {
        return showVScroll;
    }

    public void setShowVScroll(boolean b) {
        showVScroll = b;
    }

    public boolean isShowHScroll() {
        return showHScroll;
    }

    public void setShowHScroll(boolean b) {
        showHScroll = b;
    }

    /** <code>initWidgetRenderer</code> 
     * 
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {}

}
