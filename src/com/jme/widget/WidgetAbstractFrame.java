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
package com.jme.widget;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.input.AbstractInputController;
import com.jme.input.MouseInput;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.font.WidgetFontManager;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.util.WidgetFrameRate;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetAbstractFrame extends WidgetAbstractContainer implements Observer {

    private static Timer timer;
    private static WidgetFrameRate frameRate;

    /**
     * @param ic
     */
    public WidgetAbstractFrame(AbstractInputController ic) {
        super();

        WidgetAbstractFrame.timer =
            Timer.getTimer(DisplaySystem.getDisplaySystem().getRendererType().getName());

        frameRate = new WidgetFrameRate(timer);

        setInputController(ic);

        init();
    }

    /**
     * <code>getFrameRate</code>
     * @return
     */
    public static WidgetFrameRate getFrameRate() {

        if (frameRate == null) {
            noInitMsg();
        }

        return frameRate;
    }

    /**
     * <code>getTimer</code>
     * @return
     */
    public static Timer getTimer() {

        if (timer == null) {
            noInitMsg();
        }

        return timer;
    }

    /**
     * <code>noInitMsg</code>
     * 
     */
    protected static void noInitMsg() {
        String msg = "WidgetAbstractFrame is not initialized.";
        LoggingSystem.getLogger().log(Level.WARNING, msg);
        throw new JmeException(msg);
    }

    /**
     * <code>init</code>
     * 
     */
    public void init() {

        WidgetFontManager.init();

        int width = DisplaySystem.getDisplaySystem().getWidth();
        int height = DisplaySystem.getDisplaySystem().getHeight();

        setViewRectangle(new WidgetViewRectangle(0, 0, width, height));

        setSize(width, height);

        doLayout();

    }

    /**
     * <code>destroy</code>
     * 
     */
    public static void destroy() {}

    /** <code>update</code> 
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {}

    /**
     * <code>handleInput</code>
     * 
     */
    public void handleInput() {
        handleInput(true, 0);
    }

    /**
     * <code>handleInput</code>
     * @param updateController
     */
    public void handleInput(boolean updateController) {
        handleInput(updateController, 0);
    }

    /**
     * <code>handleInput</code>
     * @param time
     */
    public void handleInput(float time) {
        handleInput(true, time);
    }

    /**
     * <code>handleInput</code>
     * @param updateController
     * @param time
     */
    public void handleInput(boolean updateController, float time) {

        if (updateController) {
            getInputController().update(time);
        }

        handleMouse();
        handleKeyboard();
    }

    /**
     * <code>handleKeyboard</code>
     * 
     */
    protected void handleKeyboard() {
        //super.handleKeyboard();
    }

    /**
     * <code>handleMouse</code>
     * 
     */
    protected void handleMouse() {
        MouseInput mi = getMouseInput();

        WidgetMouseButtonType buttonType = mi.getButtonState();
        WidgetMouseButtonType lastButtonType = mi.getPreviousButtonState();

        if (lastButtonType != buttonType) {

            if (buttonType != WidgetMouseButtonType.MOUSE_BUTTON_NONE) {

                handleMouseButtonDown();

            } else {

                handleMouseButtonUp();

            }
        }

        if (buttonType == WidgetMouseButtonType.MOUSE_BUTTON_NONE
            && (mi.getXDelta() != 0 || mi.getYDelta() != 0)) {

            handleMouseMove();

        } else if (
            buttonType != WidgetMouseButtonType.MOUSE_BUTTON_NONE
                && (mi.getXDelta() != 0 || mi.getYDelta() != 0)) {

            handleMouseDrag();
        }

        lastButtonType = buttonType;
    }

    /** <code>doMouseButtonDown</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#doMouseButtonDown()
     */
    public void doMouseButtonDown() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonDown();
        }
    }

    /** <code>handleMouseButtonDown</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseButtonDown()
     */
    public void handleMouseButtonDown() {
        super.handleMouseButtonDown();
        doMouseButtonDown();
    }

    /** <code>doMouseButtonUp</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#doMouseButtonUp()
     */
    public void doMouseButtonUp() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonUp();
        }
    }

    /** <code>handleMouseButtonUp</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseButtonUp()
     */
    public void handleMouseButtonUp() {
        super.handleMouseButtonUp();
        doMouseButtonUp();
    }

    /** <code>doMouseDrag</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#doMouseDrag()
     */
    public void doMouseDrag() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseDrag();
        }
    }

    /** <code>handleMouseDrag</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseDrag()
     */
    public void handleMouseDrag() {
        super.handleMouseDrag();
        doMouseDrag();
    }

    /** <code>handleMouseEnter</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseEnter()
     */
    public void handleMouseEnter() {
        super.handleMouseEnter();
        doMouseEnter();
    }

    /** <code>handleMouseExit</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseExit()
     */
    public void handleMouseExit() {
        super.handleMouseExit();
        doMouseExit();
    }

    /** <code>doMouseMove</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#doMouseMove()
     */
    public void doMouseMove() {
        Widget last = getLastWidgetUnderMouse();
        Widget w = getWidgetUnderMouse();

        if (last != w) {

            if (last != null)
                last.handleMouseExit();

            if (w != null)
                w.handleMouseEnter();

            setLastWidgetUnderMouse(w);
        }
    }

    /** <code>handleMouseMove</code> 
     * 
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#handleMouseMove()
     */
    public void handleMouseMove() {
        super.handleMouseMove();
        doMouseMove();
    }

    /** <code>onDraw</code> 
     * @param r
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {
        super.onDraw(r);

        timer.update();
    }

    /** <code>draw</code> 
     * @param r
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        if (isVisible() == false)
            return;

        super.draw(r);

        r.getCamera().update();
    }

    /** <code>initWidgetRenderer</code> 
     * 
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {}

}
