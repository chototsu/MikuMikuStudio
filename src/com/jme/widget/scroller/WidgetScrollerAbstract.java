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
package com.jme.widget.scroller;

import java.util.Observable;
import java.util.Observer;

import com.jme.renderer.Renderer;
import com.jme.widget.WidgetContainerAbstract;
import com.jme.widget.WidgetExpander;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.button.WidgetButtonStateType;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.util.WidgetNotifier;
import com.jme.widget.util.WidgetRepeater;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetScrollerAbstract extends WidgetContainerAbstract implements Observer {

    private WidgetNotifier notifierScrollChange = new WidgetNotifier();

    private WidgetRepeater repeat = new WidgetRepeater();
    
    private boolean incrementing;
    private boolean decrementing;
    
    WidgetScrollerType type;

    WidgetScrollerButton upLeft = new WidgetScrollerButton();
    WidgetScrollerButton downRight = new WidgetScrollerButton();

    WidgetScrollerThumbTray thumbTray;
    
    int buttonSize = WidgetScrollerButton.DEFAULT_SCROLLER_BUTTON_SIZE; 

    public WidgetScrollerAbstract(WidgetScrollerType type) {
        super();

        setLayout(new WidgetBorderLayout());

        this.type = type;

        thumbTray = new WidgetScrollerThumbTray(type);
        add(thumbTray, WidgetBorderLayoutConstraint.CENTER);
        thumbTray.addMouseDragObserver(this);

        if (this.type == WidgetScrollerType.VERTICAL) {
            
            add(upLeft, WidgetBorderLayoutConstraint.NORTH);
            add(downRight, WidgetBorderLayoutConstraint.SOUTH);
            
        } else if (this.type == WidgetScrollerType.HORIZONTAL) {

            add(upLeft, WidgetBorderLayoutConstraint.WEST);
            add(downRight, WidgetBorderLayoutConstraint.EAST);

        }
        
        upLeft.addMouseButtonDownObserver(this);
        upLeft.addMouseButtonUpObserver(this);
        
        downRight.addMouseButtonDownObserver(this);
        downRight.addMouseButtonUpObserver(this);

        upLeft.setPreferredSize(buttonSize, buttonSize);
        downRight.setPreferredSize(buttonSize, buttonSize);
    }

    public int getButtonSize() {
        return buttonSize;
    }

    public void setButtonSize(int i) {
        buttonSize = i;
        initButtonExtents();
        doLayout();
    }

    void initButtonExtents() {
        
        upLeft.setPreferredSize(buttonSize, buttonSize);
        downRight.setPreferredSize(buttonSize, buttonSize);

        thumbTray.initExtents();
        
        this.notifierScrollChange.notifyObservers(this);
    }

    public void setRangeExtents(float range, float visibleRange) {
        thumbTray.setRangeExtents(range, visibleRange);
        initButtonExtents();
        doLayout();
    }

    public int getRange() {
        return thumbTray.getRange();
    }

    public int getVisibleRange() {
        return thumbTray.getVisibleRange();
    }

    public int getOffset() {
        return thumbTray.getOffset();
    }

    public void setOffset(int i) {
        thumbTray.setOffset(i);
        initButtonExtents();
        doLayout();
    }

    public void update(Observable o, Object arg) {

        if (arg == upLeft && upLeft.isVisible()) {
    
            WidgetButtonStateType bs = ((WidgetButton)arg).getButtonState();
    
            if (bs == WidgetButtonStateType.BUTTON_DOWN) {
                
                thumbTray.decrement();
                decrementing = true;
                repeat.start();
                                
            } else if (bs == WidgetButtonStateType.BUTTON_UP) {
                decrementing = false;
            }
            
        } else if (arg == downRight && downRight.isVisible()) {
    
            WidgetButtonStateType bs = ((WidgetButton)arg).getButtonState();
    
            if (bs == WidgetButtonStateType.BUTTON_DOWN) {
                
                thumbTray.increment();
                incrementing = true;
                repeat.start();
                
            } else if (bs == WidgetButtonStateType.BUTTON_UP) {
                
                incrementing = false;
                
            }
            
        }
        
        this.notifierScrollChange.notifyObservers(this);

    }

    public void doLayout() {
        super.doLayout();
        initButtonExtents();
    }

    public void addScrollChangeObserver(Observer o) {
        this.notifierScrollChange.addObserver(o);
    }

    public void deleteScrollChangeObserver(Observer o) {
        this.notifierScrollChange.deleteObserver(o);
    }

    public void setDownRightButtonExpander(WidgetExpander expander) {
        downRight.setExpander(expander);
    }

    public void onDraw(Renderer r) {
        if (decrementing) {
            if (repeat.doRepeat()) {
                thumbTray.decrement();
            }
        } else if (incrementing) {
            if (repeat.doRepeat()) {
                thumbTray.increment();
            }
        }
        
        super.onDraw(r);
    }

}
