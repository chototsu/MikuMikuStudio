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
package com.jme.widget;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;
import com.jme.widget.bounds.WidgetViewport;
import com.jme.widget.font.WidgetFontManager;
import com.jme.widget.input.mouse.*;
import com.jme.widget.util.WidgetFrameRate;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetFrameAbstract extends WidgetContainerAbstract implements Observer {

    private static Timer timer;
    private static WidgetFrameRate frameRate;
    private static DisplaySystem displaySystem;

    public WidgetFrameAbstract(DisplaySystem ds, WidgetMouseStateAbstract mouseState, Timer timer) {
        super();

        displaySystem = ds;

        WidgetFrameAbstract.timer = timer;

        frameRate = new WidgetFrameRate(timer);

        WidgetImpl.setMouseState(mouseState);

        init();
    }

    public static DisplaySystem getDisplaySystem() {
        if (displaySystem == null) {
            noInitMsg();
        }

        return displaySystem;
    }

    public static WidgetFrameRate getFrameRate() {

        if (frameRate == null) {
            noInitMsg();
        }

        return frameRate;
    }

    public static Timer getTimer() {

        if (timer == null) {
            noInitMsg();
        }

        return timer;
    }

    protected static void noInitMsg() {
        String msg = "WidgetFrameAbstract is not initialized.";
        LoggingSystem.getLogger().log(Level.WARNING, msg);
        throw new JmeException(msg);
    }

    public void init() {

        WidgetImpl.getMouseState().init();

        WidgetFontManager.init(displaySystem.getRenderer());

        setViewport(new WidgetViewport(0, 0, displaySystem.getWidth(), displaySystem.getHeight()));

        setSize(displaySystem.getWidth(), displaySystem.getHeight());

        doLayout();

    }

    public static void destroy() {
        WidgetImpl.getMouseState().destroy();
    }

    public void update(Observable o, Object arg) {
    }

    public void handleKeyboard() {
        //super.handleKeyboard();
    }

    public void handleMouse() {
        WidgetMouseStateAbstract mouseState = WidgetImpl.getMouseState();

        mouseState.setState();

        if (mouseState.lastButtonType != mouseState.buttonType) {

            if (mouseState.buttonType != null) {

                handleMouseButtonDown();

            } else {

                handleMouseButtonUp();

            }
        }

        if (mouseState.buttonType == null && (mouseState.dx != 0 || mouseState.dy != 0)) {

            handleMouseMove();

        } else if (mouseState.buttonType != null && (mouseState.dx != 0 || mouseState.dy != 0)) {

            handleMouseDrag();
        }

        mouseState.lastButtonType = mouseState.buttonType;
    }

    public void doMouseButtonDown() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonDown();
        }
    }

    public void handleMouseButtonDown() {
        super.handleMouseButtonDown();
        doMouseButtonDown();
    }

    public void doMouseButtonUp() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseButtonDown();
        }
    }

    public void handleMouseButtonUp() {
        super.handleMouseButtonUp();
        doMouseButtonUp();
    }

    public void doMouseDrag() {
        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null && mouseOwner != this) {
            mouseOwner.doMouseDrag();
        }
    }

    public void handleMouseDrag() {
        super.handleMouseDrag();
        doMouseDrag();
    }

    public void handleMouseEnter() {
        super.handleMouseEnter();
        doMouseEnter();
    }

    public void handleMouseExit() {
        super.handleMouseExit();
        doMouseExit();
    }

    public void handleMouseMove() {
        super.handleMouseMove();
        doMouseMove();
    }

    public void onDraw(Renderer r) {
        super.onDraw(r);

        timer.update();
    }

    public void draw(Renderer r) {
        if (isVisible() == false)
            return;

        super.draw(r);

        r.getCamera().update();
    }

}
