/* 
* Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding 
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
package com.jme.widget.slider;

import java.util.Observable;
import java.util.Observer;

import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.util.WidgetNotifier;

/**
 * <code>WidgetAbstractSlider</code>
 * @author Gregg Patton
 * @version $Id: WidgetAbstractSlider.java,v 1.1 2004-03-04 03:30:04 greggpatton Exp $
 */
public abstract class WidgetAbstractSlider extends WidgetPanel implements Observer {
    private float minimum = 0;
    private float maximum = 100;
    
    private boolean updating = false;
    
    private double value = 0;
    
    private WidgetOrientationType type;

    private WidgetSliderThumbTray thumbTray;

    protected WidgetNotifier notifierValueChange = new WidgetNotifier();

    /**
     * @param type
     */
    protected WidgetAbstractSlider(WidgetOrientationType type, WidgetOrientationType thumbOrientation) {
        super();

        setLayout(new WidgetBorderLayout());

        this.type = type;

        thumbTray = new WidgetSliderThumbTray(type);
        
        WidgetSliderThumb sliderThumb = new WidgetSliderThumb(thumbOrientation);
        thumbTray.setThumb(sliderThumb);
        thumbTray.calcThumbSize();
        
        add(thumbTray, WidgetBorderLayoutConstraint.CENTER);
        thumbTray.addMouseDragObserver(this);

        doLayout();
    }

    public void addValueChangeObserver(Observer o) {
        this.notifierValueChange.addObserver(o);
    }

    public void deleteValueChangeObserver(Observer o) {
        this.notifierValueChange.deleteObserver(o);
    }

    public void update(Observable o, Object arg) {
        
        updating = true;
        
        setValue(calcValue());
        
        updating = false;
        
    }
    
    /**
     * <code>getMinimum</code>
     * @return
     */
    public float getMinimum() {
        return minimum;
    }

    /**
     * <code>setMinimum</code>
     * @param f
     */
    public void setMinimum(float f) {
        minimum = f;
    }

    /**
     * <code>getMaximum</code>
     * @return
     */
    public float getMaximum() {
        return maximum;
    }

    /**
     * <code>setMaximum</code>
     * @param f
     */
    public void setMaximum(float f) {
        maximum = f;
    }

    /**
     * <code>getValue</code>
     * @return
     */
    public double getValue() {
        return value;
    }

    /**
     * <code>setValue</code>
     * @param f
     */
    public void setValue(double f) {
    
        f = clampValue(f);
    
        boolean changed = (f != value);
    
        value = f;

        if (!updating)    
            thumbTray.setThumbPos(calcThumbPostion());

        if (changed)
            this.notifierValueChange.notifyObservers(this);
            
    }

    private double calcThumbPostion() {
        double retVal = 0;

        double widgetSize = calcWidgetSize();
        
        retVal = (value - minimum) / ((maximum - minimum) / widgetSize);
        
        return retVal;
    }
        
    private double calcValue() {
        double retVal = 0;

        double pos =  thumbTray.getThumbPos();   

        double widgetSize = calcWidgetSize();
        
        retVal = (((maximum - minimum) / widgetSize) * pos) + minimum;
        
        return retVal;
    }
    
    private double calcWidgetSize() {
        double retVal = 1;
        
        WidgetInsets insets = getInsets();
        int wi = 0;
        
        WidgetBorder border = getBorder();
        int wb = 0;

        if (type == WidgetOrientationType.VERTICAL) {

            if (insets != null) {
                wi = insets.getTop() + insets.getBottom();
            }

            if (border != null) {
                wb = border.getTop() + border.getBottom();
            }
            
            retVal = getHeight() - thumbTray.getThumb().getHeight() - (wi + wb);

        } else if (type == WidgetOrientationType.HORIZONTAL) {

            if (insets != null) {
                wi = insets.getLeft() + insets.getRight();
            }

            if (border != null) {
                wb = border.getLeft() + border.getRight();
            }
            
            retVal = getWidth() - thumbTray.getThumb().getWidth() - (wi + wb);
        }
        
        return retVal;
    }
    
    private double clampValue(double v) {
        if (v >= maximum) {
            v = maximum;
        }
        else if (v <= minimum) {    
            v = minimum;
        }
    
        return v;
    }
}
